import {BasicCustomerInfo} from "~/types";
import {ComputedRef, UnwrapRef} from "vue";

const initState = (): BasicCustomerInfo => {
    return {
        id: '',
        avatarUrl: '',
        firstName: '',
        lastName: '',
        role: ''
    };
}

export const useCustomerBasicInfo = defineStore('customer', () => {
    const customer = ref(initState());

    const getCustomer: ComputedRef<BasicCustomerInfo> = computed(() => {
        const returnValue: BasicCustomerInfo = customer.value;
        return returnValue;
    });
    const isDefault: ComputedRef<boolean> = computed((): boolean => {
        const cus: UnwrapRef<BasicCustomerInfo> = customer.value;
        return cus.id === '' && cus.role === '' && cus.avatarUrl === '' && cus.firstName === '' && cus.firstName === '';
    });

    function setCustomer(newCustomer: BasicCustomerInfo) {
        customer.value = newCustomer;
    }

    return {
        customer,
        getCustomer,
        isDefault,
        setCustomer
    };
});
