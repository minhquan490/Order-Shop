export abstract class Decorator<T, U> {
  abstract decorate(target: T, param: U): T;
}
