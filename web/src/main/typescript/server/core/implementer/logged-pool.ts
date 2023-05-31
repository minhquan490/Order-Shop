import { LoginInfo } from "../../../types/login-info.type";
import { AuthContext } from "../auth/auth-context";

export class LoggedPool extends AuthContext {
    private readonly context: Map<string, LoginInfo> = new Map<string, LoginInfo>();

    addLoginInfo(loginInfo: LoginInfo): void {
        this.context.set(loginInfo.clientID, loginInfo);
    }

    isLogged(clientID: string): boolean {
        const target = this.context.get(clientID);
        return (target) ? true : false;
    }

    removeLoggedClient(clientID: string): void {
        this.context.delete(clientID);
    }
    
}