export const useAppStorage = () => {
  if (process.server) {
    // Use mongo in the future
    return ref();
  }
  const storage = localStorage;
  return ref(storage);
}
