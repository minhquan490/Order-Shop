import {Authentication, CarouselItem, NavBarsSource, Request, Response, TableHeader} from "~/types";
import {currentPath, navSources} from "~/logic/components/navbars.logic";
import {checkResponseStatus} from "~/utils/ResponseUtils";
import infoPage from "~/pages/admin/customer/info.vue";

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

const checkCustomerIdQueryParam = (): string => {
    const customerIdQuery = useRoute().query['customerId'];
    if (!customerIdQuery) {
        const navigate = useNavigation().value;
        navigate('/admin');
    }
    if (Array.isArray(customerIdQuery)) {
        return (customerIdQuery as Array<string>)[0];
    }
    return customerIdQuery as string;
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

const getCustomerInfo = async (customerId: string): Promise<CustomerInfoResp> => {
    const request: Request = getCustomerInfoRequest(customerId);
    const auth: Authentication | undefined = await getAuth();
    if (!auth) {
        const navigate = useNavigation().value;
        navigate('/403');
        return;
    }
    const {getAsyncCall} = useXmlHttpRequest(request, auth.accessToken, auth.refreshToken);
    const response: Response<CustomerInfoResp> = await getAsyncCall<CustomerInfoResp>();
    if (response.statusCode >= 400) {
        checkResponseStatus(response);
    }
    return response.body as CustomerInfoResp;
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
}

export {
    navigationSources,
    defaultCarouselItem,
    getDefaultAccessHistory,
    getDefaultAssignedVoucher,
    getDefaultOrderOfCustomer,
    getDefaultLoginHistory,
    getAccessHistoriesTableHeader,
    getAssignedVoucherTableHeader,
    getOrderOfCustomerTableHeader,
    getLoginHistoriesTableHeader,
    getChangeHistoryTableHeader,
    getDefaultChangeHistory,
    checkCustomerIdQueryParam,
    getCustomerInfo,
    createCarouselItems,
    assignData
}