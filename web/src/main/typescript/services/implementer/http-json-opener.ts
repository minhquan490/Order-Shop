import { Converter } from "~/server/core/converter";
import { Decorator } from "~/server/core/decorator";
import { Factory } from "~/server/core/factory";
import { RequestDecorator } from "~/server/core/implementer/request-decorator";
import { RequestFactory } from "~/server/core/implementer/request-factory";
import { RequestHeaderStrategy } from "~/server/core/implementer/request-header-strategy";
import { UrlDecorator } from "~/server/core/implementer/url-decorator";
import { Strategy } from "~/server/core/strategy";
import { ErrorResponse } from "~/types/error-response.type";
import { ResponseWrapper } from "~/types/response-wrapper.type";
import { HttpService, HttpServiceProvider } from "../http.service";

export class HttpJsonOpener implements HttpServiceProvider {
  private singleton: InternalHttpService;

  constructor(jsonConverter: Converter<string, any>, objectConverter: Converter<any, string>) {
    this.singleton = new InternalHttpService(jsonConverter, objectConverter, new RequestDecorator(), new RequestFactory(), new UrlDecorator(), new RequestHeaderStrategy());
  }

  open(url: string): HttpService {
    this.singleton.setUrl(url);
    return this.singleton;
  }
}

class InternalHttpService implements HttpService {
  private url: string;
  private jsonConverter: Converter<string, any>;
  private objectConverter: Converter<any, string>;
  private requestDecorator: Decorator<XMLHttpRequest, Strategy<XMLHttpRequest>>;
  private requestFactory: Factory<XMLHttpRequest>;
  private urlDecorator: Decorator<string, Map<string, string>>;
  private requestHeaderStrategy: Strategy<XMLHttpRequest>;
  private accessTokenKey: string;
  private storage?: Storage;

  constructor(
    jsonConverter: Converter<string, any>,
    objectConverter: Converter<any, string>,
    requestDecorator: Decorator<XMLHttpRequest, Strategy<XMLHttpRequest>>,
    requestFactory: Factory<XMLHttpRequest>,
    urlDecorator: Decorator<string, Map<string, string>>,
    requestHeaderStrategy: Strategy<XMLHttpRequest>
  ) {
    this.jsonConverter = jsonConverter;
    this.objectConverter = objectConverter;
    this.requestDecorator = requestDecorator;
    this.requestFactory = requestFactory;
    this.urlDecorator = urlDecorator;
    this.requestHeaderStrategy = requestHeaderStrategy;
    this.url = ''
    this.accessTokenKey = useAppConfig().authorization;
    this.storage = useAppStorage().value;
  }

  setUrl(url: string) {
    this.url = url;
  }

  get<T, U>(request?: T, urlParams?: Map<string, string>): ResponseWrapper<U | ErrorResponse | null> {
    return this.sendRequest('GET', request, urlParams);
  }
  post<T, U>(request: T, urlParams?: Map<string, string>): ResponseWrapper<U | ErrorResponse | null> {
    return this.sendRequest('POST', request, urlParams);
  }
  put<T, U>(request: T, urlParams?: Map<string, string>): ResponseWrapper<U | ErrorResponse | null> {
    return this.sendRequest('PUT', request, urlParams);
  }
  delete<T, U>(request?: T, urlParams?: Map<string, string>): ResponseWrapper<U | ErrorResponse | null> {
    return this.sendRequest('DELETE', request, urlParams);
  }

  private sendRequest<T, U>(method: string, request?: T, urlParams?: Map<string, string>): ResponseWrapper<U | ErrorResponse | null> {
    let req = this.requestFactory.getInstance();
    req.timeout = 3000;
    const path = this.createPath(this.url, urlParams);
    req.open(method, path);
    if (!(typeof request === 'undefined')) {
      const requestString = this.jsonConverter.convert(request);
      req = this.requestDecorator.decorate(req, this.requestHeaderStrategy);
      req.send(requestString);
    } else {
      req.send();
    }
    this.resetAccessToken(req);
    return this.createResponse(req);
  }

  private createPath(path: string, urlParams?: Map<string, string>): string {
    if(urlParams) {
      return this.urlDecorator.decorate(path, urlParams);
    } else {
      return path;
    }
  }

  private resetAccessToken(req: XMLHttpRequest) {
    const accessToken = req.getResponseHeader(this.accessTokenKey);
    if (this.storage && accessToken) {
      this.storage.removeItem(this.accessTokenKey);
      this.storage.setItem(this.accessTokenKey, accessToken);
    }
  }

  private createResponse<U>(req: XMLHttpRequest): ResponseWrapper<U | ErrorResponse | null> {
    const contentType = req.getResponseHeader('Content-Type');
    if (contentType !== null && contentType === 'application/json') {
      return new ResponseWrapper<U>(req.status, this.objectConverter.convert(req.response));
    } else {
      return new ResponseWrapper(req.status, null);
    }
  }
}
