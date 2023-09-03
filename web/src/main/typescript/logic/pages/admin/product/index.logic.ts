import {
    AdminProduct,
    Authentication,
    Category,
    ErrorResponse,
    NavBarsSource,
    Request,
    Response,
    TableData,
    TableHeader
} from "~/types";
import {currentPath, navSources} from "~/logic/components/navbars.logic";
import productIndexPage from '~/pages/admin/product/index.vue';

type AdminProductIndexPage = ReturnType<typeof productIndexPage>
type DeleteResp = {
    status: number,
    messages: string[]
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
        {name: 'Name', dataPropertyName: 'name'},
        {name: 'Price', dataPropertyName: 'price'},
        {name: 'Size', dataPropertyName: 'size'},
        {name: 'Color', dataPropertyName: 'color'},
        {name: 'Taobao url', dataPropertyName: 'taobao_url'},
        {name: 'Description', dataPropertyName: 'description'},
        {name: 'Pictures', dataPropertyName: 'pictures'},
        {name: 'Categories', dataPropertyName: 'categories'},
        {name: 'Order point', dataPropertyName: 'orderPoint'},
        {name: 'Enabled', dataPropertyName: 'enable'}
    ];
}

const searchProduct = (keyword: string, component: AdminProductIndexPage): void => {
    const serverUrl: string = useAppConfig().serverUrl;
    const searchProductApi: string = `${serverUrl}/content/product/search`;
    console.log("Todo refactor product search api");
}

const deleteProduct = (data: TableData, component: AdminProductIndexPage): void => {
    component.isLoading = true;
    const serverUrl: string = useAppConfig().serverUrl;
    const deleteProductApi: string = `${serverUrl}/admin/product/delete`;
    const idProp: string = Object.keys(data)[0];
    const {readAuth} = useAuthInformation();
    const authPromise: Promise<Authentication | undefined> = readAuth();
    authPromise.then(async (auth: Authentication | undefined): Promise<void> => {
        let accessToken;
        let refreshToken;
        if (auth) {
            accessToken = auth.accessToken;
            refreshToken = auth.refreshToken;
        }
        const reqBody: { product_id: string } = {
            product_id: data[idProp]
        };
        const request: Request = {
            apiUrl: deleteProductApi,
            body: reqBody
        }
        const {deleteAsyncCall} = useXmlHttpRequest(request, accessToken ?? undefined, refreshToken ?? undefined);
        try {
            const resp: Response<DeleteResp> = await deleteAsyncCall<DeleteResp>();
            if (!resp.isError) {
                component.tableData = component.tableData.filter(v => v !== data);
                component.isLoading = false;
            }
        } catch (error) {
            component.hideAlert = false;
            component.isLoading = false;
            component.callServerError = (error.body as ErrorResponse).messages;
        }
    });
}

const getProductCategories = (component: AdminProductIndexPage): void => {
    const serverUrl: string = useAppConfig().serverUrl;
    const categoriesApi: string = `${serverUrl}/content/category/list`;
    const request: Request = {
        apiUrl: categoriesApi
    }
    const {readAuth} = useAuthInformation();
    const authPromise: Promise<Authentication | undefined> = readAuth();
    authPromise.then(async (auth: Authentication | undefined) => {
        if (auth) {
            const {getAsyncCall} = useXmlHttpRequest(request, auth.accessToken, auth.refreshToken);
            try {
                const resp: Response<Category[]> = await getAsyncCall<Category[]>();
                component.tableFilters.categories = (resp.body as Category[]).map((value: Category) => value.name);
            } catch (error) {
                component.tableFilters.categories = [];
            }
        }
    });
}

const getProductList = (component: AdminProductIndexPage): void => {
    const serverUrl: string = useAppConfig().serverUrl;
    const productsApi: string = `${serverUrl}/admin/product/list`;
    const request: Request = {
        apiUrl: productsApi
    }
    const {readAuth} = useAuthInformation();
    const authPromise: Promise<Authentication | undefined> = readAuth();
    authPromise.then(async (auth: Authentication | undefined) => {
        if (auth) {
            const {getAsyncCall} = useXmlHttpRequest(request, auth.accessToken, auth.refreshToken);
            try {
                const resp: Response<AdminProduct[]> = await getAsyncCall<AdminProduct[]>();
                component.tableData = (resp.body as AdminProduct[]);
                component.isLoading = false;
            } catch (error) {
                component.callServerError = (error.body as ErrorResponse).messages;
                component.hideAlert = false;
                component.isLoading = false;
            }
        }
    });
}

const navigateToProductInfoPage = (data: TableData): void => {
    const id = data.id;
    const infoPageUrl: string = `/admin/product/info?productId=${id}`;
    const navigate = useNavigation().value;
    navigate(infoPageUrl);
}

const navigateToCreatePage = (): void => {
    const navigate = useNavigation().value;
    navigate('/admin/product/create');
}

const hideAlert = (component: AdminProductIndexPage): void => {
    component.hideAlert = !component.hideAlert;
    component.callServerError = [];
}

export {
    navigationSources,
    searchProduct,
    deleteProduct,
    getProductCategories,
    getProductList,
    tableHeaders,
    navigateToProductInfoPage,
    navigateToCreatePage,
    hideAlert
}