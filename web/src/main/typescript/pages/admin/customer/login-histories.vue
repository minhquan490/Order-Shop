<script lang="ts" setup>
import {NextPage} from '~/types';
import {
  checkCustomerIdQueryParam,
  CustomerLoginHistory,
  getCustomerLoginHistoriesData,
  getViewDataTableHeaders,
  LoginHistory,
  navigationSources
} from '~/logic/pages/admin/customer/login-histories.logic';

const customerIdQueryParam = ref(checkCustomerIdQueryParam() as string);
const navigationSource = ref(navigationSources());
const tableHeaders = ref(getViewDataTableHeaders());
const isLoading = ref(true);
const itemsPerPage = ref(5);
const customerLoginHistory = ref<CustomerLoginHistory | null>(null);

async function getNextPageData(nextPage: NextPage) {
  const value = await getCustomerLoginHistoriesData(customerIdQueryParam.value, nextPage.page, nextPage.itemsPerPage);
  if (value) {
    const oldData = customerLoginHistory.value?.login_histories as LoginHistory[];
    value.login_histories = oldData?.concat(value.login_histories);
    customerLoginHistory.value = value;
  }
}

onMounted(async () => {
  isLoading.value = false;
  customerLoginHistory.value = await getCustomerLoginHistoriesData(customerIdQueryParam.value) as CustomerLoginHistory
});
</script>

<template>
  <div class="relative">
    <loading-block-u-i :loading="isLoading">
      <nav-bars-admin :sources="navigationSource"/>
      <page-header title="Customer login histories" :bread-crumbs-enable="true"/>
      <admin-content-area>
        <div class="flex items-center justify-between">
          <div>
            <p class="hover:cursor-default" v-text="customerIdQueryParam"></p>
          </div>
          <div>
            <a :href="`/admin/customer/info?customerId=${customerIdQueryParam}`"
               class="inline-flex items-center font-medium text-blue-600 dark:text-blue-500 hover:underline">
              Return
              <svg class="w-4 h-4 ml-2" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none"
                   viewBox="0 0 14 10">
                <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M1 5h12m0 0L9 1m4 4L9 9"/>
              </svg>
            </a>
          </div>
        </div>
        <div class="grid grid-cols-6 pt-6">
          <div class="col-start-2 col-end-6">
            <data-table :table-data="customerLoginHistory?.login_histories"
                        :headers="tableHeaders"
                        :hide-delete="true"
                        :enable-id-link="false"
                        :item-per-page="itemsPerPage"
                        :server-page-size="customerLoginHistory?.page_size"
                        :server-page-num="customerLoginHistory?.page"
                        :server-total-items="customerLoginHistory?.total_histories"
                        :enable-advance-search="false"
                        :enable-new-button="false"
                        @next-page="getNextPageData"/>
          </div>
        </div>
      </admin-content-area>
    </loading-block-u-i>
  </div>
</template>
