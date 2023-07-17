import { ErrorResponse } from "~/types/error-response.type";

export abstract class ForgotPasswordService {
    abstract requestRequestPasswordEmail(email: string): void;
    abstract resetPassword(secretToken: string, newPassword: string): ErrorResponse | undefined | null;
}