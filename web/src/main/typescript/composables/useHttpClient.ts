import { HttpService, HttpServiceProvider } from "~/services/http-service";

export const useHttpClient = () => {
  const httpClientProvider: HttpServiceProvider = inject('httpClient') as HttpServiceProvider;

  function httpClient(url: string): HttpService {
    return httpClientProvider.open(url);
  }

  return { httpClient };
}
