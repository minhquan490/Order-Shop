import { ErrorResponse, QueryParam, Request, RequestHeader, Response } from "~/types";

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

    function send<T>(method: string): Response<T> {
        const {applyCommonHeader, applyAuthenticationHeader} = useRequestDecorator();
        applyCommonHeader(request);
        applyAuthenticationHeader(request, accessToken, refreshToken);
        const xmlHttpRequest: XMLHttpRequest = new XMLHttpRequest();
        const url: string = decorate(request.apiUrl, request.queryParams);
        xmlHttpRequest.open(method, url);
        request.headers?.forEach((header: RequestHeader) => xmlHttpRequest.setRequestHeader(header.name, header.value));
        try {
            if (!request.body) {
                xmlHttpRequest.send();
            } else {
                xmlHttpRequest.send(JSON.stringify(request.body));
            }
        } catch (e) {
            return createConnectionErrorResponse();
        }
        return createResponse(xmlHttpRequest);
    }

    async function sendAsync<T>(method: string): Promise<Response<T>> {
        return new Promise<Response<T>>((resolve, reject): void => {
            const {applyCommonHeader, applyAuthenticationHeader} = useRequestDecorator();
            applyCommonHeader(request);
            applyAuthenticationHeader(request, accessToken, refreshToken);
            const url: string = decorate(request.apiUrl, request.queryParams);
            const xhr: XMLHttpRequest = new XMLHttpRequest();
            xhr.open(method, url);
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

    const getCall = <T>(): Response<T> => {
        return send<T>('GET');
    }

    const patchCall = <T>(): Response<T> => {
        return send<T>('PATCH');
    }

    const postCall = <T>(): Response<T> => {
        return send<T>('POST');
    }

    const deleteCall = <T>(): Response<T> => {
        return send<T>('DELETE');
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
        deleteCall,
        postCall,
        patchCall,
        getCall,
        getAsyncCall,
        postAsyncCall,
        deleteAsyncCall,
        patchAsyncCall
    }
}
