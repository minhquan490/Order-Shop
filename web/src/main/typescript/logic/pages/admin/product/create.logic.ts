import {Category, ErrorResponse, NavBarsSource, Request} from "~/types";
import {currentPath, navSources} from "~/logic/components/navbars.logic";
import productCreatePage from '~/pages/admin/product/create.vue';

type ProductCreatePage = InstanceType<typeof productCreatePage>
type RequestType = ReturnType<typeof productCreatePage.methods.initRequest>
type ProductCreateResp = {
    id: string,
    name: string,
    price: string,
    size: string,
    color: string,
    taobao_url: string,
    description: string,
    pictures: string[],
    categories: string[]
}

const navigationSources = (): NavBarsSource[] => {
    return currentPath(
        navSources(),
        useRoute()
    );
}

const selectCategory = (category: Category, component: ProductCreatePage): void => {
    if (component.selectedCategories.indexOf(category) >= 0) {
        return;
    }
    component.selectedCategories.push(category);
}

const removeCategory = (category: Category, component: ProductCreatePage): void => {
    component.selectedCategories = component.selectedCategories.filter(cate => cate !== category);
}

const setProductEnabled = (event: Event, component: ProductCreatePage): void => {
    event.preventDefault();
    const target: HTMLInputElement = event.target as HTMLInputElement;
    component.request.product_enabled = target.checked.toString();
}

const validateProductName = (request: RequestType, component: ProductCreatePage): boolean => {
    if (request.product_name.length === 0) {
        component.validationError.productNameError = 'Name of product is required !';
        setTimeout((): void => {
            component.validationError.productNameError = '';
        }, 3000);
        return false;
    }
    if (request.product_name.length < 4 || request.product_name.length > 32) {
        component.validationError.productNameError = 'Length of product name must in range 4 to 32 character';
        setTimeout((): void => {
            component.validationError.productNameError = '';
        }, 3000);
        return false;
    }
    return true;
}

const validateProductPrice = (request: RequestType, component: ProductCreatePage): boolean => {
    if (request.product_price.length === 0) {
        component.validationError.productPriceError = 'Price of product is required !';
        setTimeout((): void => {
            component.validationError.productPriceError = '';
        }, 3000);
        return false;
    }
    const {numberRegex} = useRegex();
    if (!numberRegex.test(request.product_price)) {
        component.validationError.productPriceError = 'Price of product must be number';
        setTimeout((): void => {
            component.validationError.productPriceError = '';
        }, 3000);
        return false;
    }
    return true;
}

const validateProductSize = (request: RequestType, component: ProductCreatePage): boolean => {
    if (request.product_size.length === 0) {
        component.validationError.productSizeError = 'Size of product is required !';
        setTimeout((): void => {
            component.validationError.productSizeError = '';
        }, 3000);
        return false;
    }
    const productSizeReg: RegExp = useRegex().productSizeRegex;
    if (!productSizeReg.test(request.product_size)) {
        component.validationError.productSizeError = 'Size of product must be S, M, L, XL';
        setTimeout((): void => {
            component.validationError.productSizeError = '';
        }, 3000);
        return false;
    }
    return true;
}

const validateProductColor = (request: RequestType, component: ProductCreatePage): boolean => {
    if (request.product_color.length === 0) {
        component.validationError.productColorError = 'Color of product is required !';
        setTimeout((): void => {
            component.validationError.productColorError = '';
        }, 3000);
        return false;
    }
    return true;
}

const validateTaobaoUrl = (request: RequestType, component: ProductCreatePage): boolean => {
    if (request.product_taobao_url.length === 0) {
        component.validationError.productTaobaoUrlError = 'Taobao url of product is required !';
        setTimeout((): void => {
            component.validationError.productTaobaoUrlError = '';
        }, 3000);
        return false;
    }
    const urlReg: RegExp = useRegex().httpRegex;
    if (!urlReg.test(request.product_taobao_url)) {
        component.validationError.productTaobaoUrlError = 'Taobao url is not a https url';
        setTimeout((): void => {
            component.validationError.productTaobaoUrlError = '';
        }, 3000);
        return false;
    }
    return true;
}

const validateCategory = (request: RequestType, component: ProductCreatePage): boolean => {
    if (request.product_categories.length === 0) {
        component.validationError.productCategoriesError = 'Product require at least one category';
        setTimeout((): void => {
            component.validationError.productCategoriesError = '';
        }, 3000);
        return false;
    }
    return true;
}

const validateProductOrderPoint = (request: RequestType, component: ProductCreatePage): boolean => {
    if (request.product_order_point.length === 0) {
        component.validationError.productOrderPointError = 'Order point of product is required !';
        setTimeout((): void => {
            component.validationError.productOrderPointError = '';
        }, 3000);
        return false;
    }
    const {numberRegex} = useRegex();
    if (!numberRegex.test(request.product_order_point)) {
        component.validationError.productOrderPointError = 'Order point must be a number';
        setTimeout((): void => {
            component.validationError.productOrderPointError = '';
        }, 3000);
        return false;
    }
    if (Number.parseInt(request.product_order_point) < 0) {
        component.validationError.productOrderPointError = 'Order point must be positive';
        setTimeout((): void => {
            component.validationError.productOrderPointError = '';
        }, 3000);
        return false;
    }
    return true;
}

const validateBeforeSubmit = (request: RequestType, component: ProductCreatePage): boolean => {
    const isProductNameValid: boolean = validateProductName(request, component);
    const isProductPriceValid: boolean = validateProductPrice(request, component);
    const isProductSizeValid: boolean = validateProductSize(request, component);
    const isProductColorValid: boolean = validateProductColor(request, component);
    const isTaobaoUrlValid: boolean = validateTaobaoUrl(request, component);
    const isCategoriesValid: boolean = validateCategory(request, component);
    const isProductOrderPointValid: boolean = validateProductOrderPoint(request, component);
    return isProductNameValid &&
        isProductPriceValid &&
        isProductSizeValid &&
        isProductColorValid &&
        isTaobaoUrlValid &&
        isCategoriesValid &&
        isProductOrderPointValid;
}

const submitRequest = (request: RequestType, component: ProductCreatePage): void => {
    const serverUrl: string = useAppConfig().serverUrl;
    const apiUrl: string = `${serverUrl}/admin/product/create`;
    const {readAuth} = useAuthInformation();
    readAuth().then(async auth => {
        let accessToken;
        let refreshToken;
        if (auth) {
            accessToken = auth.accessToken;
            refreshToken = auth.refreshToken;
        }
        const actualRequest: Request = {
            apiUrl: apiUrl,
            body: request
        };
        const {postAsyncCall} = useXmlHttpRequest(actualRequest, accessToken ?? undefined, refreshToken ?? undefined);
        try {
            await postAsyncCall<ProductCreateResp>();
            component.isLoading = false;
            alert("Create product successfully");
            const navigate = useNavigation().value;
            component.isSubmitted = false;
            navigate('/admin/product');
        } catch (error) {
            component.isLoading = false;
            component.callServerError = (error.body as ErrorResponse).messages;
            component.hideAlert = false;
            component.isSubmitted = false;
        }
    })
}

export {
    navigationSources,
    selectCategory,
    removeCategory,
    setProductEnabled,
    validateBeforeSubmit,
    validateProductName,
    validateProductPrice,
    validateProductSize,
    validateCategory,
    validateProductColor,
    validateTaobaoUrl,
    validateProductOrderPoint,
    submitRequest
}