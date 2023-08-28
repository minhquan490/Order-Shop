<script lang="ts">
import {NavBarsSource, TableHeader} from "~/types";
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
  navigationSources
} from "~/logic/pages/admin/customer/info.logic";

export default {
  data() {
    const accessHistories: Array<AccessHistory> = [];
    const assignedVouchers: Array<AssignedVoucher> = [];
    const ordersOfCustomer: Array<OrderOfCustomer> = [];
    const loginHistories: Array<LoginHistory> = [];
    const changeHistories: Array<InfoChangeHistory> = [];
    const navigationSource: NavBarsSource[] = navigationSources();
    const defaultCarousel = defaultCarouselItem();
    const accessHistoriesTableHeader: Array<TableHeader> = getAccessHistoriesTableHeader();
    const assignedVoucherTableHeader: Array<TableHeader> = getAssignedVoucherTableHeader();
    const orderOfCustomerTableHeader: Array<TableHeader> = getOrderOfCustomerTableHeader();
    const loginHistoriesTableHeader: Array<TableHeader> = getLoginHistoriesTableHeader();
    const changeHistoriesTableHeader: Array<TableHeader> = getChangeHistoryTableHeader();
    const customerInfo: CustomerInfo = {
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
      username: ''
    };
    const isLoading: boolean = true;
    const mode: string = "view";
    const customerIdQuery = checkCustomerIdQueryParam();
    return {
      accessHistories,
      assignedVouchers,
      ordersOfCustomer,
      navigationSource,
      defaultCarousel,
      accessHistoriesTableHeader,
      assignedVoucherTableHeader,
      orderOfCustomerTableHeader,
      loginHistoriesTableHeader,
      loginHistories,
      changeHistoriesTableHeader,
      changeHistories,
      customerInfo,
      isLoading,
      mode,
      customerIdQuery
    }
  },
  async mounted() {
    const customerInfo = await getCustomerInfo(this.customerIdQuery);
    assignData(this, customerInfo);
    this.defaultCarousel = createCarouselItems(customerInfo.picture ?? '');
    this.isLoading = false;
  },
  methods: {
    changeMode(mode: string) {
      if (mode === 'view') {
        this.mode = 'update';
      }
      if (mode === 'update') {
        this.mode = 'view';
      }
    }
  }
}

type AccessHistory = ReturnType<typeof getDefaultAccessHistory>;
type AssignedVoucher = ReturnType<typeof getDefaultAssignedVoucher>;
type OrderOfCustomer = ReturnType<typeof getDefaultOrderOfCustomer>;
type LoginHistory = ReturnType<typeof getDefaultLoginHistory>;
type InfoChangeHistory = ReturnType<typeof getDefaultChangeHistory>;
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
  is_activated: boolean,
  is_account_non_expired: boolean,
  is_account_non_locked: boolean,
  is_credentials_non_expired: boolean,
  is_enabled: boolean,
  picture: string
}
</script>

<template>
  <div class="relative">
    <loading-block-u-i :loading="isLoading">
      <nav-bars-admin :sources="navigationSource"/>
      <page-header title="Customer information" :bread-crumbs-enable="true"/>
      <admin-content-area>
        <div class="pb-4 flex justify-between">
          <h1>Basic customer information</h1>
          <div>
            <button v-if="mode === 'view'"
                    type="button"
                    class="focus:outline-none text-white bg-red-500 hover:bg-red-800 focus:ring-4 focus:ring-red-300 font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2">
              Delete
            </button>
            <button v-if="mode === 'view'"
                    type="button"
                    class="focus:outline-none text-white bg-green-600 hover:bg-green-800 focus:ring-4 focus:ring-green-300 font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2"
                    @click="changeMode(this.mode)">
              Edit customer
            </button>
            <button v-if="mode === 'update'"
                    type="button"
                    class="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 dark:bg-blue-600 dark:hover:bg-blue-700 focus:outline-none">
              Submit
            </button>
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
                  <p class="pr-4 hover:cursor-default">ID: </p>
                  <span class="hover:cursor-default" v-text="customerInfo.id"></span>
                </div>
                <div class="col-span-1 flex border-b">
                  <p class="pr-4 hover:cursor-default">Username: </p>
                  <span class="hover:cursor-default" v-text="customerInfo.username"></span>
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
                <a href="#" v-if="accessHistories.length !== 0"
                   class="font-medium text-blue-600 dark:text-blue-500 hover:underline absolute right-0 top-0 flex items-center text-sm">
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
                <a href="#" v-if="assignedVouchers.length !== 0"
                   class="font-medium text-blue-600 dark:text-blue-500 hover:underline absolute right-0 top-0 flex items-center text-sm">
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
                <a href="#" v-if="ordersOfCustomer.length !== 0"
                   class="font-medium text-blue-600 dark:text-blue-500 hover:underline absolute right-0 top-0 flex items-center text-sm">
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
                <a href="#" v-if="loginHistories.length !== 0"
                   class="font-medium text-blue-600 dark:text-blue-500 hover:underline absolute right-0 top-0 flex items-center text-sm">
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
                <a href="#" v-if="changeHistories.length !== 0"
                   class="font-medium text-blue-600 dark:text-blue-500 hover:underline absolute right-0 top-0 flex items-center text-sm">
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
            <div class="col-start-2 col-end-6 bg-white p-6 rounded-md edit-area">
              <h1 class="font-bold text-center text-lg">Edit customer information</h1>
            </div>
          </div>
        </template>
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