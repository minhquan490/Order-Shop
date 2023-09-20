import { currentPath, navSources } from "~/logic/components/navbars.logic";
import { Authentication, ErrorResponse, NavBarsSource, Request, Response, TableHeader } from "~/types";

type CategoryResponse = {
    category_id: string,
    category_name: string
}

const navigationSources = (): NavBarsSource[] => {
    return currentPath(
        navSources(),
        useRoute()
    );
}

const tableHeaders = (): TableHeader[] => {
    return [
        {name: 'ID', dataPropertyName: 'category_id'},
        {name: 'Name', dataPropertyName: 'category_name'}
    ];
}

const getAuth = async (): Promise<Authentication | undefined> => {
    return useAuthInformation().readAuth();
}

const getCategoriesRequest = (): Request => {
    const serverUrl: string = useAppConfig().serverUrl;
    return {
        apiUrl: `${serverUrl}/content/category/list`
    };
}

const deleteCategoryRequest = (categoryId: string): Request => {
    const serverUrl: string = useAppConfig().serverUrl;
    return {
        apiUrl: `${serverUrl}/admin/category/delete`,
        body: { id: categoryId }
    };
}

const getCategories = async (): Promise<Array<CategoryResponse> | ErrorResponse | undefined> => {
    const auth: Authentication | undefined = await getAuth();
    if (!auth) {
        onUnAuthorize();
        return Promise.resolve(undefined);
    } else {
        const request: Request = getCategoriesRequest();
        const {getAsyncCall} = useXmlHttpRequest(request, auth.accessToken, auth.refreshToken);
        const response: Response<Array<CategoryResponse>> = await getAsyncCall<Array<CategoryResponse>>();
        return Promise.resolve(response.body);
    }
}

const deleteCategory = async (category: CategoryResponse): Promise<boolean | ErrorResponse> => {
    const auth: Authentication | undefined = await getAuth();
    if (!auth) {
        onUnAuthorize();
        return Promise.resolve(false);
    } else {
        const request: Request = deleteCategoryRequest(category.category_id);
        const {deleteAsyncCall} = useXmlHttpRequest(request, auth.accessToken, auth.refreshToken);
        const response: Response<any> = await deleteAsyncCall<any>();
        if (response.isError) {
            return Promise.resolve(response.body);
        } else {
            return Promise.resolve(true);
        }
    }
}

export {
    CategoryResponse,
    deleteCategory,
    getCategories,
    navigationSources,
    tableHeaders
};

