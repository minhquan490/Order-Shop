import { Converter } from "../../server/core/converter";


export class JsonStringConverter extends Converter<string, any> {
  convert(target: any): string {
    return JSON.stringify(target);
  }
}

export class ObjectConverter extends Converter<any, string> {
  convert(target: string): any {
    if (target.length === 0) {
      return {};
    }
    return JSON.parse(target);
  }
}
