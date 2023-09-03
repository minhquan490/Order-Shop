import registerPage from '~/pages/register.vue';
import {ErrorResponse, Request, Response} from "~/types";
import {onUnLogin} from "~/utils/NavigationUtils";

type RegisterPageType = InstanceType<typeof registerPage>
type RegisterReq = {
    email: string,
    password: string
}
type RegisterResp = {
    message: string
}

const errorTitle: string = 'Register failure: ';

const validateEmail = (page: RegisterPageType): boolean => {
    const {emailRegex} = useRegex();
    const email: string = page.registerData.email;

    if (email.length === 0) {
        page.registerData.emailError = 'Email is required !';
    } else {
        const passed: boolean = emailRegex.test(email);
        if (!passed) {
            page.registerData.emailError = 'Email is invalid !';
        }
    }
    return page.registerData.emailError.length === 0;
}

const validatePassword = (page: RegisterPageType): boolean => {
    const password: string = page.registerData.password;

    if (password.length === 0) {
        page.registerData.passwordError = 'Password is required !';
    } else {
        const {strongPasswordRegex} = useRegex();
        const validated: boolean = strongPasswordRegex.test(password);
        if (!validated) {
            page.registerData.passwordError = 'You password must have minimum 8 char, 1 uppercase, 1 lowercase, 1 number and 1 special char';
        }
    }

    return page.registerData.passwordError.length === 0
}

const validateConfirmPassword = (page: RegisterPageType): boolean => {
    const password: string = page.registerData.password;
    const confirmPassword: string = page.registerData.confirmPassword;

    if (password !== confirmPassword) {
        page.registerData.confirmPasswordError = 'Your password is not match !';
    }

    return page.registerData.confirmPasswordError.length === 0;
}

const validateInputData = (page: RegisterPageType): boolean => {
    const isEmailValid: boolean = validateEmail(page);
    const isPasswordValid: boolean = validatePassword(page);
    const isConfirmPasswordValid: boolean = validateConfirmPassword(page);

    return isEmailValid && isPasswordValid && isConfirmPasswordValid;
}

const register = (page: RegisterPageType, registerUrl: string): void => {
    page.isLoading = true;
    const email: string = page.registerData.email;
    const password: string = page.registerData.password;
    const registerReq: RegisterReq = {
        email: email,
        password: password
    }
    const request: Request = {
        apiUrl: registerUrl,
        body: registerReq
    }
    const {postAsyncCall} = useXmlHttpRequest(request);
    const resp: Promise<Response<RegisterResp>> = postAsyncCall<RegisterResp>();
    resp.then(res => {
        if (res.isError) {
            page.registerErrorMsg = (res.body as ErrorResponse).messages;
            page.hide = false;
        } else {
            const registerResp: RegisterResp = res.body as RegisterResp;
            console.log(registerResp.message);
            onUnLogin();
        }
        applyCallbackRequestCall(page);
    }).catch(err => {
        applyCallbackRequestCall(page);
    });
}

function applyCallbackRequestCall(page: RegisterPageType): void {
    page.registerData.email = '';
    page.registerData.password = '';
    page.registerData.confirmPassword = '';
    page.isLoading = false;
}

export {
    errorTitle,
    register,
    validateConfirmPassword,
    validateEmail,
    validateInputData,
    validatePassword
};
