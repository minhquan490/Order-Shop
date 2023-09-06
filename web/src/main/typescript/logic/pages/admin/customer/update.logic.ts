import { CustomerInfoResp, assignData } from "~/logic/pages/admin/customer/info.logic";
import infoPage from "~/pages/admin/customer/info.vue";
import { Authentication, District, ErrorResponse, Province, Request, Response, Ward } from "~/types";
import { onUnAuthorize } from "~/utils/NavigationUtils";
import { getDistrictFetchRequest, getProvinceFetchRequest, getWardFetchRequest } from "~/utils/RequestUtils";

type Page = InstanceType<typeof infoPage>;
type CustomerInfo = {
    id: string,
    first_name: string,
    last_name: string,
    phone: string,
    email: string,
    gender: string,
    role: string,
    username: string,
    address: Array<string>,
    order_point: number,
    is_activated: boolean,
    is_account_non_expired: boolean,
    is_account_non_locked: boolean,
    is_credentials_non_expired: boolean,
    is_enabled: boolean,
    picture: string
}
type CustomerUpdateInfo = {
    id: string,
    first_name: string,
    last_name: string,
    phone: string,
    email: string,
    gender: string,
    role: string,
    order_point: number,
    activated: boolean,
    account_non_expired: boolean,
    account_non_locked: boolean,
    credentials_non_expired: boolean,
    enabled: boolean,
    addresses: Array<Address>
}
type SelectedAddress = {
    house_number: string,
    ward: Ward,
    district: District,
    province: Province
}
type Address = {
    house_number: string,
    ward: string,
    district: string,
    province: string
}

const getGenderCheckboxValues = (): Array<string> => {
    return [
        'male',
        'female'
    ];
}

const getProvinces = async (): Promise<Array<Province>> => {
    const {readAuth} = useAuthInformation();
    const auth: Authentication | undefined = await readAuth();
    if (!auth) {
        onUnAuthorize();
        return Promise.resolve([]);
    } else {
        const request: Request = getProvinceFetchRequest();
        const {getAsyncCall} = useXmlHttpRequest(request, auth.accessToken, auth.refreshToken);
        const response: Response<Province[]> = await getAsyncCall<Province[]>();
        if (response.isError) {
            return Promise.resolve([]);
        } else {
            return Promise.resolve(response.body as Array<Province>);
        }
    }
}

const getDistricts = async (provinceId: string): Promise<Array<District>> => {
    const {readAuth} = useAuthInformation();
    const auth: Authentication | undefined = await readAuth();
    if (!auth) {
        onUnAuthorize();
        return Promise.resolve([]);
    } else {
        const request: Request = getDistrictFetchRequest(provinceId);
        const {getAsyncCall} = useXmlHttpRequest(request, auth.accessToken, auth.refreshToken);
        const response: Response<District[]> = await getAsyncCall<District[]>();
        if (response.isError) {
            return Promise.resolve([]);
        } else {
            return Promise.resolve(response.body as Array<District>);
        }
    }
}

const getWards = async (districtId: string): Promise<Array<Ward>> => {
    const {readAuth} = useAuthInformation();
    const auth: Authentication | undefined = await readAuth();
    if (!auth) {
        onUnAuthorize();
        return Promise.resolve([]);
    } else {
        const request: Request = getWardFetchRequest(districtId);
        const {getAsyncCall} = useXmlHttpRequest(request, auth.accessToken, auth.refreshToken);
        const response: Response<Ward[]> = await getAsyncCall<Ward[]>();
        if (response.isError) {
            return Promise.resolve([]);
        } else {
            return Promise.resolve(response.body as Array<Ward>);
        }
    }
}

const createDefaultCustomerUpdateInfo = (): CustomerUpdateInfo => {
    return {
        activated: false,
        id: '',
        order_point: -1,
        account_non_expired: false,
        account_non_locked: false,
        addresses: [],
        email: '',
        enabled: false,
        credentials_non_expired: false,
        first_name: '',
        gender: '',
        last_name: '',
        phone: '',
        role: ''
    };
}

