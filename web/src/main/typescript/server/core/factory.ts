export abstract class Factory<T> {
  abstract getInstance(): T;

  abstract getInstance(params: Array<Object>): T;
}
