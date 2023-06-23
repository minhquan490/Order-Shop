import { ErrorResponse } from "~/types/error-response.type";
import { ResponseWrapper } from "~/types/response-wrapper.type";

export interface HttpServiceProvider {
  open(url: string): HttpService;
}

export interface HttpService {
  get<T, U>(request?: T, urlParams?: Map<string, string>): ResponseWrapper<U | ErrorResponse | null>;
  post<T, U>(request: T, urlParams?: Map<string, string>): ResponseWrapper<U | ErrorResponse | null>;
  put<T, U>(request: T, urlParams?: Map<string, string>): ResponseWrapper<U | ErrorResponse | null>;
  delete<T, U>(request?: T, urlParams?: Map<string, string>): ResponseWrapper<U | ErrorResponse | null>;
}