const assignUpdatedData = (source: CustomerInfo, selectedAddresses: Array<SelectedAddress>): CustomerUpdateInfo => {
    const address: Array<Address> = selectedAddresses.map(value => {
        const transferred: Address = {
            house_number: value.house_number,
            province: value.province.id,
            ward: value.ward.id,
            district: value.district.id
        };
        return transferred;
    });
    return {
        id: source.id,
        first_name: source.first_name,
        last_name: source.last_name,
        phone: source.phone,
        email: source.email,
        gender: source.gender,
        role: source.role,
        order_point: source.order_point,
        activated: source.is_activated,
        account_non_expired: source.is_account_non_expired,
        account_non_locked: source.is_account_non_locked,
        credentials_non_expired: source.is_credentials_non_expired,
        enabled: source.is_enabled,
        addresses: address
    }
}

const createDefaultCustomerInfo = (): CustomerInfo => {
    return {
        address: [],
        email: '',
        first_name: '',
        gender: '',
        id: '',
        is_account_non_expired: false,
        is_account_non_locked: false,
        is_activated: false,
        is_enabled: false,
        last_name: '',
        phone: '',
        picture: '',
        role: '',
        is_credentials_non_expired: false,
        username: '',
        order_point: 0
    };
}

const clear = async (page: Page): Promise<void> => {
    (page.$refs['province'] as HTMLSelectElement).value = '';
    (page.$refs['district'] as HTMLSelectElement).value = '';
    (page.$refs['ward'] as HTMLSelectElement).value = '';
    (page.$refs['houseNumber'] as HTMLInputElement).value = '';
    return Promise.resolve();
}

const addAddress = (page: Page): void => {
    const address: SelectedAddress = {
        house_number: (page.$refs['houseNumber'] as HTMLInputElement).value,
        district: JSON.parse((page.$refs['district'] as HTMLSelectElement).value),
        ward: JSON.parse((page.$refs['ward'] as HTMLSelectElement).value),
        province: JSON.parse((page.$refs['province'] as HTMLSelectElement).value)
    };
    page.selectedAddresses.push(address);
}

const getUpdateCustomerRequest = (updateCustomerInfo: CustomerUpdateInfo): Request => {
    const serverUrl: string = useAppConfig().serverUrl;
    return {
        apiUrl: `${serverUrl}/admin/customer/update`,
        body: updateCustomerInfo
    };
}

const updateCustomer = async (updateCustomerInfo: CustomerUpdateInfo, page: Page): Promise<void> => {
    const auth: Authentication | undefined = await useAuthInformation().readAuth();
    if (!auth) {
        onUnAuthorize();
        return Promise.resolve();
    } else {
        const request: Request = getUpdateCustomerRequest(updateCustomerInfo);
        const {patchAsyncCall} = useXmlHttpRequest(request, auth.accessToken, auth.refreshToken);
        const response: Response<CustomerInfoResp> = await patchAsyncCall<CustomerInfoResp>();
        if (response.isError) {
            const error: ErrorResponse = response.body as ErrorResponse;
            page.alertErrorContents = error.messages;
        } else {
            assignData(page, response.body as CustomerInfoResp);
            if (page.selectedAddresses.length !== 0) {
                page.selectedAddresses = [];
            }
            page.alertInfoContents.push('Customer info is updated.');
            page.mode = 'view';
        }
    }
    page.isLoading = false;
    return Promise.resolve();
}

export {
    CustomerInfo,
    CustomerUpdateInfo,
    SelectedAddress,
    addAddress,
    assignUpdatedData,
    clear,
    createDefaultCustomerInfo,
    createDefaultCustomerUpdateInfo,
    getDistricts,
    getGenderCheckboxValues,
    getProvinces,
    getWards,
    updateCustomer
};
