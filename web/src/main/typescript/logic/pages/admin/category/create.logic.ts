import { currentPath, navSources } from "~/logic/components/navbars.logic";
import { Authentication, ErrorResponse, NavBarsSource, Request, Response } from "~/types";

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

const validateCategory = (categoryName: string): string => {
    const name: string = categoryName.trim();
    if (name.length < 4 || name.length > 32) {
        return "Name of category must be in range 4 to 32 character";
    }
    return "";
}

const getCreateCategoryRequest = (categoryName: string): Request => {
    const serverUrl: string = useAppConfig().serverUrl;
    return {
        apiUrl: `${serverUrl}/admin/category/create`,
        body: { name: categoryName }
    };
}

const getAuth = async (): Promise<Authentication | undefined> => {
    return useAuthInformation().readAuth();
}

const createCategory = async (categoryName: string): Promise<CategoryResponse | ErrorResponse | undefined> => {
    const auth: Authentication | undefined = await getAuth();
    if (!auth) {
        onUnAuthorize();
        return Promise.resolve(undefined);
    } else {
        const request: Request = getCreateCategoryRequest(categoryName);
        const {postAsyncCall} = useXmlHttpRequest(request, auth.accessToken, auth.refreshToken);
        const response: Response<CategoryResponse> = await postAsyncCall<CategoryResponse>();
        return Promise.resolve(response.body);
    }
}

export {
    CategoryResponse,
    createCategory,
    navigationSources,
    validateCategory
};

