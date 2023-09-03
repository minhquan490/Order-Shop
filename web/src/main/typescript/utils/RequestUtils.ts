import {Request} from "~/types";

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

export {
    getWardFetchRequest,
    getProvinceFetchRequest,
    getDistrictFetchRequest
}