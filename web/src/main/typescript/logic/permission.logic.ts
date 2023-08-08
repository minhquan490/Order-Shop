import {BasicCustomerInfo, Response} from "~/types";

const checkAdminPermission = async (): Promise<void> => {
    const {isDefault, getCustomer} = useCustomerBasicInfo();
    const route = useRoute();
    if (isDefault) {
        await setUpState();
    }
    if (route.fullPath.startsWith("/admin") && getCustomer.role !== 'ADMIN') {
        const navigate = useNavigation().value;
        navigate('/403');
    }
    return Promise.resolve();
}

const checkCustomerPermission = async (): Promise<void> => {
    const {isDefault, getCustomer} = useCustomerBasicInfo();
    const route = useRoute();
    if (isDefault) {
        await setUpState();
    }
    if (route.fullPath.startsWith("/customer") && getCustomer.role !== 'CUSTOMER') {
        const navigate = useNavigation().value;
        navigate('/403');
    }
    return Promise.resolve();
}

const setUpState = async (): Promise<void> => {
    const navigate = useNavigation().value;
    const {canAccess} = usePageAccessPermission();
    const resp: Response<BasicCustomerInfo> | undefined = await canAccess();
    if (!resp || resp.statusCode >= 400) {
        navigate('/403');
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