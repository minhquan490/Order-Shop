import {currentPath, navSources} from "~/logic/components/navbars.logic";
import {Authentication, NavBarsSource, Request, Response, TableHeader} from "~/types";

type History = {
    id: string,
    old_value: string,
    field_name: string,
    time_update: string,
}

type CustomerUpdateDataHistories = {
    histories: Array<History>,
    total_histories: number,
    page: number,
    page_size: number
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

const getUpdateHistoriesRequest = (customerId: string, page?: number, pageSize?: number): Request => {
    const serverUrl = useAppConfig().serverUrl;
    const serverPage: number = (page) ?? 1;
    const serverPageSize: number = (pageSize) ?? 100;
    return {
        apiUrl: `${serverUrl}/admin/customer/updated-data-histories`,
        queryParams: [
            {name: 'customerId', value: customerId},
            {name: 'page', value: serverPage.toString()},
            {name: 'pageSize', value: serverPageSize.toString()}
        ]
    };
}

const fetchHistories = async (customerId: string, page?: number, pageSize?: number): Promise<CustomerUpdateDataHistories | undefined> => {
    const auth: Authentication | undefined = await getAuth();
    if (!auth) {
        onUnAuthorize();
        return undefined;
    } else {
        const request: Request = getUpdateHistoriesRequest(customerId, page, pageSize);
        const {getAsyncCall} = useXmlHttpRequest(request, auth.accessToken, auth.refreshToken);
        const resp: Response<CustomerUpdateDataHistories> = await getAsyncCall<CustomerUpdateDataHistories>();
        if (resp.isError) {
            checkResponseStatus(resp);
            return undefined;
        }
        return resp.body as CustomerUpdateDataHistories;
    }
}

const getTableHeaders = (): TableHeader[] => {
    return [
        {name: 'ID', dataPropertyName: 'id'},
        {name: 'Old value', dataPropertyName: 'old_value'},
        {name: 'Field name', dataPropertyName: 'field_name'},
        {name: 'Time update', dataPropertyName: 'time_update'}
    ];
}

export {
    navigationSources,
    History,
    CustomerUpdateDataHistories,
    fetchHistories,
    getTableHeaders
};
