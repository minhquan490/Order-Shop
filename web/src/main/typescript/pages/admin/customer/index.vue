<script lang="ts">
import {navigationSources, tableHeaders} from "~/logic/pages/admin/customer/index.logic";
import {NavBarsSource, TableHeader, TableNewData} from "~/types";

export default {
  setup() {
    const navigationSource: NavBarsSource[] = navigationSources();
    const headers: TableHeader[] = tableHeaders();
    const isLoading: boolean = false;
    const tableNewData: TableNewData = {
      icon: 'ic:round-plus',
      buttonContent: 'New customer'
    }
    return {
      navigationSource,
      isLoading,
      headers,
      tableNewData
    }
  }
}
</script>

<template>
  <div class="relative">
    <loading-block-u-i :loading="isLoading">
      <nav-bars-admin :sources="navigationSource"/>
      <page-header title="Customer list" :bread-crumbs-enable="true"/>
      <admin-content-area>
        <data-table :headers="headers" :button-new="tableNewData"/>
      </admin-content-area>
    </loading-block-u-i>
  </div>
</template>
