import {Authentication, NavBarsSource, Request, Response, TableHeader} from "~/types";
import {currentPath, navSources} from "~/logic/components/navbars.logic";

type CustomerAccessHistory = {
    id: string,
    path_request: string,
    request_type: string,
    request_content: string,
    customer_ip: string,
    remove_time: string
}

type CustomerAccessHistoriesLogic = {
    access_histories: Array<CustomerAccessHistory>,
    total_histories: number,
    page: number,
    page_size: number
}

const getTableHeaders = (): TableHeader[] => {
    return [
        {name: 'ID', dataPropertyName: 'id'},
        {name: 'Path request', dataPropertyName: 'path_request'},
        {name: 'Request type', dataPropertyName: 'request_type'},
        {name: 'Request content', dataPropertyName: 'request_content'},
        {name: 'Customer IP', dataPropertyName: 'customer_ip'},
        {name: 'Remove time', dataPropertyName: 'remove_time'}
    ];
}

const navigationSources = (): NavBarsSource[] => {
    return currentPath(
        navSources(),
        useRoute()
    );
}

const getAccessHistoriesRequest = (customerId: string, page?: number, pageSize?: number): Request => {
    const serverUrl = useAppConfig().serverUrl;
    const serverPage: number = (page) ?? 1;
    const serverPageSize: number = (pageSize) ?? 100;
    return {
        apiUrl: `${serverUrl}/admin/customer/access-histories`,
        queryParams: [
            {name: 'customerId', value: customerId},
            {name: 'page', value: serverPage.toString()},
            {name: 'pageSize', value: serverPageSize.toString()}
        ]
    };
}

const getAuth = async (): Promise<Authentication | undefined> => {
    return useAuthInformation().readAuth();
}

const fetchAccessHistories = async (customerId: string, page?: number, pageSize?: number): Promise<CustomerAccessHistoriesLogic | undefined> => {
    const auth: Authentication | undefined = await getAuth();
    if (!auth) {
        onUnAuthorize();
        return undefined;
    } else {
        const request: Request = getAccessHistoriesRequest(customerId, page, pageSize);
        const {getAsyncCall} = useXmlHttpRequest(request, auth.accessToken, auth.refreshToken);
        const resp: Response<CustomerAccessHistoriesLogic> = await getAsyncCall<CustomerAccessHistoriesLogic>();
        if (resp.isError) {
            checkResponseStatus(resp);
            return undefined;
        }
        return resp.body as CustomerAccessHistoriesLogic;
    }
}

export {
    getTableHeaders,
    navigationSources,
    CustomerAccessHistory,
    CustomerAccessHistoriesLogic,
    fetchAccessHistories
}