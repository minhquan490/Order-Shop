import {Request} from "~/types";

export const useRequestDecorator = () => {

    const applyCommonHeader = (request: Request): void => {
        request.headers = [
            {name: 'X-Sec-Client-UA', value: '?1'},
            {name: 'Content-Type', value: 'application/json'}
        ];
    }

    const applyAuthenticationHeader = (request: Request, accessToken?: string, refreshToken?: string): void => {
        if (!request.headers) {
            request.headers = [];
        }
        if (accessToken && refreshToken) {
            request.headers.push({name: useAppConfig().authorization, value: accessToken});
            request.headers.push({name: useAppConfig().refreshToken, value: refreshToken});
        }
    }

    const applyCsrfHeader = async (request: Request): Promise<void> => {
        if (!request.headers) {
            request.headers = [];
        }
        const csrfHeader = useAppConfig().csrfToken;
        const {readCsrf} = useAuthInformation()
        const csrf: string | undefined = await readCsrf();
        if (csrf) {
            request.headers.push({name: csrfHeader, value: csrf});
        }
    }

    return {
        applyCommonHeader,
        applyAuthenticationHeader,
        applyCsrfHeader
    };
}
