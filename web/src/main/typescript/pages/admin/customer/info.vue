<script lang="ts">
import {
  assignData,
  checkCustomerIdQueryParam,
  createCarouselItems,
  defaultCarouselItem,
  getAccessHistoriesTableHeader,
  getAssignedVoucherTableHeader,
  getChangeHistoryTableHeader,
  getCustomerInfo,
  getDefaultAccessHistory,
  getDefaultAssignedVoucher,
  getDefaultChangeHistory,
  getDefaultLoginHistory,
  getDefaultOrderOfCustomer,
  getLoginHistoriesTableHeader,
  getOrderOfCustomerTableHeader,
  navigationSources,
  redirectToCustomerAccessHistoriesPage,
  redirectToCustomerLoginHistoriesPage,
  redirectToOrdersOfCustomerPage,
  redirectToUpdatedDataHistoriesPage,
  redirectToVouchersOfCustomerPage
} from "~/logic/pages/admin/customer/info.logic";
import {
  addAddress,
  assignUpdatedData,
  clear,
  createDefaultCustomerInfo,
  createDefaultCustomerUpdateInfo,
  CustomerInfo,
  CustomerUpdateInfo,
  getDistricts,
  getGenderCheckboxValues,
  getProvinces,
  getWards,
  SelectedAddress,
  updateCustomer
} from "~/logic/pages/admin/customer/update.logic";
import {District, NavBarsSource, Province, TableHeader, Ward} from "~/types";

