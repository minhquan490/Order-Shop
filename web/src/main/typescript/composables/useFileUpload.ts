import {ErrorResponse, FileUploadRequestBody, Request, RequestHeader, Response} from "~/types";

export const useFileUpload = () => {

    const upload = async (request: Request, accessToken?: string, refreshToken?: string): Promise<Response<FileUploadResponse>> => {
        if (!(request.body as FileUploadRequestBody).content) {
            throw new Error("Request body is required !");
        }
        const {applyCommonHeader, applyAuthenticationHeader} = useRequestDecorator();
        applyCommonHeader(request);
        applyAuthenticationHeader(request, accessToken, refreshToken);
        return new Promise<Response<FileUploadResponse>>((resolve, reject): void => {
            const fileUploadRequest: FileUploadRequestBody = request.body;
            const formData: FormData = new FormData();
            formData.append('file-name', fileUploadRequest.fileName);
            formData.append('content-type', fileUploadRequest.contentType);
            formData.append('content-length', fileUploadRequest.contentLength.toString());
            formData.append('is-chunked', fileUploadRequest.isChunked.toString());
            formData.append('content', fileUploadRequest.content as Blob);
            const xhr: XMLHttpRequest = new XMLHttpRequest();
            xhr.open('POST', request.apiUrl);
            request.headers?.forEach((header: RequestHeader) => xhr.setRequestHeader(header.name, header.value));
            xhr.onload = (): void => {
                if (xhr.status >= 200 && xhr.status < 400) {
                    resolve(createResponse<FileUploadResponse>(xhr));
                } else {
                    reject(createResponse<FileUploadResponse>(xhr));
                }
            };
            xhr.onerror = () => reject(createConnectionErrorResponse());
            xhr.send(formData);
        });
    }

    const uploadSingleFile = async (file: File, productId: string, accessToken?: string, refreshToken?: string): Promise<Response<FileUploadResponse>> => {
        const serverUrl: string = useAppConfig().serverUrl;
        const uploadApi: string = `${serverUrl}/admin/files/upload`;
        const maxFileSize: number = 100000; // 100Kb
        if (file.size > maxFileSize) {
            const partNumber: number = file.size / maxFileSize;
            const range: number = Math.floor(partNumber);
            for (let i: number = 0; i < range; i++) {
                const request: Request = createRequest(uploadApi, file, i * maxFileSize, (i + 1) * maxFileSize, `${productId}-${file.name}-${i + 1}`, true);
                try {
                    const response: Response<FileUploadResponse> = await doUpload(request, accessToken, refreshToken);
                    if ((!response.isError) && ((i + 1) === range) && (partNumber - range > 0)) {
                        const remaining: number = partNumber - Math.floor(partNumber);
                        return uploadLittleFile(uploadApi, file, remaining, file.size, productId, serverUrl, accessToken, refreshToken);
                    }
                } catch (error) {
                    return Promise.reject<Response<FileUploadResponse>>(error);
                }
            }
        } else {
            return uploadLittleFile(uploadApi, file, 0, file.size, productId, serverUrl, accessToken, refreshToken);
        }
    }

    const uploadMultiFile = async (fileList: FileList, productId: string, accessToken?: string, refreshToken?: string): Promise<void> => {
        for (const file: File of fileList) {
            const response: Response<FileUploadResponse> = await uploadSingleFile(file, productId, accessToken, refreshToken);
            if (response.isError) {
                break;
            }
        }
    }

    const uploadLittleFile = async (uploadApi: string, file: File, startSize: number, endSize: number, productId: string, serverUrl: string, accessToken?: string, refreshToken?: string) => {
        const request: Request = createRequest(uploadApi, file, startSize, endSize, `${productId}-${file.name}`, false);
        const response: Response<FileUploadResponse> = await doUpload(request, accessToken, refreshToken);
        if (response.isError) {
            return Promise.reject<Response<FileUploadResponse>>(response);
        } else {
            return sendFlushRequest(productId, file, serverUrl, accessToken, refreshToken);
        }
    }

    const sendFlushRequest = async (productId: string, file: File, serverUrl: string, accessToken?: string, refreshToken?: string): Promise<Response<FileUploadResponse>> => {
        const flushFileApi: string = `${serverUrl}/admin/files/flush`;
        const flushFileRequestBody: FlushFileRequest = {
            product_id: productId,
            content_type: file.type,
            file_name: file.name
        }
        const flushRequest: Request = {
            apiUrl: flushFileApi,
            body: flushFileRequestBody
        }
        const {postAsyncCall} = useXmlHttpRequest(flushRequest, accessToken, refreshToken);
        const response: Response<any> = await postAsyncCall<any>();
        if (response.isError) {
            response.body = {
                status: response.statusCode,
                messages: ['Can not upload file']
            } as FileUploadResponse;
            return Promise.reject<Response<FileUploadResponse>>(response);
        } else {
            response.body = {
                status: response.statusCode,
                messages: ['Upload file success']
            } as FileUploadResponse;
            return Promise.resolve<Response<FileUploadResponse>>(response);
        }
    }

    const doUpload = async (request: Request, accessToken?: string, refreshToken?: string): Promise<Response<FileUploadResponse>> => {
        try {
            const response: Response<FileUploadResponse> = await upload(request, accessToken, refreshToken);
            return Promise.resolve(response);
        } catch (e) {
            const errorResponse: Response<ErrorResponse> = e;
            const uploadResponse: FileUploadResponse = {
                status: errorResponse.statusCode,
                messages: errorResponse.body.messages
            }
            return Promise.reject<Response<FileUploadResponse>>(uploadResponse);
        }
    }

    const createRequest = (uploadApi: string, file: File, startSize: number, endSize: number, fileName: string, isChunked: boolean): Request => {
        const request: Request = {
            apiUrl: uploadApi
        }
        const data: Blob = file.slice(startSize, endSize, file.type);
        request.body = {
            fileName: fileName,
            contentLength: data.size,
            isChunked: isChunked,
            contentType: file.type,
            content: data
        };
        return request;
    }

    const createResponse = (xhr: XMLHttpRequest): Response<FileUploadResponse> => {
        return {
            statusCode: xhr.status,
            body: JSON.parse(xhr.responseText)
        } as Response<FileUploadResponse>
    }

    function createConnectionErrorResponse(): Response<ErrorResponse> {
        return {
            statusCode: 418,
            body: {
                messages: ['Checking your connection and try again']
            },
            isError: true
        }
    }

    return {
        uploadSingleFile,
        uploadMultiFile
    };
}

type FileUploadResponse = {
    status: number,
    messages: string[]
}

type FlushFileRequest = {
    product_id: string,
    file_name: string,
    content_type: string
}