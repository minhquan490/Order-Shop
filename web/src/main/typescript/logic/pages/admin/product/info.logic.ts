import adminProductInfoPage from '~/pages/admin/product/info.vue';
import {
    AdminProduct,
    Authentication,
    CarouselItem,
    Category,
    ErrorResponse,
    NavBarsSource,
    Request,
    Response
} from "~/types";
import {currentPath, navSources} from "~/logic/components/navbars.logic";

type AdminProductInfoPage = InstanceType<typeof adminProductInfoPage>
type CategoryResp = {
    category_id: string,
    category_name: string
};
type ProductUpdateForm = {
    product_id: string,
    product_name: string,
    product_price: string,
    product_size: string,
    product_color: string,
    product_taobao_url: string,
    product_description: string,
    product_enabled: string,
    product_categories: Array<string>,
    product_order_point: string
}
type ProductMediaDeleteRequest = {
    media_url: string
}

const navigationSources = (): NavBarsSource[] => {
    return currentPath(
        navSources(),
        useRoute()
    );
}

const carouselItems = (): CarouselItem[] => {
    return [
        {pictureUrl: '/carousel-no-image-available.svg', current: true},
    ];
}

const checkPageQueryParam = (component: AdminProductInfoPage): void => {
    if ((!component.query?.productId) || Array.isArray(component.query.productId) || component.query.productId.length === 0) {
        const navigate = useNavigation().value;
        navigate('/404', true, 301);
    }
}

const getProduct = (productId: string, component: AdminProductInfoPage): void => {
    const serverUrl = useAppConfig().serverUrl;
    const productInfoApi: string = `${serverUrl}/content/product?id=${productId}`;
    const request: Request = {
        apiUrl: productInfoApi
    };
    const {getAsyncCall} = useXmlHttpRequest(request);
    const resp: Promise<Response<AdminProduct>> = getAsyncCall<AdminProduct>();
    resp.then((res: Response<AdminProduct>): void => {
        component.isLoading = false;
        component.product = res.body as AdminProduct;
        component.slideItems = component.product.pictures.map((pic: string) => {
            return {
                pictureUrl: pic,
                current: false
            } as CarouselItem;
        })
        component.slideItems[0].current = true;
    }).catch((): void => {
        component.isLoading = false;
        const navigate = useNavigation().value;
        navigate('/404');
    });
}

const createDefaultProduct = (): AdminProduct => {
    return {
        name: '',
        size: '',
        taobao_url: '',
        price: '',
        pictures: [],
        categories: [],
        description: '',
        color: '',
        id: '',
        enable: false,
        orderPoint: ''
    };
};

const getCategories = (component: AdminProductInfoPage): void => {
    const serverUrl = useAppConfig().serverUrl;
    const categoryApi: string = `${serverUrl}/content/category/list`;
    const request: Request = {
        apiUrl: categoryApi
    };
    const {getAsyncCall} = useXmlHttpRequest(request);
    const response: Promise<Response<CategoryResp[]>> = getAsyncCall<CategoryResp[]>();
    response.then((value: Response<CategoryResp[]>): void => {
        component.categoriesSource = (value.body as CategoryResp[]).map((res: CategoryResp) => {
            return {
                name: res.category_name,
                id: res.category_id
            } as Category;
        })
    }).catch(error => console.log(error.body.messages.join(', ')));
}

const onMounted = (component: AdminProductInfoPage): void => {
    getCategories(component);
    getProduct(component.query.productId as string, component);
    component.initSelectedCategory = component.categoriesSource.filter((value: Category) => component.product.categories.includes(value.name));
}

const submitUpdate = (component: AdminProductInfoPage): void => {
    component.isLoading = true;
    const product: AdminProduct = component.product;
    const requestBody: ProductUpdateForm = {
        product_id: product.id,
        product_name: product.name,
        product_price: product.price,
        product_size: product.size,
        product_color: product.color,
        product_description: product.description,
        product_enabled: product.enable.toString(),
        product_order_point: product.orderPoint,
        product_taobao_url: product.taobao_url,
        product_categories: component.initSelectedCategory.map(category => category.id)
    }
    const serverUrl: string = useAppConfig().serverUrl;
    const updateProductApi: string = `${serverUrl}/admin/product/update`;
    const request: Request = {
        apiUrl: updateProductApi,
        body: requestBody
    }
    const {patchAsyncCall} = useXmlHttpRequest(request);
    const response: Promise<Response<AdminProduct>> = patchAsyncCall<AdminProduct>();
    response.then((resp: Response<AdminProduct>): void => {
        component.isLoading = false;
        component.product = resp.body;
        component.updateMode = false;
    }).catch(err => {
        component.isLoading = false;
        component.hideAlert = false;
        component.updateMode = false;
        component.updateErrors = err.body.messages;
    });
}

const deletePicture = (component: AdminProductInfoPage): void => {
    component.isLoading = true;
    const clientPics: string[] = useAppConfig().clientPics;
    const selectedPic: string = component.pictureSelected;
    if (clientPics.includes(selectedPic)) {
        return;
    }
    const serverUrl = useAppConfig().serverUrl;
    const deletePicUrl: string = `${serverUrl}/admin/product/media/delete`;
    const requestBody: ProductMediaDeleteRequest = {
        media_url: selectedPic
    }
    const request: Request = {
        apiUrl: deletePicUrl,
        body: requestBody
    }
    const {deleteAsyncCall} = useXmlHttpRequest(request);
    const response: Promise<Response<undefined>> = deleteAsyncCall<undefined>();
    response.then((res: Response<undefined>): void => {
        if (res.statusCode === 200) {
            component.hideConfirmAlert = true;
            component.pictureSelected = '';
            component.isLoading = false;
        }
    }).catch(error => {
        component.isLoading = false;
        component.updateErrors = (error.body as ErrorResponse).messages;
        component.hideAlert = false;
    });
}

const uploadFile = async (event: Event): Promise<void> => {
    const {uploadMultiFile} = useFileUpload();
    const inputElement: HTMLInputElement = event.target as HTMLInputElement;
    const {readAuth} = useAuthInformation();
    const auth: Authentication | undefined = await readAuth();
    if (!auth) {
        const navigate = useNavigation().value;
        navigate('/403');
        return;
    }
    return uploadMultiFile(inputElement.files, this.product.id, auth.accessToken, auth.refreshToken);
}

export {
    checkPageQueryParam,
    navigationSources,
    carouselItems,
    getProduct,
    createDefaultProduct,
    onMounted,
    getCategories,
    submitUpdate,
    deletePicture,
    uploadFile
}