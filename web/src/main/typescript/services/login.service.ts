
export abstract class LoginService {
    abstract validateLoginForm(form: LoginForm): ValidateResult;
    abstract login(form: LoginForm): SuccessResponse | FailureResponse | undefined;
    abstract storeAuthentication(res: SuccessResponse): void;
}

type ValidateResult = {
    username?: string,
    password?: string,
    isValid: boolean
}

type LoginForm = {
    username: string,
    password: string
}

type SuccessResponse = {
    refresh_token: string,
    access_token: string
}

type FailureResponse = {
    status: number,
    messages: Array<string>
}