export default {
  setup() {
    const customerIdQuery = checkCustomerIdQueryParam();
    const accessHistoriesTableHeader: Array<TableHeader> = getAccessHistoriesTableHeader();
    const assignedVoucherTableHeader: Array<TableHeader> = getAssignedVoucherTableHeader();
    const orderOfCustomerTableHeader: Array<TableHeader> = getOrderOfCustomerTableHeader();
    const loginHistoriesTableHeader: Array<TableHeader> = getLoginHistoriesTableHeader();
    const changeHistoriesTableHeader: Array<TableHeader> = getChangeHistoryTableHeader();
    const navigationSource: NavBarsSource[] = navigationSources();
    const defaultCarousel = defaultCarouselItem();
    const genderCheckboxValues = getGenderCheckboxValues();
    const {getRoles} = useRole();
    const roles = getRoles();
    return {
      customerIdQuery,
      accessHistoriesTableHeader,
      assignedVoucherTableHeader,
      orderOfCustomerTableHeader,
      loginHistoriesTableHeader,
      changeHistoriesTableHeader,
      navigationSource,
      defaultCarousel,
      genderCheckboxValues,
      roles
    }
  },
  data() {
    const accessHistories: Array<AccessHistory> = [];
    const assignedVouchers: Array<AssignedVoucher> = [];
    const ordersOfCustomer: Array<OrderOfCustomer> = [];
    const loginHistories: Array<LoginHistory> = [];
    const changeHistories: Array<InfoChangeHistory> = [];
    const isLoading: boolean = true;
    const mode: string = "view";
    const provinceSource: Province[] = [];
    const districtSource: District[] = [];
    const wardSource: Ward[] = [];
    const selectedAddresses: Array<SelectedAddress> = [];
    const hideModal: boolean = true;
    const rotateAddressIcon: boolean = false;
    const customerInfo: CustomerInfo = createDefaultCustomerInfo();
    const customerUpdateInfo: CustomerUpdateInfo = createDefaultCustomerUpdateInfo();
    const alertErrorContents: Array<string> = [];
    const alertInfoContents: Array<string> = [];
    return {
      accessHistories,
      assignedVouchers,
      ordersOfCustomer,
      loginHistories,
      changeHistories,
      customerInfo,
      isLoading,
      mode,
      provinceSource,
      districtSource,
      wardSource,
      customerUpdateInfo,
      selectedAddresses,
      hideModal,
      rotateAddressIcon,
      alertErrorContents,
      alertInfoContents
    }
  },
  async mounted() {
    const customerInfo = await getCustomerInfo(this.customerIdQuery);
    if (customerInfo) {
      assignData(this, customerInfo);
      this.defaultCarousel = createCarouselItems(customerInfo.picture ?? '');
      this.isLoading = false;
    }
  },
  methods: {
    changeMode(mode: string) {
      if (mode === 'view') {
        this.mode = 'update';
        if (this.provinceSource.length === 0) {
          getProvinces().then(value => this.provinceSource = value);
        }
      }
      if (mode === 'update') {
        this.mode = 'view';
        this.districtSource = [];
        this.wardSource = [];
      }
    },
    bindGender(event: Event) {
      const inputTarget: HTMLInputElement = event.target as HTMLInputElement;
      if (inputTarget.checked) {
        this.customerInfo.gender = inputTarget.value;
      } else {
        this.customerInfo.gender = '';
      }
    },
    fetchDistrict(province: Province) {
      getDistricts(province.id).then(value => this.districtSource = value);
    },
    fetchWard(district: District) {
      getWards(district.id).then(value => this.wardSource = value);
    },
    closeModal() {
      this.hideModal = !this.hideModal;
      clear(this);
    },
    rotateIcon() {
      this.rotateAddressIcon = !this.rotateAddressIcon;
    },
    toggleModal() {
      this.hideModal = !this.hideModal;
    },
    addAddress() {
      addAddress(this);
      this.closeModal();
    },
    updateCustomer() {
      this.isLoading = true;
      const updateInfo = assignUpdatedData(this.customerInfo, toRaw(this.selectedAddresses));
      updateCustomer(updateInfo, this);
    },
    removeSelectedAddress(selectedAddress: SelectedAddress) {
      const index = this.selectedAddresses.indexOf(selectedAddress);
      if (index > -1) {
        this.selectedAddresses.splice(index, 1);
      }
    },
    hideErrorAlert() {
      this.alertErrorContents = [];
    },
    processSelectedAddress(selectedAddress: SelectedAddress): string {
      return selectedAddress.house_number + ', ' + selectedAddress.ward.name + ', ' + selectedAddress.district.name + ', ' + selectedAddress.province.name;
    },
    bindAccountNonExpired(event: Event) {
      event.preventDefault();
      const inputElement: HTMLInputElement = event.target as HTMLInputElement;
      this.customerInfo.is_account_non_expired = inputElement.checked;
    },
    bindAccountNonLocked(event: Event) {
      event.preventDefault();
      const inputElement: HTMLInputElement = event.target as HTMLInputElement;
      this.customerInfo.is_account_non_locked = inputElement.checked;
    },
    bindCredentialsNonLocked(event: Event) {
      event.preventDefault();
      const inputElement: HTMLInputElement = event.target as HTMLInputElement;
      this.customerInfo.is_credentials_non_expired = inputElement.checked;
    },
    bindEnabled(event: Event) {
      event.preventDefault();
      const inputElement: HTMLInputElement = event.target as HTMLInputElement;
      this.customerInfo.is_enabled = inputElement.checked;
    },
    bindActivated(event: Event) {
      event.preventDefault();
      const inputElement: HTMLInputElement = event.target as HTMLInputElement;
      this.customerInfo.is_activated = inputElement.checked;
    },
    navigateToCustomerLoginHistories(customerId: string) {
      redirectToCustomerLoginHistoriesPage(customerId);
    },
    navigateToUpdatedDataHistories(customerId: string) {
      redirectToUpdatedDataHistoriesPage(customerId);
    },
    navigateToVoucherOfCustomer(customerId: string) {
      redirectToVouchersOfCustomerPage(customerId);
    },
    navigateToOrderOfCustomer(customerId: string) {
      redirectToOrdersOfCustomerPage(customerId);
    },
    navigateToAccessPageHistories(customerId: string) {
      redirectToCustomerAccessHistoriesPage(customerId);
    },
    hideInfoAlert() {
      this.alertInfoContents = [];
    }
  }
}

