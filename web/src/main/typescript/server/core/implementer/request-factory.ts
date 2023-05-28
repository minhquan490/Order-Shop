import { Factory } from "../factory";

export class RequestFactory implements Factory<XMLHttpRequest> {

  constructor() {}

  getInstance(): XMLHttpRequest;
  getInstance(params: Object[]): XMLHttpRequest;
  getInstance(params?: unknown): XMLHttpRequest {
    return new XMLHttpRequest();
  }
  
}
