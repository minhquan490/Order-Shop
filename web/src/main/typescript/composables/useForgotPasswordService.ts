import { ForgotPasswordService } from "~/services/forgot-password.service";

export const useForgotPasswordService = () => {
    
    const forgotPasswordService: ForgotPasswordService = inject('forgotPasswordService') as ForgotPasswordService;
    const queryParam = useRoute().query;
    const token = queryParam['token'];

    const sendEmail = (email: string) => {
        return forgotPasswordService.requestRequestPasswordEmail(email);
    }

    const resetPassword = (newPassword: string) => {
        let actualToken: string;
        if (Array.isArray(token)) {
            const t = token[0];
            actualToken = (t === null) ? '' : t;
        } else {
            actualToken = (token === null) ? '' : token;
        }
        return forgotPasswordService.resetPassword(actualToken, newPassword);
    }

    return { sendEmail, resetPassword };
}