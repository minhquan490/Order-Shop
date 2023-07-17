export const useAppStorage = () => {
  if (process.server) {
    // Use mongo in the future
    return ref();
  }
  return ref(localStorage);
}
