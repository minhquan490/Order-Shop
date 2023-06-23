import { RegisterService } from "~/services/register.service";

export const useRegisterService = () => {
    const registerService = inject('registerService') as RegisterService;

    const register = (registerForm: Map<string, string>) => {
        return registerService.register(registerForm);
    }

    const validateRegisterForm = (registerForm: Map<string, string>) => {
        return registerService.validateForm(registerForm);
    }

    return {
        register,
        validateRegisterForm
    }
}