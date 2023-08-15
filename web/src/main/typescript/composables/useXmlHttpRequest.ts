import {Authentication, ErrorResponse, QueryParam, Request, Response} from "~/types";

export const useXmlHttpRequest = (request: Request, accessToken?: string, refreshToken?: string) => {

    function decorate(target: string, params: QueryParam[] | undefined): string {
        if (!params) {
            return target;
        }
        let queryParam: string = "";
        params.forEach((param: QueryParam) => queryParam = queryParam.concat(`${param.name}=${param.value}&`))
        return `${target}?${queryParam.substring(0, queryParam.length - 1)}`;
    }

    function createResponse<T>(xmlRequest: XMLHttpRequest): Response<T> {
        const accessTokenHeader = useAppConfig().authorization;
        const accessToken: string = xmlRequest.getResponseHeader(accessTokenHeader);
        if (accessToken) {
            const {readAuth, updateAuth} = useAuthInformation();
            readAuth().then(auth => {
                if (auth && auth.accessToken !== accessToken) {
                    const newAuth: Authentication = {
                        refreshToken: auth.refreshToken,
                        accessToken: accessToken
                    };
                    return updateAuth(newAuth);
                }
            });
        }
        return {
            statusCode: xmlRequest.status,
            body: JSON.parse(xmlRequest.responseText)
        }
    }

    function createConnectionErrorResponse(): Response<ErrorResponse> {
        return {
            statusCode: 418,
            body: {
                messages: ['Checking your connection and try again']
            }
        }
    }

    async function sendAsync<T>(method: string): Promise<Response<T>> {
        return new Promise<Response<T>>((resolve, reject): void => {
            const {applyCommonHeader, applyAuthenticationHeader} = useRequestDecorator();
            applyCommonHeader(request);
            applyAuthenticationHeader(request, accessToken, refreshToken);
            const url: string = decorate(request.apiUrl, request.queryParams);
            const xhr: XMLHttpRequest = new XMLHttpRequest();
            xhr.open(method, url);
            request.headers?.forEach(header => xhr.setRequestHeader(header.name, header.value));
            xhr.onload = (): void => {
                if (xhr.status >= 200 && xhr.status < 400) {
                    resolve(createResponse<T>(xhr));
                } else {
                    reject(createResponse<T>(xhr))
                }
            };
            xhr.onerror = () => reject(createConnectionErrorResponse());
            if (!request.body) {
                xhr.send();
            } else {
                xhr.send(JSON.stringify(request.body));
            }
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
