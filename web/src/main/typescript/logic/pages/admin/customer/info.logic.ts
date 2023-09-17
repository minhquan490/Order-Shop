import { currentPath, navSources } from "~/logic/components/navbars.logic";
import infoPage from "~/pages/admin/customer/info.vue";
import { Authentication, CarouselItem, NavBarsSource, Request, Response, TableHeader } from "~/types";
import { onUnAuthorize } from "~/utils/NavigationUtils";
import { checkResponseStatus } from "~/utils/ResponseUtils";

type AccessHistory = {
    path_request: string,
    request_type: string,
    request_time: string
}

type AssignedVoucher = {
    id: string,
    name: string
}

type OrderOfCustomer = {
    id: string,
    time_order: string,
    transaction_code: string,
    status: string
}

type LoginHistory = {
    id: string,
    last_login_time: string,
    login_ip: string,
    is_success: boolean
}

type ChangeHistory = {
    id: string,
    old_value: string,
    field_name: string,
    time_update: string
}

type CustomerInfoResp = {
    id: string,
    first_name: string,
    last_name: string,
    phone: string,
    email: string,
    gender: string,
    role: string,
    username: string,
    address: Array<string>,
    order_point: number,
    is_activated: boolean,
    is_account_non_expired: boolean,
    is_account_non_locked: boolean,
    is_credentials_non_expired: boolean,
    is_enabled: boolean,
    picture: string,
    histories: Array<AccessHistory>,
    vouchers: Array<AssignedVoucher>,
    orders: Array<OrderOfCustomer>,
    login_histories: Array<LoginHistory>,
    change_histories: Array<ChangeHistory>
}

type Page = InstanceType<typeof infoPage>

const navigationSources = (): NavBarsSource[] => {
    return currentPath(
        navSources(),
        useRoute()
    );
}

const defaultCarouselItem = (): CarouselItem[] => {
    return [
        {pictureUrl: '/carousel-no-image-available.svg', current: true},
    ];
}

const getAccessHistoriesTableHeader = (): Array<TableHeader> => {
    return [
        {name: 'Path request', dataPropertyName: 'path_request'},
        {name: 'Request type', dataPropertyName: 'request_type'},
        {name: 'Request time', dataPropertyName: 'request_time'}
    ];
}

const getAssignedVoucherTableHeader = (): Array<TableHeader> => {
    return [
        {name: 'id', dataPropertyName: 'id'},
        {name: 'name', dataPropertyName: 'name'}
    ];
}

const getOrderOfCustomerTableHeader = (): Array<TableHeader> => {
    return [
        {name: 'id', dataPropertyName: 'id'},
        {name: 'Status', dataPropertyName: 'status'},
        {name: 'Time order', dataPropertyName: 'time_order'},
        {name: 'Transaction Code', dataPropertyName: 'transaction_code'}
    ];
}

const getLoginHistoriesTableHeader = (): Array<TableHeader> => {
    return [
        {name: 'id', dataPropertyName: 'id'},
        {name: 'Last login time', dataPropertyName: 'last_login_time'},
        {name: 'Login ip', dataPropertyName: 'login_ip'},
        {name: 'Success', dataPropertyName: 'is_success'}
    ];
}

const getChangeHistoryTableHeader = (): Array<TableHeader> => {
    return [
        {name: 'id', dataPropertyName: 'id'},
        {name: 'Old value', dataPropertyName: 'old_value'},
        {name: 'Field name', dataPropertyName: 'field_name'},
        {name: 'Time update', dataPropertyName: 'time_update'}
    ];
}

const getDefaultAccessHistory = (): AccessHistory => {
    return {
        path_request: '',
        request_time: '',
        request_type: ''
    }
}

const getDefaultAssignedVoucher = (): AssignedVoucher => {
    return {
        id: '',
        name: ''
    }
}

const getDefaultOrderOfCustomer = (): OrderOfCustomer => {
    return {
        id: '',
        status: '',
        time_order: '',
        transaction_code: ''
    }
}

const getDefaultLoginHistory = (): LoginHistory => {
    return {
        id: '',
        is_success: false,
        last_login_time: '',
        login_ip: ''
    }
}

const getDefaultChangeHistory = (): ChangeHistory => {
    return {
        field_name: '',
        old_value: '',
        time_update: '',
        id: ''
    }
}

