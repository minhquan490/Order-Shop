import {Authentication, NavBarsSource, Request, Response, TableHeader} from "~/types";
import {currentPath, navSources} from "~/logic/components/navbars.logic";

type AssignmentVoucher = {
    id: string,
    name: string,
    discount_percent: string,
    time_start: string,
    time_expired: string
}

type CustomerAssignmentVouchers = {
    vouchers: Array<AssignmentVoucher>,
    total_vouchers: number,
    page: number,
    page_size: number
}

const getAssignmentVouchersRequest = (customerId: string, page?: number, pageSize?: number): Request => {
    const serverUrl = useAppConfig().serverUrl;
    const serverPage: number = (page) ?? 1;
    const serverPageSize: number = (pageSize) ?? 100;
    return {
        apiUrl: `${serverUrl}/admin/customer/assignment-vouchers`,
        queryParams: [
            {name: 'customerId', value: customerId},
            {name: 'page', value: serverPage.toString()},
            {name: 'pageSize', value: serverPageSize.toString()}
        ]
    };
}

const navigationSources = (): NavBarsSource[] => {
    return currentPath(
        navSources(),
        useRoute()
    );
}

const getAuth = async (): Promise<Authentication | undefined> => {
    return useAuthInformation().readAuth();
}

const fetchAssignmentVoucher = async (customerId: string, page?: number, pageSize?: number): Promise<CustomerAssignmentVouchers | undefined> => {
    const auth: Authentication | undefined = await getAuth();
    if (!auth) {
        onUnAuthorize();
        return undefined;
    } else {
        const request: Request = getAssignmentVouchersRequest(customerId, page, pageSize);
        const {getAsyncCall} = useXmlHttpRequest(request, auth.accessToken, auth.refreshToken);
        const response: Response<CustomerAssignmentVouchers> = await getAsyncCall<CustomerAssignmentVouchers>();
        if (response.isError) {
            checkResponseStatus(response);
            return undefined;
        }
        return response.body as CustomerAssignmentVouchers;
    }
}

const getTableHeaders = (): TableHeader[] => {
    return [
        {name: 'ID', dataPropertyName: 'id'},
        {name: 'Name', dataPropertyName: 'name'},
        {name: 'Discount percent', dataPropertyName: 'discount_percent'},
        {name: 'Time start', dataPropertyName: 'time_start'},
        {name: 'Time expired', dataPropertyName: 'time_expired'}
    ];
}

export {
    getAssignmentVouchersRequest,
    AssignmentVoucher,
    CustomerAssignmentVouchers,
    fetchAssignmentVoucher,
    navigationSources,
    getTableHeaders
}