type AccessHistory = ReturnType<typeof getDefaultAccessHistory>;
type AssignedVoucher = ReturnType<typeof getDefaultAssignedVoucher>;
type OrderOfCustomer = ReturnType<typeof getDefaultOrderOfCustomer>;
type LoginHistory = ReturnType<typeof getDefaultLoginHistory>;
type InfoChangeHistory = ReturnType<typeof getDefaultChangeHistory>;
</script>

<template>
  <div class="relative">
    <loading-block-u-i :loading="isLoading">
      <nav-bars-admin :sources="navigationSource"/>
      <page-header title="Customer information" :bread-crumbs-enable="true"/>
      <admin-content-area>
        <div class="pb-4 flex justify-between">
          <h1 v-text="customerInfo.id"></h1>
          <div>
            <button v-if="mode === 'view'" type="button"
                    class="focus:outline-none text-white bg-red-500 hover:bg-red-800 focus:ring-4 focus:ring-red-300 font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2">
              Delete
            </button>
            <button v-if="mode === 'view'" type="button"
                    class="focus:outline-none text-white bg-green-600 hover:bg-green-800 focus:ring-4 focus:ring-green-300 font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2"
                    @click="changeMode(mode)">
              Edit customer
            </button>
            <a v-if="mode === 'update'"
               class="inline-flex items-center font-medium text-blue-600 dark:text-blue-500 hover:underline hover:cursor-pointer"
               @click="changeMode(mode)">
              Return customer information
              <svg class="w-4 h-4 ml-2" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none"
                   viewBox="0 0 14 10">
                <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M1 5h12m0 0L9 1m4 4L9 9"/>
              </svg>
            </a>
          </div>
        </div>
        <template v-if="mode === 'view'">
          <div class="w-full grid grid-cols-3 gap-x-6">
            <div class="col-span-1">
              <carousel :items="defaultCarousel"/>
            </div>
            <div class="col-span-2 bg-white rounded-lg customer-basic-info">
              <div class="grid grid-cols-2 gap-10 p-4" v-if="mode === 'view'">
                <div class="col-span-1 flex border-b">
                  <p class="pr-4 hover:cursor-default">Username: </p>
                  <span class="hover:cursor-default" v-text="customerInfo.username"></span>
                </div>
                <div class="col-span-1 flex border-b">
                  <p class="pr-4 hover:cursor-default">Order point: </p>
                  <span class="hover:cursor-default" v-text="customerInfo.order_point"></span>
                </div>
                <div class="col-span-1 flex border-b">
                  <p class="pr-4 hover:cursor-default">First name: </p>
                  <span class="hover:cursor-default" v-text="customerInfo.first_name"></span>
                </div>
                <div class="col-span-1 flex border-b">
                  <p class="pr-4 hover:cursor-default">Last name: </p>
                  <span class="hover:cursor-default" v-text="customerInfo.last_name"></span>
                </div>
                <div class="col-span-1 flex border-b">
                  <p class="pr-4 hover:cursor-default">Phone: </p>
                  <span class="hover:cursor-default" v-text="customerInfo.phone"></span>
                </div>
                <div class="col-span-1 flex border-b">
                  <p class="pr-4 hover:cursor-default">Email: </p>
                  <span class="hover:cursor-default" v-text="customerInfo.email"></span>
                </div>
                <div class="col-span-1 flex border-b">
                  <p class="pr-4 hover:cursor-default">Gender: </p>
                  <div class="flex items-center">
                    <p class="pr-2 hover:cursor-default">Male</p>
                    <template v-if="customerInfo.gender.toLowerCase() === 'male'">
                      <Icon name="gg:check-o" color="green" width="18" height="18"/>
                    </template>
                    <template v-else>
                      <Icon name="typcn:delete-outline" color="red" width="24" height="24"/>
                    </template>
                  </div>
                  <div class="px-6">
                    <p class="hover:cursor-default">-</p>
                  </div>
                  <div class="flex items-center">
                    <p class="pr-2 hover:cursor-default">Female</p>
                    <template v-if="customerInfo.gender.toLowerCase() === 'female'">
                      <Icon name="gg:check-o" color="green" width="18" height="18"/>
                    </template>
                    <template v-else>
                      <Icon name="typcn:delete-outline" color="red" width="24" height="24"/>
                    </template>
                  </div>
                </div>
                <div class="col-span-1 flex border-b">
                  <p class="pr-4 hover:cursor-default">Role: </p>
                  <span class="hover:cursor-default" v-text="customerInfo.role"></span>
                </div>
                <div class="col-span-2">
                  <p class="hover:cursor-default">Address: </p>
                  <textarea class="w-full" rows="2" disabled :value="customerInfo.address.join('\n')"></textarea>
                </div>
              </div>
            </div>
          </div>
          <div class="w-full mt-8 pt-2 border-t border-gray-600 grid grid-cols-2">
            <div class="col-span-1 border-r border-gray-600 px-2 pb-8 mb-2">
              <h1 class="text-center">Account information</h1>
              <div class="bg-white rounded-md account-info px-4 mt-2 pb-2">
                <div class="flex items-center">
                  <span class="hover:cursor-default">Account activated: </span>
                  <template v-if="customerInfo.is_activated">
                    <Icon name="typcn:tick" color="green" width="36" height="36"/>
                  </template>
                  <template v-else>
                    <Icon name="iconamoon:close-bold" color="red" width="36" height="36"/>
                  </template>
                </div>
                <div class="flex items-center pt-4">
                  <span class="hover:cursor-default">Account non expired: </span>
                  <template v-if="customerInfo.is_account_non_expired">
                    <Icon name="typcn:tick" color="green" width="36" height="36"/>
                  </template>
                  <template v-else>
                    <Icon name="iconamoon:close-bold" color="red" width="36" height="36"/>
                  </template>
                </div>
                <div class="flex items-center pt-4">
                  <span class="hover:cursor-default">Account non locked: </span>
                  <template v-if="customerInfo.is_account_non_locked">
                    <Icon name="typcn:tick" color="green" width="36" height="36"/>
                  </template>
                  <template v-else>
                    <Icon name="iconamoon:close-bold" color="red" width="36" height="36"/>
                  </template>
                </div>
                <div class="flex items-center pt-4">
                  <span class="hover:cursor-default">Credentials non expired: </span>
                  <template v-if="customerInfo.is_credentials_non_expired">
                    <Icon name="typcn:tick" color="green" width="36" height="36"/>
                  </template>
                  <template v-else>
                    <Icon name="iconamoon:close-bold" color="red" width="36" height="36"/>
                  </template>
                </div>
                <div class="flex items-center pt-4">
                  <span class="hover:cursor-default">Enabled: </span>
                  <template v-if="customerInfo.is_enabled">
                    <Icon name="typcn:tick" color="green" width="36" height="36"/>
                  </template>
                  <template v-else>
                    <Icon name="iconamoon:close-bold" color="red" width="36" height="36"/>
                  </template>
                </div>
              </div>
            </div>
            <div class="col-span-1 px-2">
              <div class="relative">
                <h1 class="text-center">Access page histories</h1>
                <a v-if="accessHistories.length !== 0"
                   class="font-medium text-blue-600 dark:text-blue-500 hover:underline absolute right-0 top-0 flex items-center text-sm hover:cursor-pointer"
                   @click="navigateToAccessPageHistories(customerInfo.id)">
                  See more
                  <Icon name="bxs:up-arrow" color="blue" width="12" height="12" :rotate="1"/>
                </a>
              </div>
              <div class="mt-2 rounded-md bg-white account-info">
                <view-data-table :headers="accessHistoriesTableHeader" :data="accessHistories"/>
              </div>
            </div>
          </div>
          <div class="w-full pt-2 border-t border-gray-600 grid grid-cols-2">
            <div class="col-span-1 border-r border-gray-600 px-2 pb-8 mb-2">
              <div class="relative">
                <h1 class="text-center">Vouchers of customer</h1>
                <a v-if="assignedVouchers.length !== 0"
                   class="font-medium text-blue-600 dark:text-blue-500 hover:underline absolute right-0 top-0 flex items-center text-sm hover:cursor-pointer"
                   @click="navigateToVoucherOfCustomer(customerInfo.id)">
                  See more
                  <Icon name="bxs:up-arrow" color="blue" width="12" height="12" :rotate="1"/>
                </a>
              </div>
              <div class="mt-2 rounded-md bg-white account-info">
                <view-data-table :headers="assignedVoucherTableHeader" :data="assignedVouchers"/>
              </div>
            </div>
            <div class="col-span-1 px-2">
              <div class="relative">
                <h1 class="text-center">Orders of customer</h1>
                <a v-if="ordersOfCustomer.length !== 0"
                   class="font-medium text-blue-600 dark:text-blue-500 hover:underline absolute right-0 top-0 flex items-center text-sm hover:cursor-pointer"
                   @click="navigateToOrderOfCustomer(customerInfo.id)">
                  See more
                  <Icon name="bxs:up-arrow" color="blue" width="12" height="12" :rotate="1"/>
                </a>
              </div>
              <div class="mt-2 rounded-md bg-white account-info">
                <view-data-table :headers="orderOfCustomerTableHeader" :data="ordersOfCustomer"/>
              </div>
            </div>
          </div>
          <div class="w-full pt-2 border-t border-gray-600 grid grid-cols-2">
            <div class="col-span-1 border-r border-gray-600 px-2 pb-8 mb-2">
              <div class="relative">
                <h1 class="text-center">Login histories</h1>
                <a v-if="loginHistories.length !== 0"
                   class="font-medium text-blue-600 dark:text-blue-500 hover:underline absolute right-0 top-0 flex items-center text-sm hover:cursor-pointer"
                   @click="navigateToCustomerLoginHistories(customerInfo.id)">
                  See more
                  <Icon name="bxs:up-arrow" color="blue" width="12" height="12" :rotate="1"/>
                </a>
              </div>
              <div class="mt-2 rounded-md bg-white account-info">
                <view-data-table :headers="loginHistoriesTableHeader" :data="loginHistories"/>
              </div>
            </div>
            <div class="col-span-1 px-2">
              <div class="relative">
                <h1 class="text-center">Update data histories</h1>
                <a v-if="changeHistories.length !== 0"
                   class="font-medium text-blue-600 dark:text-blue-500 hover:underline absolute right-0 top-0 flex items-center text-sm cursor-pointer"
                   @click="navigateToUpdatedDataHistories(customerInfo.id)">
                  See more
                  <Icon name="bxs:up-arrow" color="blue" width="12" height="12" :rotate="1"/>
                </a>
              </div>
              <div class="mt-2 rounded-md bg-white account-info">
                <view-data-table :headers="changeHistoriesTableHeader" :data="changeHistories"/>
              </div>
            </div>
          </div>
        </template>
        <template v-if="mode === 'update'">
          <div class="grid grid-cols-6">
            <div class="col-start-2 col-end-6 bg-white p-6 rounded-md edit-area grid grid-cols-2 gap-x-10">
              <h1 class="font-bold text-center text-lg col-span-2">Edit customer information</h1>
              <input type="hidden" :value="customerInfo.id">
              <div class="col-span-1 relative z-0 w-full mb-6 mt-4 group">
                <input type="text" name="firstName" id="firstName"
                       class="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                       placeholder=" " v-model="customerInfo.first_name"/>
                <label for="firstName"
                       class="peer-focus:font-medium absolute text-sm text-gray-500 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">
                  First name
                </label>
              </div>
              <div class="relative z-0 w-full mb-6 mt-4 group col-span-1">
                <input type="text" name="lastName" id="lastName"
                       class="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                       placeholder=" " v-model="customerInfo.last_name"/>
                <label for="lastName"
                       class="peer-focus:font-medium absolute text-sm text-gray-500 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">
                  Last name
                </label>
              </div>
              <div class="relative z-0 w-full mb-6 mt-4 group col-span-1">
                <input type="text" name="phoneNumber" id="phoneNumber"
                       class="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                       placeholder=" " v-model="customerInfo.phone"/>
                <label for="phoneNumber"
                       class="peer-focus:font-medium absolute text-sm text-gray-500 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">
                  Phone
                </label>
              </div>
              <div class="relative z-0 w-full mb-6 mt-4 group col-span-1">
                <input type="text" name="email" id="email"
                       class="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                       placeholder=" " v-model="customerInfo.email"/>
                <label for="email"
                       class="peer-focus:font-medium absolute text-sm text-gray-500 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">
                  Email
                </label>
              </div>
              <div class="relative z-0 w-full mb-6 mt-4 group col-span-1">
                <label class="block mb-2 text-sm font-medium text-gray-500">Gender</label>
                <div class="flex items-center">
                  <div class="flex items-center pr-6" v-for="gender in genderCheckboxValues">
                    <p v-text="gender"></p>
                    <input type="checkbox" name="gender" :value="gender"
                           :checked="customerInfo.gender.toLowerCase() === gender"
                           class="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500 focus:ring-2 mx-4"
                           @change="bindGender">
                  </div>
                </div>
              </div>
              <div class="relative z-0 w-full mb-6 mt-4 group col-span-1">
                <label for="role" class="block mb-2 text-sm font-medium text-gray-500">
                  Role
                </label>
                <select id="role"
                        class="block w-full p-2 text-sm text-gray-900 border border-gray-300 rounded-lg bg-gray-50 focus:ring-blue-500 focus:border-blue-500"
                        v-model="customerInfo.role">
                  <option disabled value="">Choose a role</option>
                  <option v-for="role in roles" :key="role" :value="role" v-text="role"></option>
                </select>
              </div>
              <div class="relative z-0 w-full mb-6 mt-4 group col-span-1">
                <input type="number" name="orderPoint" id="orderPoint"
                       class="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                       placeholder=" " v-model="customerInfo.order_point"/>
                <label for="orderPoint"
                       class="peer-focus:font-medium absolute text-sm text-gray-500 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">
                  Order point
                </label>
              </div>
              <div class="relative z-0 w-full mb-6 mt-4 group col-span-1 flex items-end">
                <div class="flex items-center absolute left-[2rem]">
                  <label for="enabled" class="pr-4">Enabled</label>
                  <input type="checkbox" name="enabled" id="enabled"
                         class="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500 dark:focus:ring-blue-600 dark:ring-offset-gray-800 focus:ring-2"
                         :checked="customerInfo.is_enabled" @change="bindEnabled"/>
                </div>
                <div class="flex items-center absolute right-[2rem]">
                  <label for="activated" class="pr-4">Activated</label>
                  <input type="checkbox" name="activated" id="activated"
                         class="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500 dark:focus:ring-blue-600 dark:ring-offset-gray-800 focus:ring-2"
                         :checked="customerInfo.is_activated" @change="bindActivated">
                </div>
              </div>
              <div class="relative z-0 w-full mb-6 mt-4 group col-span-2 grid grid-cols-3">
                <div class="col-span-1 flex items-center">
                  <label for="accountNonExpired" class="pr-4">Non expired</label>
                  <input type="checkbox" name="accountNonExpired" id="accountNonExpired"
                         class="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500 dark:focus:ring-blue-600 dark:ring-offset-gray-800 focus:ring-2"
                         :checked="customerInfo.is_account_non_expired" @change="bindAccountNonExpired">
                </div>
                <div class="col-span-1 flex items-center">
                  <label for="accountNonExpired" class="pr-4">Non locked</label>
                  <input type="checkbox" name="accountNonLocked" id="accountNonLocked"
                         class="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500 dark:focus:ring-blue-600 dark:ring-offset-gray-800 focus:ring-2"
                         :checked="customerInfo.is_account_non_locked" @change="bindAccountNonLocked">
                </div>
                <div class="col-span-1 flex items-center">
                  <label class="pr-4">Credentials non expired</label>
                  <input type="checkbox" name="credentialsNonExpired" id="credentialsNonExpired"
                         class="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500 dark:focus:ring-blue-600 dark:ring-offset-gray-800 focus:ring-2"
                         :checked="customerInfo.is_credentials_non_expired" @change="bindCredentialsNonLocked">
                </div>
              </div>
              <div class="relative z-0 w-max mt-4 group col-span-2 flex items-center hover:cursor-pointer"
                   @click="rotateIcon">
                <p>Address</p>
                <Icon class="ease-in duration-200 origin-center" :class="{ 'rotate-90': rotateAddressIcon }"
                      name="eva:arrow-up-outline" color="green" width="24" height="24" :rotate="1"/>
              </div>
              <div class="relative z-0 w-full mb-6 group col-span-2 ease-in-out duration-[700ms] overflow-hidden"
                   :class="{ 'h-full': rotateAddressIcon, 'h-0 border-t border-gray-300': !rotateAddressIcon }">
                <ul v-if="customerInfo.address.length !== 0"
                    class="w-full space-y-1 text-gray-700 list-disc list-inside pt-2">
                  <template :key="address" v-for="address in customerInfo.address">
                    <li v-if="address.length !== 0" v-text="address"></li>
                  </template>
                </ul>
                <ul v-if="selectedAddresses.length !== 0"
                    class="w-full space-y-1 text-gray-700 list-disc list-inside pt-2">
                  <li v-for="selected in selectedAddresses" class="w-full flex justify-between"
                      :key="JSON.stringify(selected)">
                    <span class="hover:cursor-default">
                      {{ processSelectedAddress(selected) }}
                    </span>
                    <Icon @click="removeSelectedAddress(selected)" class="hover:cursor-pointer" name="iwwa:delete"
                          color="red" width="24" height="24"/>
                  </li>
                </ul>
                <button type="button"
                        class="focus:outline-none text-white bg-green-700 hover:bg-green-800 focus:ring-4 focus:ring-green-300 font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 dark:bg-green-600 dark:hover:bg-green-700 dark:focus:ring-green-800"
                        :class="{ 'mt-6': (customerInfo.address.length !== 0 && customerInfo.address[0].length !== 0) || (selectedAddresses.length !== 0) }"
                        @click="toggleModal">
                  Add new address
                </button>
              </div>
              <div class="relative z-0 w-full group col-span-2 flex justify-end">
                <button type="button"
                        class="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 focus:outline-none"
                        @click="updateCustomer">
                  Submit
                </button>
              </div>
              <template v-if="!hideModal">
                <div tabindex="-1"
                     class="fixed top-0 left-0 right-0 z-50 w-full p-4 overflow-x-hidden overflow-y-auto md:inset-0 h-[100vh] max-h-full flex justify-center items-center bg-gray-400 bg-opacity-50">
                  <div class="relative w-full max-w-2xl max-h-full">
                    <div class="relative bg-white rounded-lg shadow">
                      <div class="flex items-start justify-between p-4 border-b rounded-t">
                        <h3 class="text-xl font-semibold text-gray-900">
                          Add new address for customer
                        </h3>
                        <button type="button"
                                class="text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-8 h-8 ml-auto inline-flex justify-center items-center"
                                data-modal-hide="defaultModal" @click="closeModal">
                          <svg class="w-3 h-3" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 14">
                            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                  d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6"/>
                          </svg>
                          <span class="sr-only">Close modal</span>
                        </button>
                      </div>
                      <div class="p-6 space-y-6 grid grid-cols-2">
                        <div class="relative z-0 w-full mb-6 mt-4 group col-span-2 grid grid-cols-3">
                          <div class="col-span-1">
                            <label for="province">Province</label>
                            <select name="province" id="province"
                                    class="block w-[90%] p-2 text-sm text-gray-900 border border-gray-300 rounded-lg bg-gray-50 focus:ring-blue-500 focus:border-blue-500"
                                    @change="event => fetchDistrict(JSON.parse((event.target as HTMLSelectElement).value))"
                                    ref="province">
                              <option value="" selected disabled>-- Choose province --</option>
                              <option v-for="province in provinceSource" :key="province.id"
                                      :value="JSON.stringify(province)" v-text="province.name"></option>
                            </select>
                          </div>
                          <div class="col-span-1">
                            <label for="district">District</label>
                            <select name="district" id="district"
                                    class="block w-[90%] p-2 text-sm text-gray-900 border border-gray-300 rounded-lg bg-gray-50 focus:ring-blue-500 focus:border-blue-500"
                                    @change="event => fetchWard(JSON.parse((event.target as HTMLSelectElement).value))"
                                    ref="district">
                              <option value="" selected disabled>-- Choose district --</option>
                              <option v-for="district in districtSource" :key="district.id"
                                      :value="JSON.stringify(district)" v-text="district.name"></option>
                            </select>
                          </div>
                          <div class="col-span-1">
                            <label for="ward">Ward</label>
                            <select name="ward" id="ward"
                                    class="block w-[90%] p-2 text-sm text-gray-900 border border-gray-300 rounded-lg bg-gray-50 focus:ring-blue-500 focus:border-blue-500"
                                    ref="ward">
                              <option selected value="" disabled>-- Choose ward --</option>
                              <option v-for="ward in wardSource" :key="ward.id" :value="JSON.stringify(ward)"
                                      v-text="ward.name"></option>
                            </select>
                          </div>
                        </div>
                        <div class="relative z-0 w-full mb-6 mt-4 group col-span-2">
                          <input placeholder=" " ref="houseNumber" id="houseNumber" name="houseNumber" type="text"
                                 class="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer">
                          <label for="houseNumber"
                                 class="peer-focus:font-medium absolute text-sm text-gray-500 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">
                            House number
                          </label>
                        </div>
                      </div>
                      <div class="flex items-center p-6 space-x-2 border-t border-gray-200 rounded-b">
                        <button data-modal-hide="defaultModal" type="button"
                                class="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center"
                                @click="addAddress">
                          Add
                        </button>
                        <button data-modal-hide="defaultModal" type="button"
                                class="text-gray-500 bg-white hover:bg-gray-100 focus:ring-4 focus:outline-none focus:ring-blue-300 rounded-lg border border-gray-200 text-sm font-medium px-5 py-2.5 hover:text-gray-900 focus:z-10"
                                @click="closeModal">
                          Cancel
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </template>
            </div>
          </div>
        </template>
        <div class="fixed top-[4.5rem] right-0 w-[25%]">
          <alert-list-error :error-messages="alertErrorContents" :hidden="alertErrorContents.length === 0"
                            :title="'Update customer failure'" @hidden="hideErrorAlert"/>
        </div>
        <div class="fixed top-[4.5rem] right-0 w-[25%]">
          <alert-list-info :title="'Update customer successfully'" :error-messages="alertInfoContents"
                           :hide="alertInfoContents.length === 0" @hidden="hideInfoAlert"/>
        </div>
      </admin-content-area>
    </loading-block-u-i>
  </div>
</template>

<style lang="css">
.customer-basic-info {
  box-shadow: rgba(0, 0, 0, 0.16) 0 1px 4px;
}

.account-info {
  box-shadow: rgba(0, 0, 0, 0.12) 0 1px 3px, rgba(0, 0, 0, 0.24) 0 1px 2px;
}

.edit-area {
  box-shadow: rgba(50, 50, 93, 0.25) 0 2px 5px -1px, rgba(0, 0, 0, 0.3) 0 1px 3px -1px;
}
</style>