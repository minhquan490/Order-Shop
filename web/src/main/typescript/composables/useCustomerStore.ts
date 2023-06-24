import { Customer } from "~/types/customer.type";

export const useCustomerStore = defineStore("customer-store", () => {
  const initState = (): Customer => {
    const result: Customer = {
      id: "",
      first_name: "",
      last_name: "",
      phone: "",
      email: "",
      gender: "",
      role: "",
      username: "",
      address: [],
      is_activated: false,
      is_account_non_expired: false,
      is_account_non_locked: false,
      is_credentials_non_expired: false,
      is_enabled: false,
      picture: "",
    };

    return result;
  };

  const selectedCustomer = ref<Customer>(initState());

  const getCustomer = computed(() => selectedCustomer.value);

  function setCustomer(customer: Customer) {
    selectedCustomer.value = customer;
  }

  function $reset() {
    selectedCustomer.value = initState();
  }

  return {
    selectedCustomer,
    getCustomer,
    setCustomer,
    $reset
  };
});