const getCustomerInfoRequest = (customerId: string): Request => {
    const serverUrl = useAppConfig().serverUrl;
    return {
        apiUrl: `${serverUrl}/admin/customer/info`,
        queryParams: [
            {name: 'userId', value: customerId}
        ]
    }
}

const getAuth = async (): Promise<Authentication | undefined> => {
    return useAuthInformation().readAuth();
}

const getCustomerInfo = async (customerId: string): Promise<CustomerInfoResp | undefined> => {
    const auth: Authentication | undefined = await getAuth();
    if (!auth) {
        onUnAuthorize();
        return undefined;
    } else {
        const request: Request = getCustomerInfoRequest(customerId);
        const {getAsyncCall} = useXmlHttpRequest(request, auth.accessToken, auth.refreshToken);
        const response: Response<CustomerInfoResp> = await getAsyncCall<CustomerInfoResp>();
        if (response.isError) {
            checkResponseStatus(response);
        }
        return response.body as CustomerInfoResp;
    }
}

const createCarouselItems = (picUrl: string): CarouselItem[] => {
    if (picUrl.length === 0) {
        return defaultCarouselItem();
    }
    return [
        {pictureUrl: picUrl, current: true}
    ];
}

const assignData = (page: Page, customerInfo: CustomerInfoResp): void => {
    page.accessHistories = customerInfo.histories;
    page.assignedVouchers = customerInfo.vouchers;
    page.ordersOfCustomer = customerInfo.orders;
    page.loginHistories = customerInfo.login_histories;
    page.changeHistories = customerInfo.change_histories;
    page.customerInfo.id = customerInfo.id;
    page.customerInfo.first_name = customerInfo.first_name;
    page.customerInfo.last_name = customerInfo.last_name;
    page.customerInfo.phone = customerInfo.phone;
    page.customerInfo.email = customerInfo.email;
    page.customerInfo.gender = customerInfo.gender;
    page.customerInfo.role = customerInfo.role;
    page.customerInfo.username = customerInfo.username;
    page.customerInfo.address = customerInfo.address;
    page.customerInfo.is_activated = customerInfo.is_activated;
    page.customerInfo.is_account_non_expired = customerInfo.is_account_non_expired;
    page.customerInfo.is_account_non_locked = customerInfo.is_account_non_locked;
    page.customerInfo.is_credentials_non_expired = customerInfo.is_credentials_non_expired;
    page.customerInfo.is_enabled = customerInfo.is_enabled;
    page.customerInfo.picture = customerInfo.picture;
    page.customerInfo.order_point = customerInfo.order_point;
}

const redirectToCustomerLoginHistoriesPage = (customerId: string): void => {
    const url: string = `/admin/customer/login-histories?customerId=${customerId}`;
    const navigate = useNavigation().value;
    navigate(url);
}

const redirectToUpdatedDataHistoriesPage = (customerId: string): void => {
    const url: string = `/admin/customer/updated-data-histories?customerId=${customerId}`;
    const navigate = useNavigation().value;
    navigate(url);
}

const redirectToVouchersOfCustomerPage = (customerId: string): void => {
    const url: string = `/admin/customer/voucher-of-customer?customerId=${customerId}`;
    const navigate = useNavigation().value;
    navigate(url);
}

const redirectToOrdersOfCustomerPage = (customerId: string): void => {
    const url: string = `/admin/customer/orders-of-customer?customerId=${customerId}`;
    const navigate = useNavigation().value;
    navigate(url);
}

const redirectToCustomerAccessHistoriesPage = (customerId: string): void => {
    const url: string = `/admin/customer/access-histories?customerId=${customerId}`;
    const navigate = useNavigation().value;
    navigate(url);
}

export {
    CustomerInfoResp,
    assignData,
    createCarouselItems,
    defaultCarouselItem,
    getAccessHistoriesTableHeader,
    getAssignedVoucherTableHeader,
    getChangeHistoryTableHeader,
    getCustomerInfo,
    getDefaultAccessHistory,
    getDefaultAssignedVoucher,
    getDefaultChangeHistory,
    getDefaultLoginHistory,
    getDefaultOrderOfCustomer,
    getLoginHistoriesTableHeader,
    getOrderOfCustomerTableHeader,
    navigationSources,
    redirectToCustomerAccessHistoriesPage,
    redirectToCustomerLoginHistoriesPage,
    redirectToOrdersOfCustomerPage,
    redirectToUpdatedDataHistoriesPage,
    redirectToVouchersOfCustomerPage
};

