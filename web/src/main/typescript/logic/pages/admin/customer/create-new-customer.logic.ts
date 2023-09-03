import {Authentication, District, ErrorResponse, NavBarsSource, Province, Request, Response, Ward} from "~/types";
import {currentPath, navSources} from "~/logic/components/navbars.logic";
import createNewCustomer from "~/pages/admin/customer/create-new-customer.vue";
import {onUnLogin} from "~/utils/NavigationUtils";
import {getDistrictFetchRequest, getProvinceFetchRequest, getWardFetchRequest} from "~/utils/RequestUtils";

type PageType = InstanceType<typeof createNewCustomer>;

type PageData = {
    roles: string[],
    provinces: Province[],
    districts: District[],
    wards: Ward[],
    customerCreateData: CustomerCreate,
    selectedData: SelectedData,
    validationError: PageValidationError,
    submitDataSuccessMsg: string,
    submitDataFailureMsg: Array<string>
}

type CustomerCreate = {
    first_name: string,
    last_name: string,
    phone: string,
    email: string,
    gender: string,
    role: string,
    username: string,
    password: string,
    address: Address
}

type SelectedData = {
    provinceId: string,
    districtId: string,
    wardId: string,
    houseAddress: string
}

type PageValidationError = {
    firstNameError: string,
    lastNameError: string,
    phoneError: string,
    emailError: string,
    genderError: string,
    roleError: string,
    usernameError: string,
    passwordError: string,
    provinceError: string,
    districtError: string,
    wardError: string,
    houseAddressError: string
}

type Address = {
    house_address: string,
    ward: string,
    district: string,
    province: string
}

const getCustomerCreateRequest = (): Request => {
    const serverUrl: string = useAppConfig().serverUrl;
    return {
        apiUrl: `${serverUrl}/admin/customer/create`
    }
}

const getAuthentication = async (): Promise<Authentication> => {
    const auth: Authentication | undefined = await useAuthInformation().readAuth();
    if (!auth) {
        onUnLogin();
    } else {
        return Promise.resolve(auth);
    }
}

const navigationSources = (): NavBarsSource[] => {
    return currentPath(
        navSources(),
        useRoute()
    );
}

const initPageData = async (): Promise<PageData> => {
    const auth: Authentication = await getAuthentication();
    const request: Request = getProvinceFetchRequest();
    const {getAsyncCall} = useXmlHttpRequest(request, auth?.accessToken, auth?.accessToken);
    const response: Response<Province[]> = await getAsyncCall<Province[]>();
    let provinces: Province[] = [];
    if (!response.isError) {
        provinces = response.body as Province[];
    }
    const {getRoles} = useRole();
    const result: PageData = {
        roles: getRoles(),
        provinces: provinces,
        wards: [],
        districts: [],
        customerCreateData: {
            email: '',
            first_name: '',
            gender: '',
            last_name: '',
            password: '',
            phone: '',
            role: '',
            username: '',
            address: {
                district: '',
                ward: '',
                house_address: '',
                province: ''
            }
        },
        selectedData: {
            districtId: '',
            provinceId: '',
            wardId: '',
            houseAddress: ''
        },
        validationError: {
            emailError: '',
            genderError: '',
            firstNameError: '',
            lastNameError: '',
            passwordError: '',
            phoneError: '',
            roleError: '',
            usernameError: '',
            provinceError: '',
            districtError: '',
            houseAddressError: '',
            wardError: ''
        },
        submitDataSuccessMsg: '',
        submitDataFailureMsg: []
    }
    return Promise.resolve(result);
}

const getDistrict = async (provinceId: string, page: PageType): Promise<District[]> => {
    const request: Request = getDistrictFetchRequest(provinceId);
    const auth: Authentication = await getAuthentication();
    const {getAsyncCall} = useXmlHttpRequest(request, auth.accessToken, auth.refreshToken);
    const resp: Response<District[]> = await getAsyncCall<District[]>();
    if (!resp.isError) {
        page.isLoading = false;
        return Promise.resolve(resp.body as District[]);
    } else {
        page.isLoading = false;
        return Promise.resolve([]);
    }
}

const getWard = async (districtId: string, page: PageType): Promise<{ name: string, id: string }[]> => {
    const request: Request = getWardFetchRequest(districtId);
    const auth: Authentication = await getAuthentication();
    const {getAsyncCall} = useXmlHttpRequest(request, auth.accessToken, auth.refreshToken);
    const resp: Response<Ward[]> = await getAsyncCall<Ward[]>();
    if (!resp.isError) {
        page.isLoading = false;
        return Promise.resolve(resp.body as { name: string, id: string }[]);
    } else {
        page.isLoading = false;
        return Promise.resolve([]);
    }
}

const validateInput = (pageData: PageData): boolean => {
    const submitData: CustomerCreate = pageData.customerCreateData;
    const selectedData: SelectedData = pageData.selectedData;
    const validationError: PageValidationError = pageData.validationError;
    validateFirstName(validationError, submitData.first_name);
    validateLastName(validationError, submitData.last_name);
    validateUsername(validationError, submitData.username);
    validatePhone(validationError, submitData.phone);
    validateEmail(validationError, submitData.email);
    validateGender(validationError, submitData.gender);
    validateRole(validationError, submitData.role);
    validatePassword(validationError, submitData.password);
    validateProvince(validationError, selectedData.provinceId);
    validateHouseAddress(validationError, selectedData.houseAddress);
    validateDistrict(validationError, selectedData.districtId);
    validateWard(validationError, selectedData.wardId);
    pageData.validationError = validationError;
    return pageData.validationError.passwordError.length === 0 &&
        pageData.validationError.usernameError.length === 0 &&
        pageData.validationError.roleError.length === 0 &&
        pageData.validationError.emailError.length === 0 &&
        pageData.validationError.genderError.length === 0 &&
        pageData.validationError.phoneError.length === 0 &&
        pageData.validationError.firstNameError.length === 0 &&
        pageData.validationError.lastNameError.length === 0;
}

