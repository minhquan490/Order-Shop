export abstract class RegisterService {
    abstract register(registerForm: Map<string, string>): { message: string };
    abstract validateForm(registerForm: Map<string, string>): Map<string, string>;
}