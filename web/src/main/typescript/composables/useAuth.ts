import { AuthService } from "~/services/auth.service";
import { LoginService } from "~/services/login.service";
import { LoginForm } from "~/types/login-form.type";
import { SuccessResponse } from "~/types/success-response.type";

export const useAuth = () => {
    const loginService = inject('loginService') as LoginService;
    const authService = inject('authService') as AuthService;
    
    const login = (form: LoginForm) => {
        return loginService.login(form);
    }

    const validateFormLogin = (form: LoginForm) => {
        return loginService.validateLoginForm(form);
    }

    const storeAuth = (res: SuccessResponse) => {
        return loginService.storeAuthentication(res);
    }

    const getAuth = () => {
        return authService.getAuth();
    }

    const isUserLogged = () => {
        return authService.isLogged();
    }

    const updateLoginStatus = (loginStatus: boolean) => {
        return authService.updateAuthStatus(loginStatus);
    }

    return {
        login,
        validateFormLogin,
        storeAuth,
        getAuth,
        isUserLogged,
        updateLoginStatus
    }
}