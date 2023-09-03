import {BasicCustomerInfo, Response} from "~/types";
import {RouteLocationNormalizedLoaded} from "vue-router";
import {onUnAuthorize} from "~/utils/NavigationUtils";

const checkAdminPermission = async (): Promise<void> => {
    const {isDefault} = useCustomerBasicInfo();
    const route: RouteLocationNormalizedLoaded = useRoute();
    if (isDefault) {
        await setUpState();
    }
    const {getCustomer} = useCustomerBasicInfo();
    if (route.fullPath.startsWith("/admin") && getCustomer.role !== 'ADMIN') {
        onUnAuthorize();
    }
    return Promise.resolve();
}

const checkCustomerPermission = async (): Promise<void> => {
    const {isDefault, getCustomer} = useCustomerBasicInfo();
    const route: RouteLocationNormalizedLoaded = useRoute();
    if (isDefault) {
        await setUpState();
    }
    if (route.fullPath.startsWith("/customer") && getCustomer.role !== 'CUSTOMER') {
        onUnAuthorize();
    }
    return Promise.resolve();
}

const setUpState = async (): Promise<void> => {
    const {canAccess} = usePageAccessPermission();
    const resp: Response<BasicCustomerInfo> | undefined = await canAccess();
    if (!resp || resp.isError) {
        onUnAuthorize();
    } else {
        const responseBody: BasicCustomerInfo = resp.body as BasicCustomerInfo;
        const {setCustomer} = useCustomerBasicInfo();
        setCustomer(responseBody);
    }
    return Promise.resolve();
}

export {
    checkAdminPermission,
    checkCustomerPermission
}