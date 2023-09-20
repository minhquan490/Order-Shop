<script lang="ts" setup>
import { CategoryResponse, deleteCategory, getCategories, navigationSources, tableHeaders } from '~/logic/pages/admin/category/index.logic';
import { NavBarsSource, TableHeader } from '~/types';

const isLoading = ref<boolean>(true);
const navigation = ref<Array<NavBarsSource>>(navigationSources());
const headers = ref<Array<TableHeader>>(tableHeaders());
const categories = ref<Array<CategoryResponse>>([]);
const fetchErrors = ref<Array<string>>([]);

function goToCreateCategoryPage(): void {
  const navigate = useNavigation().value;
  navigate('/admin/category/create');
}

function clearFetchErrors(): void {
  fetchErrors.value = [];
}

function deleteCate(category: CategoryResponse): void {
  deleteCategory(category).then(resp => {
    if (typeof resp === "boolean") {
      categories.value = categories.value.filter(cate => cate.category_id !== category.category_id);
    } else {
      fetchErrors.value = resp.messages;
    }
  });
}

onMounted(() => {
  getCategories().then(results => {
    if (results) {
      if ("messages" in results) {
        console.log(results.messages);
      } else {
        categories.value = results;
      }
      isLoading.value = false;
    }
  });
});
</script>

<template>
  <loading-block-u-i :loading="isLoading">
    <div class="relative">
      <nav-bars-admin :sources="navigation"/>
      <page-header title="Category list" :bread-crumbs-enable="true"/>
      <admin-content-area>
        <div class="grid grid-cols-6">
          <div class="col-start-2 col-end-6">
            <data-table :headers="headers" 
                        :table-data="categories"
                        :enable-advance-search="false"
                        @new-data="goToCreateCategoryPage"
                        @delete-data="deleteCate"/>
          </div>
        </div>
      </admin-content-area>
      <div class="absolute top-20 right-0 w-1/4">
        <alert-list-error  title="Fetch category failure, because" 
                           :error-messages="fetchErrors" 
                           :hidden="fetchErrors.length === 0"
                           @hidden="clearFetchErrors"/>
      </div>
    </div>
  </loading-block-u-i>
</template>
