import { Decorator } from "../decorator";
import { Strategy } from "../strategy";

export class RequestDecorator extends Decorator<XMLHttpRequest, Strategy<XMLHttpRequest>> {
  override decorate(target: XMLHttpRequest, param: Strategy<XMLHttpRequest>): XMLHttpRequest {
    return param.applyStrategy(target);
  }

}
