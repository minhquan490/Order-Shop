export interface HttpServiceProvider {
  open(url: string): HttpService;
}

export interface HttpService {
  get<T, U>(request?: T, urlParams?: Map<string, string>): U;
  post<T, U>(request: T, urlParams?: Map<string, string>): U;
  put<T, U>(request: T, urlParams?: Map<string, string>): U;
  delete<T, U>(request?: T, urlParams?: Map<string, string>): U;
}
