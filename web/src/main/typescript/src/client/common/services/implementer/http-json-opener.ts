import { Converter } from '@core/converter';
import { Decorator } from '@core/decorator';
import { Factory } from '@core/factory';
import { RequestDecorator } from '@core/implementer/request-decorator';
import { RequestFactory } from '@core/implementer/request-factory';
import { RequestHeaderStrategy } from '@core/implementer/request-header-strategy';
import { UrlDecorator } from '@core/implementer/url-decorator';
import { Strategy } from '@core/strategy';
import type { HttpService, HttpServiceProvider } from '@services/http-service';

export class HttpJsonOpener implements HttpServiceProvider {
  private jsonConverter: Converter<string, any>;
  private objectConverter: Converter<any, string>;
  private requestDecorator: Decorator<XMLHttpRequest, Strategy<XMLHttpRequest>>;
  private requestFactory: Factory<XMLHttpRequest>;
  private urlDecorator: Decorator<string, Map<string, string>>;
  private requestHeaderStrategy: Strategy<XMLHttpRequest>;

  constructor(jsonConverter: Converter<string, any>, objectConverter: Converter<any, string>) {
    this.jsonConverter = jsonConverter;
    this.objectConverter = objectConverter;
    this.requestDecorator = new RequestDecorator();
    this.requestFactory = new RequestFactory();
    this.urlDecorator = new UrlDecorator();
    this.requestHeaderStrategy = new RequestHeaderStrategy();
  }

  open(url: string): HttpService {
    return new InternalHttpService(url, this.jsonConverter, this.objectConverter, this.requestDecorator, this.requestFactory, this.urlDecorator, this.requestHeaderStrategy);
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

  constructor(
    url: string,
    jsonConverter: Converter<string, any>,
    objectConverter: Converter<any, string>,
    requestDecorator: Decorator<XMLHttpRequest, Strategy<XMLHttpRequest>>,
    requestFactory: Factory<XMLHttpRequest>,
    urlDecorator: Decorator<string, Map<string, string>>,
    requestHeaderStrategy: Strategy<XMLHttpRequest>
  ) {
    this.url = url;
    this.jsonConverter = jsonConverter;
    this.objectConverter = objectConverter;
    this.requestDecorator = requestDecorator;
    this.requestFactory = requestFactory;
    this.urlDecorator = urlDecorator;
    this.requestHeaderStrategy = requestHeaderStrategy;
  }

  get<T, U>(request?: T, urlParams?: Map<string, string>): U {
    return this.sendRequest('GET', request, urlParams);
  }
  post<T, U>(request: T, urlParams?: Map<string, string>): U {
    return this.sendRequest('POST', request, urlParams);
  }
  put<T, U>(request: T, urlParams?: Map<string, string>): U {
    return this.sendRequest('PUT', request, urlParams);
  }
  delete<T, U>(request?: T, urlParams?: Map<string, string>): U {
    return this.sendRequest('DELETE', request, urlParams);
  }

  private sendRequest<T, U>(method: string, request?: T, urlParams?: Map<string, string>): U {
    let req = this.requestFactory.getInstance();
    const path = this.createPath(this.url, urlParams);
    req.open(method, path);
    req = this.requestDecorator.decorate(req, this.requestHeaderStrategy);
    if (request) {
      const requestString = this.jsonConverter.convert(request);
      req.setRequestHeader("Content-Type", "application/json");
      req.setRequestHeader("Content-Length", requestString.length.toString());
      req.send();
    } else {
      req.send();
    }
    return this.objectConverter.convert(req.response);
  }

  private createPath(path: string, urlParams?: Map<string, string>): string {
    if(urlParams) {
      return this.urlDecorator.decorate(path, urlParams);
    } else {
      return path;
    }
  }
}
