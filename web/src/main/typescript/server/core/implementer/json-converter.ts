import { Converter } from "../converter";


export class JsonStringConverter extends Converter<string, any> {
  convert(target: any): string {
    return JSON.stringify(target);
  }
}

export class ObjectConverter extends Converter<any, string> {
  convert(target: string): any {
    return JSON.parse(target);
  }
}
