import { HttpServiceProvider } from "../http.service";
import { RegisterService } from "../register.service";
import {ErrorResponse} from "~/types/error-response.type";

export class RegisterServiceImpl extends RegisterService {
    private readonly emailPattern: RegExp = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;

    constructor(private httpServiceProvider: HttpServiceProvider) {
        super();
    }

    register(registerForm: Map<string, string>): { message: string } | ErrorResponse | null {
        const httpService = this.httpServiceProvider.open(`${useAppConfig().serverUrl}/register`);
        registerForm.delete('confirmPassword');
        return httpService.post<Map<string, string>, { message: string }>(registerForm).getResponse;
    }

    validateForm(registerForm: Map<string, string>): Map<string, string> {
        const firstName: string = registerForm.get('firstName') as string;
        const lastName: string = registerForm.get('lastName') as string;
        const email: string = registerForm.get('email') as string;
        const username: string = registerForm.get('username') as string;
        const password: string = registerForm.get('password') as string;
        const confirmPassword: string = registerForm.get('confirmPassword') as string;

        const result: Map<string, string> = new Map();
        result.set('firstNameError', '');
        result.set('lastNameError', '');
        result.set('emailError', '');
        result.set('usernameError', '');
        result.set('passwordError', '');
        result.set('confirmPasswordError', '');

        if (firstName.length === 0) {
            result.set('firstNameError', 'First name is required');
        }
        if (lastName.length === 0) {
            result.set('lastNameError', 'Last name is required');
        }
        if (email.length === 0) {
            result.set('emailError', 'Email is required');
        }
        if (username.length === 0) {
            result.set('usernameError', 'Username is required');
        }
        if (password.length === 0) {
            result.set('passwordError', 'Password is required');
        }
        if (password !== confirmPassword) {
            result.set('confirmPasswordError', 'Your password is not match');
        }

        if ((firstName.length < 6 || firstName.length > 32) && result.get('firstName')?.length === 0) {
            result.set('firstNameError', 'First name must be in range 6 - 32');
        }
        if ((lastName.length < 6 || lastName.length > 32) && result.get('lastName')?.length === 0) {
            result.set('lastNameError', 'Last name must be in range 6 - 32');
        }
        if ((email.length < 6 || email.length > 32) && result.get('email')?.length === 0) {
            result.set('emailError', 'Email must be in range 6 - 32');
        }
        if (!this.emailPattern.test(email) && result.get('email')?.length === 0) {
            result.set('emailError', 'Email is not valid');
        }
        return result;
    }

}