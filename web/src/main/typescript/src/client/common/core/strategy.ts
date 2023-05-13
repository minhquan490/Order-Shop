export abstract class Strategy<T> {
  abstract applyStrategy(target: T): T;
}
