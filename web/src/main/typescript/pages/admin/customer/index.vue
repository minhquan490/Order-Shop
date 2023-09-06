<script lang="ts">
import {
  getCustomersInfo,
  getDefaultCustomerInfo,
  navigateToCustomerInfoPage,
  navigateToNewCustomerPage,
  navigationSources,
  tableHeaders
} from "~/logic/pages/admin/customer/index.logic";
import { NavBarsSource, TableHeader, TableNewData } from "~/types";

export default {
  methods: { navigateToCustomerInfoPage },
  async setup() {
    const navigationSource: NavBarsSource[] = navigationSources();
    const headers: TableHeader[] = tableHeaders();
    const isLoading: boolean = false;
    const tableNewData: TableNewData = {
      icon: 'ic:round-plus',
      buttonContent: 'New customer'
    }
    let customerList: Array<CustomerInfo> | undefined = await getCustomersInfo();
    if (!customerList) {
      customerList = [];
    }
    return {
      navigationSource,
      isLoading,
      headers,
      tableNewData,
      navigateToNewCustomerPage,
      customerList
    }
  }
}

type CustomerInfo = ReturnType<typeof getDefaultCustomerInfo>;
</script>

<template>
  <div class="relative">
    <loading-block-u-i :loading="isLoading">
      <nav-bars-admin :sources="navigationSource" />
      <page-header title="Customer list" :bread-crumbs-enable="true" />
      <admin-content-area>
        <data-table :headers="headers" :button-new="tableNewData" @new-data="navigateToNewCustomerPage" :table-data="customerList" @info-data="data => navigateToCustomerInfoPage(data['id'])" />
      </admin-content-area>
    </loading-block-u-i>
  </div>
</template>
