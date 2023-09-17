<script lang="ts" setup>
import {
  AssignmentVoucher,
  CustomerAssignmentVouchers,
  fetchAssignmentVoucher,
  getTableHeaders,
  navigationSources
} from "~/logic/pages/admin/customer/assignment-vouchers.logic";
import {checkCustomerIdQueryParam} from "~/utils/RequestUtils";
import {NextPage} from "~/types";

const isLoading = ref(true);
const tableHeaders = ref(getTableHeaders());
const itemsPerPage = ref(5);
const navigationSource = ref(navigationSources());
const customerIdQuery = ref(checkCustomerIdQueryParam() as string);
const assignmentVouchers = ref<CustomerAssignmentVouchers | null>(null);

async function getNextPage(nextPage: NextPage): Promise<void> {
  const value: CustomerAssignmentVouchers | undefined = await fetchAssignmentVoucher(customerIdQuery.value, nextPage.page, nextPage.itemsPerPage);
  if (value) {
    const oldData: AssignmentVoucher[] = assignmentVouchers.value?.vouchers as AssignmentVoucher[];
    value.vouchers = oldData.concat(value.vouchers);
    assignmentVouchers.value = value;
  }
}

onMounted(() => {
  fetchAssignmentVoucher(customerIdQuery.value).then(data => {
    if (data) {
      assignmentVouchers.value = data;
      isLoading.value = false;
    }
  });
});
</script>

<template>
  <div class="relative">
    <loading-block-u-i :loading="isLoading">
      <nav-bars-admin :sources="navigationSource"/>
      <page-header title="Customer updated data histories" :bread-crumbs-enable="true"/>
      <admin-content-area>
        <div class="flex items-center justify-between">
          <div>
            <p class="hover:cursor-default" v-text="customerIdQuery"></p>
          </div>
          <div>
            <a :href="`/admin/customer/info?customerId=${customerIdQuery}`"
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
            <data-table :table-data="assignmentVouchers?.vouchers"
                        :headers="tableHeaders"
                        :hide-delete="true"
                        :enable-id-link="false"
                        :item-per-page="itemsPerPage"
                        :server-page-size="assignmentVouchers?.page_size"
                        :server-page-num="assignmentVouchers?.page"
                        :server-total-items="assignmentVouchers?.total_vouchers"
                        :enable-advance-search="false"
                        :enable-new-button="false"
                        @next-page="getNextPage"/>
          </div>
        </div>
      </admin-content-area>
    </loading-block-u-i>
  </div>
</template>
