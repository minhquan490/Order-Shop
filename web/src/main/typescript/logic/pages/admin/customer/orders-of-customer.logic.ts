import {Authentication, NavBarsSource, Request, Response, TableHeader} from "~/types";
import {currentPath, navSources} from "~/logic/components/navbars.logic";

type OrderInfo = {
    id: string,
    order_time: string,
    order_status: string
}

type OrderOfCustomer = {
    infos: Array<OrderInfo>,
    total: number,
    page: number,
    page_size: number
}

const tableHeaders = (): TableHeader[] => {
    return [
        {name: 'ID', dataPropertyName: 'id'},
        {name: 'Order time', dataPropertyName: 'order_time'},
        {name: 'Order status', dataPropertyName: 'order_status'}
    ];
}

const getOrdersOfCustomerRequest = (customerId: string, page?: number, pageSize?: number): Request => {
    const serverUrl = useAppConfig().serverUrl;
    const serverPage: number = (page) ?? 1;
    const serverPageSize: number = (pageSize) ?? 100;
    return {
        apiUrl: `${serverUrl}/admin/order/orders-of-customer`,
        queryParams: [
            {name: 'customerId', value: customerId},
            {name: 'pageSize', value: serverPageSize.toString()},
            {name: 'page', value: serverPage.toString()}
        ]
    };
}

const getAuth = async (): Promise<Authentication | undefined> => {
    return useAuthInformation().readAuth();
}

const fetchOrdersOfCustomer = async (customerId: string, page?: number, pageSize?: number): Promise<OrderOfCustomer | undefined> => {
    const auth: Authentication | undefined = await getAuth();
    if (!auth) {
        onUnAuthorize();
        return undefined;
    } else {
        const request: Request = getOrdersOfCustomerRequest(customerId, page, pageSize);
        const {getAsyncCall} = useXmlHttpRequest(request, auth.accessToken, auth.refreshToken);
        const response: Response<OrderOfCustomer> = await getAsyncCall<OrderOfCustomer>();
        if (response.isError) {
            checkResponseStatus(response);
            return undefined;
        }
        return response.body as OrderOfCustomer;
    }
}

const navigationSources = (): NavBarsSource[] => {
    return currentPath(
        navSources(),
        useRoute()
    );
}

export {
    OrderInfo,
    OrderOfCustomer,
    tableHeaders,
    fetchOrdersOfCustomer,
    navigationSources
}