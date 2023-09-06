import {currentPath, navSources} from "~/logic/components/navbars.logic";
import {Authentication, NavBarsSource, Request, Response, TableHeader} from "~/types";
import {onUnLogin} from "~/utils/NavigationUtils";
import {checkResponseStatus} from "~/utils/ResponseUtils";

type CustomerInfo = {
    id: string,
    first_name: string,
    last_name: string,
    phone: string,
    email: string,
    gender: string,
    role: string,
    username: string,
    is_activated: boolean,
    is_account_non_expired: boolean,
    is_account_non_locked: boolean,
    is_credentials_non_expired: boolean,
    is_enabled: boolean
}

const navigationSources = (): NavBarsSource[] => {
    return currentPath(
        navSources(),
        useRoute()
    );
}

const tableHeaders = (): TableHeader[] => {
    return [
        {name: 'ID', dataPropertyName: 'id'},
        {name: 'First name', dataPropertyName: 'first_name'},
        {name: 'Last name', dataPropertyName: 'last_name'},
        {name: 'Phone', dataPropertyName: 'phone'},
        {name: 'Email', dataPropertyName: 'email'},
        {name: 'Gender', dataPropertyName: 'gender'},
        {name: 'Username', dataPropertyName: 'username'},
        {name: 'Is activated', dataPropertyName: 'is_activated'},
        {name: 'Account non expired', dataPropertyName: 'is_account_non_expired'},
        {name: 'Account non locked', dataPropertyName: 'is_account_non_locked'},
        {name: 'Credentials non expired', dataPropertyName: 'is_credentials_non_expired'},
        {name: 'Enabled', dataPropertyName: 'is_enabled'}
    ];
}

const navigateToNewCustomerPage = (): void => {
    const navigate = useNavigation().value;
    navigate('/admin/customer/create-new-customer');
}

const getCustomersInfoRequest = (): Request => {
    const serverUrl: string = useAppConfig().serverUrl;
    return {
        apiUrl: `${serverUrl}/admin/customer/list`
    }
}

const getAuth = async (): Promise<Authentication | undefined> => {
    const {readAuth} = useAuthInformation();
    const auth: Authentication | undefined = await readAuth();
    return Promise.resolve(auth);
}

const getCustomersInfo = async (): Promise<Array<CustomerInfo> | undefined> => {
    const auth: Authentication | undefined = await getAuth();
    if (!auth) {
        onUnLogin();
        return Promise.resolve(undefined);
    } else {
        const request: Request = getCustomersInfoRequest();
        const {getAsyncCall} = useXmlHttpRequest(request, auth.accessToken, auth.refreshToken);
        const response: Response<CustomerInfo[]> = await getAsyncCall<CustomerInfo[]>();
        if (response.isError) {
            checkResponseStatus(response);
            return Promise.resolve([]);
        } else {
            const resp: Array<CustomerInfo> = response.body as Array<CustomerInfo>;
            return Promise.resolve(resp);
        }
    }
}

const getDefaultCustomerInfo = (): CustomerInfo => {
    return {
        email: '',
        first_name: '',
        gender: '',
        is_account_non_expired: false,
        is_account_non_locked: false,
        is_activated: false,
        is_credentials_non_expired: false,
        is_enabled: false,
        last_name: '',
        phone: '',
        role: '',
        username: '',
        id: ''
    }
}

const navigateToCustomerInfoPage = (customerId: string): void => {
    const url: string = `/admin/customer/info?customerId=${customerId}`;
    const navigate = useNavigation().value;
    navigate(url);
}

export {
    getCustomersInfo,
    getDefaultCustomerInfo,
    navigateToCustomerInfoPage,
    navigateToNewCustomerPage,
    navigationSources,
    tableHeaders
};
