export type NavBarsSource = {
    name: string,
    href: string,
    current: boolean
}

export type RequestHeader = {
    name: string,
    value: string
}

export type QueryParam = {
    name: string,
    value: string
}

export type Request = {
    apiUrl: string,
    queryParams?: Array<QueryParam>,
    headers?: Array<RequestHeader>,
    body?: any
}

export type Response<T> = {
    statusCode: number,
    body?: ErrorResponse | T,
    isError: boolean
}

export type ErrorResponse = {
    messages: Array<string>
}

export type TableHeader = {
    name: string,
    dataPropertyName: string
}

export type TableData = Record<string, any>

export type TableNewData = {
    icon: string,
    buttonContent: string
}

export type TableFilter = {
    title: string,
    categories: Array<string>
}

export class AppError {
    message?: string;
    stack?: string
    statusCode?: number;
    statusMessage?: string
    url?: string
}

export class TableAction {
    callback!: Function;
    name!: string;
}

export type Category = {
    id: string,
    name: string
}

export type AdminProduct = {
    id: string,
    name: string,
    price: string,
    size: string,
    color: string,
    taobao_url: string,
    description: string,
    orderPoint: string,
    enable: boolean
    pictures: string[],
    categories: string[]
}

export type CarouselItem = {
    pictureUrl: string,
    current: boolean
}

export type FileUploadRequestBody = {
    fileName: string,
    contentType: string,
    contentLength: number,
    isChunked: boolean,
    content?: Blob
}

export type Authentication = {
    accessToken: string,
    refreshToken: string
}

export type BasicCustomerInfo = {
    id: string,
    firstName: string,
    lastName: string,
    role: string,
    avatarUrl: string
}

export type Province = {
    id: string,
    name: string
}

export type District = {
    id: string,
    name: string
}

export type Ward = {
    id: string,
    name: string
}