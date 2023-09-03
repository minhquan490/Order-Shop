import {Authentication, ErrorResponse, QueryParam, Request, RequestHeader, Response} from "~/types";

export const useXmlHttpRequest = (request: Request, accessToken?: string, refreshToken?: string) => {

    function decorate(target: string, params: QueryParam[] | undefined): string {
        if (!params || params.length === 0) {
            return target;
        }
        let queryParam: string = "";
        params.forEach((param: QueryParam) => queryParam = queryParam.concat(`${param.name}=${param.value}&`))
        return `${target}?${queryParam.substring(0, queryParam.length - 1)}`;
    }

    const updateAccessToken = async (xmlRequest: XMLHttpRequest): Promise<void> => {
        const accessTokenHeader = useAppConfig().authorization;
        const accessToken: string = xmlRequest.getResponseHeader(accessTokenHeader);
        if (accessToken) {
            const {readAuth, updateAuth} = useAuthInformation();
            const auth: Authentication | undefined = await readAuth();
            if (auth && auth.accessToken !== accessToken) {
                const newAuth: Authentication = {
                    refreshToken: auth.refreshToken,
                    accessToken: accessToken
                };
                await updateAuth(newAuth);
            }
        }
        return Promise.resolve();
    }

    const updateCsrfToken = async (xmlRequest: XMLHttpRequest): Promise<void> => {
        const csrfTokenHeader = useAppConfig().csrfToken;
        const csrf: string = xmlRequest.getResponseHeader(csrfTokenHeader);
        if (csrf) {
            const {readCsrf, updateCsrf} = useAuthInformation();
            const csrfToken: string | undefined = await readCsrf();
            if (csrfToken) {
                if (csrfToken !== csrf) {
                    await updateCsrf(csrf);
                }
            } else {
                await updateCsrf(csrf);
            }
        }
    }

    function createResponse<T>(xmlRequest: XMLHttpRequest): Response<T> {
        // @formatter:off
        updateAccessToken(xmlRequest).then((): void => {});
        updateCsrfToken(xmlRequest).then((): void => {});
        // @formatter:on
        return {
            statusCode: xmlRequest.status,
            body: JSON.parse(xmlRequest.responseText),
            isError: xmlRequest.status >= 400
        }
    }

    function createConnectionErrorResponse(xmlRequest: XMLHttpRequest): Response<ErrorResponse> {
        // @formatter:off
        updateAccessToken(xmlRequest).then((): void => {});
        updateCsrfToken(xmlRequest).then((): void => {});
        // @formatter:on
        return {
            statusCode: 418,
            body: {
                messages: ['Checking your connection and try again']
            },
            isError: true
        }
    }

    async function sendAsync<T>(method: string): Promise<Response<T>> {
        const {applyCommonHeader, applyAuthenticationHeader, applyCsrfHeader} = useRequestDecorator();
        applyCommonHeader(request);
        applyAuthenticationHeader(request, accessToken, refreshToken);
        await applyCsrfHeader(request);
        const url: string = decorate(request.apiUrl, request.queryParams);
        const xhr: XMLHttpRequest = new XMLHttpRequest();
        xhr.open(method, url);
        request.headers?.forEach((header: RequestHeader) => xhr.setRequestHeader(header.name, header.value));
        if (!request.body) {
            xhr.send();
        } else {
            xhr.send(JSON.stringify(request.body));
        }
        return new Promise<Response<T>>((resolve): void => {
            xhr.onload = (): void => {
                if (xhr.status >= 200 && xhr.status < 400) {
                    resolve(createResponse<T>(xhr));
                } else {
                    resolve(createResponse<T>(xhr))
                }
            };
            xhr.onerror = () => resolve(createConnectionErrorResponse(xhr));
        });
    }

    const getAsyncCall = async <T>(): Promise<Response<T>> => {
        return await sendAsync<T>('GET');
    }

    const postAsyncCall = async <T>(): Promise<Response<T>> => {
        return await sendAsync<T>('POST');
    }

    const patchAsyncCall = async <T>(): Promise<Response<T>> => {
        return await sendAsync('PATCH');
    }

    const deleteAsyncCall = async <T>(): Promise<Response<T>> => {
        return await sendAsync('DELETE');
    }

    return {
        getAsyncCall,
        postAsyncCall,
        deleteAsyncCall,
        patchAsyncCall
    }
}
