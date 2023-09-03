import {Authentication, BasicCustomerInfo, Request, Response} from "~/types";
import {onUnLogin} from "~/utils/NavigationUtils";

export const usePageAccessPermission = () => {

    const indexedDb = useAuthInformation();
    const makeRequest = useXmlHttpRequest;
    const serverUrl = useAppConfig().serverUrl;
    const basicInfoPath: string = '/basic-information';

    const canAccess = async (): Promise<Response<BasicCustomerInfo> | undefined> => {
        const auth: Authentication | undefined = await indexedDb.readAuth();
        if (!auth) {
            onUnLogin();
        } else {
            const basicInfoUrl: string = `${serverUrl}${basicInfoPath}`;
            const request: Request = {
                apiUrl: basicInfoUrl,
                body: {token: auth.accessToken}
            };
            const {postAsyncCall} = makeRequest(request, auth.accessToken, auth.refreshToken);
            try {
                const resp: Response<BasicCustomerInfo> = await postAsyncCall<BasicCustomerInfo>();
                if (resp.isError) {
                    return Promise.resolve(undefined);
                } else {
                    return Promise.resolve(resp);
                }
            } catch (error) {
                console.log(error);
                return Promise.resolve(undefined);
            }
        }
    }

    return {
        canAccess
    }
}
