<script lang="ts">
import {NavBarsSource, TableData, TableFilter, TableHeader, TableNewData} from "~/types";
import {
  deleteProduct,
  getProductCategories,
  getProductList,
  hideAlert,
  navigateToCreatePage,
  navigateToProductInfoPage,
  navigationSources,
  searchProduct,
  tableHeaders
} from "~/logic/pages/admin/product/index.logic";

export default {
  data() {
    const navigation: NavBarsSource[] = navigationSources();

    const headers: TableHeader[] = tableHeaders();

    const tableNewData: TableNewData = {
      icon: 'ic:round-plus',
      buttonContent: 'New product'
    }

    const tableData: TableData[] = [];

    const isLoading = false;

    const hideAlert = true;

    const callServerError: string[] = []

    const tableFilters: TableFilter = {
      title: 'Choose category',
      categories: []
    };
    return {
      navigation,
      headers,
      tableNewData,
      tableData,
      isLoading,
      hideAlert,
      callServerError,
      tableFilters
    }
  },
  methods: {
    searchData(keyword: string) {
      searchProduct(keyword, this);
    },
    deleteProduct(data: TableData) {
      deleteProduct(data, this);
    },
    productInfo(data: TableData) {
      navigateToProductInfoPage(data);
    },
    navigateToCreatePage() {
      navigateToCreatePage();
    },
    alertHiddenListener() {
      hideAlert(this);
    }
  },
  beforeMount() {
    this.isLoading = true;
    getProductCategories(this);
    getProductList(this);
  }
}
</script>

<template>
  <loading-block-u-i :loading="isLoading">
    <div class="relative">
      <nav-bars-admin :sources="navigation"/>
      <page-header title="Product list" :bread-crumbs-enable="true"/>
      <admin-content-area>
        <data-table :headers="headers"
                    :table-data="tableData"
                    :button-new="tableNewData"
                    :filters="tableFilters"
                    @new-data="navigateToCreatePage"
                    @search-data="searchData"
                    @delete-data="deleteProduct"
                    @info-data="productInfo"/>
      </admin-content-area>
      <div v-if="!hideAlert" class="absolute top-[10rem] right-0 w-[30%]">
        <client-only>
          <alert-list-error :hidden="hideAlert"
                            :error-messages="callServerError"
                            @hidden="alertHiddenListener"
                            :title="'Something wrong: '"/>
        </client-only>
      </div>
    </div>
  </loading-block-u-i>
</template>
