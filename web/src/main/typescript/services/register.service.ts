import {ErrorResponse} from "~/types/error-response.type";

export abstract class RegisterService {
    abstract register(registerForm: Map<string, string>): { message: string } | ErrorResponse | null;
    abstract validateForm(registerForm: Map<string, string>): Map<string, string>;
}