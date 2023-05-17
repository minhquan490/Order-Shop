import { Decorator } from "../decorator";

export class UrlDecorator extends Decorator<string, Map<string, string>> {
  override decorate(target: string, param: Map<string, string>): string {
    let queryParam = "";
    param.forEach((value: string, key: string) => queryParam = queryParam.concat(`${key}=${value}&`));
    return `${target}?${queryParam.substring(0, queryParam.length - 1)}`;
  }
}
