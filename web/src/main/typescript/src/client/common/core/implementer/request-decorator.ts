import { Decorator } from '@core/decorator';
import { Strategy } from '@core/strategy';

export class RequestDecorator extends Decorator<XMLHttpRequest, Strategy<XMLHttpRequest>> {
  override decorate(target: XMLHttpRequest, param: Strategy<XMLHttpRequest>): XMLHttpRequest {
    return param.applyStrategy(target);
  }

}
