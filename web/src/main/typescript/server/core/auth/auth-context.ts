import { LoginInfo } from "../../../types/login-info.type";

export abstract class AuthContext {
    abstract addLoginInfo(loginInfo: LoginInfo): void;
    abstract isLogged(clientID: string): boolean;
    abstract removeLoggedClient(clientID: string): void;
}