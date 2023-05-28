import { Auth } from "~/types/auth.type";
import { AuthService } from "../auth.service";
import { HttpServiceProvider } from "../http.service";
import { LoginService } from "../login.service";

export class LoginServiceImpl extends LoginService {
    private authService: AuthService;
    private httpClientProvider: HttpServiceProvider;

    constructor(httpServiceProvider: HttpServiceProvider, authService: AuthService) {
        super();
        this.authService = authService;
        this.httpClientProvider = httpServiceProvider;
    }
    
    storeAuthentication(res: SuccessResponse): void {
        const auth: Auth = {
            accessToken: res.access_token,
            refreshToken: res.refresh_token
        };
        this.authService.storeAuth(auth);
    }

    validateLoginForm(form: LoginForm): ValidateResult {
        const isUsernameInvalid = form.username.length < 4 || form.username.length > 32;
        const isPasswordInvalid = form.password.length === 0;
        let passwordErrorDetail = "";
        let usernameErrorDetail = "";
        if (isPasswordInvalid) {
        passwordErrorDetail = "Password is require";
        }
        if (isUsernameInvalid) {
        usernameErrorDetail = "Length of username must be in range 4 - 32";
        }
        const isValid = passwordErrorDetail.length === 0 && usernameErrorDetail.length === 0;
        let result: ValidateResult;
        if (isValid) {
            result = { isValid: isValid };
        } else {
            result = {
                isValid: false,
                username: usernameErrorDetail,
                password: passwordErrorDetail,
            };
        }
        return result;
    }

    login(form: LoginForm): SuccessResponse | FailureResponse | undefined {
        const httpService = this.httpClientProvider.open("/api/login");
        const response = httpService.post<LoginForm, SuccessResponse | FailureResponse>(form);
        if ("status" in response) {
            return {
                messages: response.messages,
                status: response.status,
            };
        }
        if ("access_token" in response) {
            return {
                access_token: response.access_token,
                refresh_token: response.refresh_token,
            };
        }
    }
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

type HttpBrowser = {
    statusCode: number,
    message: string,
}