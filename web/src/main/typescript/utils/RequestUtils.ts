import { Request } from "~/types";

const getProvinceFetchRequest = (): Request => {
    const serverUrl: string = useAppConfig().serverUrl;
    return {
        apiUrl: `${serverUrl}/content/province/list`
    }
}

const getDistrictFetchRequest = (provinceId: string): Request => {
    const serverUrl: string = useAppConfig().serverUrl;
    return {
        apiUrl: `${serverUrl}/content/district/list-with-province`,
        queryParams: [
            {name: 'provinceId', value: provinceId}
        ]
    }
}

const getWardFetchRequest = (districtId: string): Request => {
    const serverUrl: string = useAppConfig().serverUrl;
    return {
        apiUrl: `${serverUrl}/content/ward/list-with-district`,
        queryParams: [
            {name: 'districtId', value: districtId}
        ]
    }
}

const checkCustomerIdQueryParam = (): string | undefined => {
    const customerIdQuery = useRoute().query['customerId'];
    if (!customerIdQuery) {
        const navigate = useNavigation().value;
        navigate('/404');
        return undefined;
    }
    if (Array.isArray(customerIdQuery)) {
        return (customerIdQuery as Array<string>)[0];
    }
    return customerIdQuery;
}

export {
    checkCustomerIdQueryParam,
    getDistrictFetchRequest,
    getProvinceFetchRequest,
    getWardFetchRequest
};

