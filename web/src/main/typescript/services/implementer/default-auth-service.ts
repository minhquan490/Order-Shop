import { Auth } from "~/types/auth.type";
import { AuthService } from "../auth.service";

export class DefaultAuthService extends AuthService {

    constructor() {
        super();
    }

    getAuth(): Auth {
        const store = useAppStorage().value;
        const appConfig = useAppConfig();
        const refreshToken = store.getItem(appConfig.refreshToken);
        const accessToken = store.getItem(appConfig.authorization);
        return {
            accessToken: accessToken === null ? '' : accessToken,
            refreshToken: refreshToken === null ? '' : refreshToken
        };
    }

    storeAuth(auth: Auth): void {
        const store = useAppStorage().value;
        const appConfig = useAppConfig();
        if (auth.refreshToken && auth.accessToken) {
            store.setItem(appConfig.refreshToken, auth.refreshToken);
            store.setItem(appConfig.authorization, auth.accessToken);
            store.setItem(appConfig.logged, 'true');
        }
    }
    
    isLogged(): boolean {
        const store = useAppStorage().value;
        const appConfig = useAppConfig();
        return store.getItem(appConfig.logged) == true;
    }

    updateAuthStatus(value: boolean): void {
        const store = useAppStorage().value;
        const appConfig = useAppConfig();
        store.setItem(appConfig.logged, value);
    }
}