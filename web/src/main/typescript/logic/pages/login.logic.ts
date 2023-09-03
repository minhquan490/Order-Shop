import loginPage from '~/pages/login.vue';
import {Authentication, ErrorResponse, Request, Response} from "~/types";

type LoginPageType = InstanceType<typeof loginPage>
type LoginReq = {
    username: string,
    password: string
}
type LoginResp = {
    refresh_token: string,
    access_token: string
}

const errorTitle: string = "Login failure because: ";

const validateInput = (page: LoginPageType): boolean => {

    const isEmailValid: boolean = validateEmail(page);
    const isPasswordValid: boolean = validatePassword(page)

    return isPasswordValid && isEmailValid;
}

const validateEmail = (page: LoginPageType): boolean => {
    const {emailRegex} = useRegex();
    const email: string = page.loginData.email;

    if (email.length === 0) {
        page.loginData.emailError = 'Email is required !';
    } else {
        const passed: boolean = emailRegex.test(email);
        if (!passed) {
            page.loginData.emailError = 'Email is invalid !';
        }
    }
    return page.loginData.emailError.length === 0;
}

const validatePassword = (page: LoginPageType): boolean => {
    const password: string = page.loginData.password;

    if (password.length === 0) {
        page.loginData.passwordError = 'Password is required !';
    }

    return page.loginData.passwordError.length === 0
}

const login = async (page: LoginPageType, loginUrl: string): Promise<void> => {
    const {updateAuth} = useAuthInformation();
    const email: string = page.loginData.email;
    const password: string = page.loginData.password;
    const loginReq: LoginReq = {
        username: email,
        password: password
    }
    const request: Request = {
        apiUrl: loginUrl,
        body: loginReq
    }
    const {postAsyncCall} = useXmlHttpRequest(request);
    const resp: Response<LoginResp> = await postAsyncCall<LoginResp>()
    if (resp.isError) {
        page.loginErrorMsg = (resp.body as ErrorResponse).messages;
        page.hide = false;
    } else {
        const loginResp: LoginResp = resp.body as LoginResp;

        const auth: Authentication = {
            refreshToken: loginResp.refresh_token,
            accessToken: loginResp.access_token
        }

        const saveResult: boolean = await updateAuth(auth);
        if (saveResult) {
            page.isLoading = false;
            const navigate = useNavigation().value;
            navigate('/');
            return;
        }
    }
    page.loginData.email = '';
    page.loginData.password = '';
    page.isLoading = false;
}

export {
    validateInput,
    validateEmail,
    validatePassword,
    errorTitle,
    login
}