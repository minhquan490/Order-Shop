export const useLocalStorage = () => {

    const localStorage: Storage = window.localStorage;

    const clearStorage = (): void => {
        localStorage.clear();
    }

    const setValue = (key: string, value: string): void => {
        localStorage.setItem(key, value);
    }

    const setValues = (values: Array<{ key: string, value: string }>): void => {
        values.forEach(value => setValue(value.key, value.value));
    }

    const getValue = (key: string): string | null => {
        return localStorage.getItem(key);
    }

    return {
        localStorage,
        clearStorage,
        setValue,
        setValues,
        getValue
    }
}
