import { Authentication } from "~/types";

export const useAuthInformation = () => {
    const databaseName: string = 'Order-Shop';
    const collectionName: string = 'auth';
    const authKey: string = 'user';
    const csrfKey: string = "_csrf";
    const versionNumber: number = 1;

    const openConnection = async (): Promise<IDBDatabase> => {
        return new Promise<IDBDatabase>((resolve, reject): void => {
            const databaseRequest: IDBOpenDBRequest = window.indexedDB.open(databaseName, versionNumber);
            databaseRequest.onupgradeneeded = (): void => {
                const database: IDBDatabase = databaseRequest.result;
                database.createObjectStore(collectionName, {
                    autoIncrement: true
                })
            };
            databaseRequest.onsuccess = (event: Event) => resolve((event.target as IDBOpenDBRequest).result);
            databaseRequest.onerror = (event: Event) => reject((event.target as IDBOpenDBRequest).result);
            databaseRequest.onblocked = (event: Event) => reject((event.target as IDBOpenDBRequest).result);
        });
    }

    const resolveReadAuth = (data: IDBRequest<Authentication>): Promise<Authentication | undefined> => {
        return new Promise<Authentication | undefined>((resolve) => {
            data.onsuccess = () => resolve(data.result);
            data.onerror = () => resolve(undefined);
        });
    }

    const readAuth = async (): Promise<Authentication | undefined> => {
        const database: IDBDatabase = await openConnection();
        try {
            const data: IDBRequest<Authentication> = database.transaction(collectionName, "readonly").objectStore(collectionName).get(authKey);
            const result: Authentication | undefined = await resolveReadAuth(data);
            return Promise.resolve(result);
        } catch (error) {
            return Promise.resolve(undefined);
        }
    }

    const storeAuth = async (auth: Authentication): Promise<boolean> => {
        const database: IDBDatabase = await openConnection();
        try {
            database.transaction(collectionName, "readwrite").objectStore(collectionName).add(auth, authKey);
            database.close();
            return Promise.resolve(true);
        } catch (error) {
            database.close();
            return Promise.resolve(false);
        }
    }

    const removeAuth = async (): Promise<boolean> => {
        const database: IDBDatabase = await openConnection();
        try {
            database.transaction(collectionName, "readwrite").objectStore(collectionName).delete(authKey);
            database.close();
            return Promise.resolve(true);
        } catch (error) {
            database.close();
            return Promise.resolve(false);
        }
    }

    const updateAuth = async (newAuth: Authentication): Promise<boolean | undefined> => {
        const database: IDBDatabase = await openConnection();
        const auth: Authentication | undefined = await readAuth();
        if (auth) {
            if (auth.accessToken !== newAuth.accessToken && auth.refreshToken !== newAuth.refreshToken) {
                const objectStore: IDBObjectStore = database.transaction(collectionName, "readwrite").objectStore(collectionName);
                const result: boolean = await resolveAuthUpdate(objectStore, newAuth);
                return Promise.resolve(result);
            }
        } else {
            const result: boolean = await storeAuth(newAuth);
            return Promise.resolve(result);
        }
    }

    const resolveAuthUpdate = async (objectStore: IDBObjectStore, newAuth: Authentication): Promise<boolean> => {
        return new Promise<boolean>((resolve) => {
            const request: IDBRequest<IDBValidKey> = objectStore.put(newAuth, authKey);
            request.onsuccess = () => resolve(true);
            request.onerror = () => resolve(false);
        });
    }

    const storeCsrf = async (csrfToken: string): Promise<boolean> => {
        const database: IDBDatabase = await openConnection();
        try {
            database.transaction(collectionName, "readwrite").objectStore(collectionName).add(csrfToken, csrfKey);
            database.close();
            return Promise.resolve(true);
        } catch (error) {
            database.close();
            return Promise.resolve(false);
        }
    }

    const readCsrf = async (): Promise<string | undefined> => {
        const database: IDBDatabase = await openConnection();
        try {
            const data: IDBRequest<string> = database.transaction(collectionName, "readonly").objectStore(collectionName).get(csrfKey);
            const result: string | undefined = await resolveReadCsrf(data);
            return Promise.resolve(result);
        } catch (error) {
            return Promise.resolve(undefined);
        }
    }

    const updateCsrf = async (newCsrfToken: string): Promise<boolean> => {
        const database: IDBDatabase = await openConnection();
        const csrfToken: string | undefined = await readCsrf();
        if (csrfToken) {
            const objectStore: IDBObjectStore = database.transaction(collectionName, "readwrite").objectStore(collectionName);
            const result: boolean = await resolveCsrfUpdate(objectStore, newCsrfToken);
            return Promise.resolve(result);
        } else {
            const result: boolean = await storeCsrf(newCsrfToken);
            return Promise.resolve(result);
        }
    }

    const resolveCsrfUpdate = (objectStore: IDBObjectStore, newCsrfToken: string): Promise<boolean> => {
        return new Promise<boolean>((resolve) => {
            const request: IDBRequest<IDBValidKey> = objectStore.put(newCsrfToken, csrfKey);
            request.onsuccess = () => resolve(true);
            request.onerror = () => resolve(false);
        });
    }

    const resolveReadCsrf = (data: IDBRequest<string>): Promise<string | undefined> => {
        return new Promise<string | undefined>(resolve => {
            data.onsuccess = () => resolve(data.result);
            data.onerror = () => resolve(undefined);
        });
    }

    const removeCsrf = async (): Promise<boolean> => {
        const database: IDBDatabase = await openConnection();
        try {
            database.transaction(collectionName, "readwrite").objectStore(collectionName).delete(csrfKey);
            database.close();
            return Promise.resolve(true);
        } catch (error) {
            database.close();
            return Promise.resolve(false);
        }
    }

    return {
        readAuth,
        storeAuth,
        removeAuth,
        updateAuth,
        storeCsrf,
        readCsrf,
        updateCsrf,
        removeCsrf
    }
}
