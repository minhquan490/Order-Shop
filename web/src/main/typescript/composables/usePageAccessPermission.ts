import {Authentication, BasicCustomerInfo, Request, Response} from "~/types";

export const usePageAccessPermission = () => {

    const indexedDb = useAuthInformation();
    const makeRequest = useXmlHttpRequest;
    const serverUrl = useAppConfig().serverUrl;
    const basicInfoPath: string = '/basic-information';

    const canAccess = async (): Promise<Response<BasicCustomerInfo> | undefined> => {
        const auth: Authentication | undefined = await indexedDb.readAuth();
        if (!auth) {
            return Promise.resolve(undefined);
        }
        const basicInfoUrl: string = `${serverUrl}${basicInfoPath}`;
        const request: Request = {
            apiUrl: basicInfoUrl,
            queryParams: [{name: 'token', value: auth.accessToken}]
        };
        const {getAsyncCall} = makeRequest(request, auth.accessToken, auth.refreshToken);
        try {
            const resp: Response<BasicCustomerInfo> = await getAsyncCall<BasicCustomerInfo>();
            if (resp.statusCode >= 400) {
                return Promise.resolve(undefined);
            } else {
                return Promise.resolve(resp);
            }
        } catch (error) {
            console.log(error);
            return Promise.resolve(undefined);
        }
    }

    return {
        canAccess
    }
}
