import { Decorator } from "../decorator";
import { Strategy } from "../strategy";

export class RequestDecorator extends Decorator<XMLHttpRequest, Strategy<XMLHttpRequest>> {

  constructor() {
    super();
  }

  override decorate(target: XMLHttpRequest, param: Strategy<XMLHttpRequest>): XMLHttpRequest {
    target.setRequestHeader("Content-Type", "application/json");
    return param.applyStrategy(target);
  }
}
