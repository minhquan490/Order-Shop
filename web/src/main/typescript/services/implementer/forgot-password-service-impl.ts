import { ErrorResponse } from "~/types/error-response.type";
import { ForgotPasswordService } from "../forgot-password.service";
import { HttpServiceProvider } from "../http.service";

export class ForgotPasswordServiceImpl extends ForgotPasswordService {
    private serverUrl: string;
    
    constructor(private httpServiceProvider: HttpServiceProvider) {
        super();
        this.serverUrl = useAppConfig().serverUrl;
    }

    requestRequestPasswordEmail(email: string): void {
        const service = this.httpServiceProvider.open(`${this.serverUrl}/request-reset-password`);
        const req: RequestResetPassword = {
            email: email
        };
        service.post<RequestResetPassword, void>(req);
    }

    resetPassword(secretToken: string, newPassword: string): ErrorResponse | undefined {
        const service = this.httpServiceProvider.open(`${this.serverUrl}/reset-password`);
        const req: ResetPassword = {
            password: newPassword,
            token: secretToken
        };
        return service.post<ResetPassword, undefined | ErrorResponse>(req);
    }
}

type RequestResetPassword = {
    email: string
}

type ResetPassword = {
    token: string,
    password: string
}