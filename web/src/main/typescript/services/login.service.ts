import { ErrorResponse } from "~/types/error-response.type"
import { LoginForm } from "~/types/login-form.type"
import { SuccessResponse } from "~/types/success-response.type"
import { ValidateResult } from "~/types/validate-result.type"

export abstract class LoginService {
    abstract validateLoginForm(form: LoginForm): ValidateResult;
    abstract login(form: LoginForm): SuccessResponse | ErrorResponse | undefined;
    abstract storeAuthentication(res: SuccessResponse): void;
}