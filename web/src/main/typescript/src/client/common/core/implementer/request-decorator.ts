import { Decorator } from '@client/core/decorator';
import { Strategy } from '@client/core/strategy';

export class RequestDecorator extends Decorator<XMLHttpRequest, Strategy<XMLHttpRequest>> {
  override decorate(target: XMLHttpRequest, param: Strategy<XMLHttpRequest>): XMLHttpRequest {
    return param.applyStrategy(target);
  }

}
