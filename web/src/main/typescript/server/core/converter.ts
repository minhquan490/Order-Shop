export abstract class Converter<T, U> {
  abstract convert(target: U): T;
}
