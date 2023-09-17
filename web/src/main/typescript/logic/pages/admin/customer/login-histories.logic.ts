import { currentPath, navSources } from "~/logic/components/navbars.logic";
import loginHistoriesVue from "~/pages/admin/customer/login-histories.vue";
import { Authentication, NavBarsSource, QueryParam, Request, Response, TableHeader } from "~/types";

const navigationSources = (): NavBarsSource[] => {
    return currentPath(
        navSources(),
        useRoute()
    );
}

const getViewDataTableHeaders = (): TableHeader[] => {
    return [
        {name: 'ID', dataPropertyName: 'id'},
        {name: 'Last login time', dataPropertyName: 'last_login_time'},
        {name: 'Login IP', dataPropertyName: 'login_ip'},
        {name: 'Is success', dataPropertyName: 'success'}
    ];
}

const getLoginHistoriesRequest = (customerId: string, page?: number, pageSize?: number): Request => {
    const serverUrl: string = useAppConfig().serverUrl;
    const queryParams: QueryParam[] = [];
    queryParams.push({name: 'customerId', value: customerId});
    if (page) {
        queryParams.push({name: 'page', value: page.toString()});
    }
    if (pageSize) {
        queryParams.push({name: 'pageSize', value: pageSize.toString()});
    }
    return {
        apiUrl: `${serverUrl}/admin/customer/login-histories`,
        queryParams: queryParams
    };
}

const getCustomerLoginHistoriesData = async (customerId: string, page?: number, pageSize?: number): Promise<CustomerLoginHistory | undefined> => {
    const {readAuth} = useAuthInformation();
    const auth: Authentication | undefined = await readAuth();
    if (!auth) {
        onUnAuthorize();
        return Promise.resolve(undefined);
    } else {
        const request: Request = getLoginHistoriesRequest(customerId, page, pageSize);
        const {getAsyncCall} = useXmlHttpRequest(request, auth.accessToken, auth.refreshToken);
        const response: Response<CustomerLoginHistory> = await getAsyncCall<CustomerLoginHistory>();
        let result: Promise<CustomerLoginHistory | undefined>;
        if (response.isError) {
            const navigate = useNavigation().value;
            const url: string = `/admin/customer/info?customerId=${customerId}`;
            navigate(url);
            result = Promise.resolve(undefined);
        } else {
            result = Promise.resolve(response.body as CustomerLoginHistory);
        }
        return result;
    }
}

type LoginHistoriesPage = InstanceType<typeof loginHistoriesVue>;

type CustomerLoginHistory = {
    login_histories: Array<LoginHistory>,
    total_histories: number,
    page: number,
    page_size: number
}

type LoginHistory = {
    id: string,
    last_login_time: string,
    login_ip: string,
    success: string
}

export {
    CustomerLoginHistory,
    LoginHistory,
    getCustomerLoginHistoriesData,
    getViewDataTableHeaders,
    navigationSources
};

