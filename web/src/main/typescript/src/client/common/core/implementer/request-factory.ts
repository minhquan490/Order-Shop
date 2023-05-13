import { Factory } from "@core/factory";

export class RequestFactory implements Factory<XMLHttpRequest> {
  getInstance(): XMLHttpRequest;
  getInstance(params: Object[]): XMLHttpRequest;
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  getInstance(params?: unknown): XMLHttpRequest {
    return new XMLHttpRequest();
  }

}