const validateFirstName = (validationError: PageValidationError, firstName: string): void => {
    const firstNameLength: number = firstName.length;
    if (firstNameLength === 0) {
        validationError.firstNameError = 'First name is required !';
    } else if (firstNameLength < 4 || firstNameLength > 32) {
        validationError.firstNameError = 'First name length must be in range 4 - 32';
    }
}

const validateLastName = (validationError: PageValidationError, lastName: string): void => {
    const lastNameLength: number = lastName.length;
    if (lastNameLength === 0) {
        validationError.lastNameError = 'Last name is required !';
    } else if (lastNameLength < 4 || lastNameLength > 32) {
        validationError.lastNameError = 'Last name length must be in range 4 - 32';
    }
}

const validateRole = (validationError: PageValidationError, role: string): void => {
    if (role.length === 0) {
        validationError.roleError = 'Role must be choice';
    }
}

const validateUsername = (validationError: PageValidationError, username: string): void => {
    const usernameLength: number = username.length;
    if (usernameLength === 0) {
        validationError.usernameError = 'Username is required !';
    } else if (usernameLength < 4 || usernameLength > 32) {
        validationError.usernameError = 'Username length must be in range 4 - 32';
    }
}

const validatePhone = (validationError: PageValidationError, phone: string): void => {
    const phoneLength: number = phone.length;
    if (phoneLength === 0) {
        validationError.phoneError = 'Phone is required !';
    } else {
        const {phoneRegex} = useRegex();
        const result: boolean = phoneRegex.test(phone);
        if (!result) {
            validationError.phoneError = 'Phone is invalid';
        }
    }
}

const validateEmail = (validationError: PageValidationError, email: string): void => {
    const emailLength: number = email.length;
    if (emailLength === 0) {
        validationError.emailError = 'Email is required !';
    } else {
        const {emailRegex} = useRegex();
        const result: boolean = emailRegex.test(email);
        if (!result) {
            validationError.emailError = 'Email is invalid';
        }
    }
}

const validateGender = (validationError: PageValidationError, gender: string): void => {
    if (gender.length === 0) {
        validationError.genderError = 'Gender must be choice';
    }
}

const validatePassword = (validationError: PageValidationError, password: string): void => {
    const passwordLength: number = password.length;
    if (passwordLength === 0) {
        validationError.passwordError = 'Password is required !';
    } else {
        const {strongPasswordRegex} = useRegex();
        const result: boolean = strongPasswordRegex.test(password);
        if (!result) {
            validationError.passwordError = 'You password must have minimum 8 char, 1 uppercase, 1 lowercase, 1 number and 1 special char';
        }
    }
}

const validateProvince = (validationError: PageValidationError, provinceId: string): void => {
    if (provinceId.length === 0) {
        validationError.provinceError = 'Province must be choice';
    }
}

const validateDistrict = (validationError: PageValidationError, districtId: string): void => {
    if (districtId.length === 0) {
        validationError.districtError = 'District is required !';
    }
}

const validateWard = (validationError: PageValidationError, wardId: string): void => {
    if (wardId.length === 0) {
        validationError.wardError = 'Ward is required !';
    }
}

const validateHouseAddress = (validationError: PageValidationError, houseAddress: string): void => {
    if (houseAddress.length === 0) {
        validationError.houseAddressError = 'House number is required !';
    }
}

const clearError = (): PageValidationError => {
    return {
        houseAddressError: '',
        wardError: '',
        districtError: '',
        provinceError: '',
        passwordError: '',
        roleError: '',
        genderError: '',
        emailError: '',
        phoneError: '',
        usernameError: '',
        lastNameError: '',
        firstNameError: ''
    }
}

const submitData = async (page: PageType): Promise<void> => {
    const selectedData: SelectedData = page.pageData.selectedData;
    const customerCreateData: CustomerCreate = page.pageData.customerCreateData;
    customerCreateData.address.house_address = selectedData.houseAddress;
    customerCreateData.address.ward = selectedData.wardId;
    customerCreateData.address.province = selectedData.provinceId;
    customerCreateData.address.district = selectedData.districtId;

    const request: Request = getCustomerCreateRequest();
    const auth: Authentication = await getAuthentication();
    const {postAsyncCall} = useXmlHttpRequest(request, auth.accessToken, auth.refreshToken);
    const response: Response<CustomerCreate> = await postAsyncCall<CustomerCreate>();
    if (!response.isError) {
        page.pageData.submitDataSuccessMsg = 'Create customer successfully';
        page.hideListInfo = false;
    } else {
        page.pageData.submitDataFailureMsg = (response.body as ErrorResponse).messages;
        page.hideListError = false;
    }
    page.isLoading = false;
}

export {
    navigationSources,
    CustomerCreate,
    PageData,
    initPageData,
    getDistrict,
    getWard,
    validateInput,
    clearError,
    submitData
}