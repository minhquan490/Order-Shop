import { Auth } from "../types/auth.type";

export abstract class AuthService {
    abstract getAuth(): Auth;
    abstract storeAuth(auth: Auth): void;
    abstract isLogged(): boolean;
    abstract updateAuthStatus(value: boolean): void;
}