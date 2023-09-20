<script lang="ts" setup>
import { CategoryResponse, createCategory, navigationSources, validateCategory } from '~/logic/pages/admin/category/create.logic';
import { ErrorResponse } from '~/types';

const isLoading = ref(false);
const navigation = ref(navigationSources());
const adminAreaMinHeight = ref(30);
const categoryName = ref('');
const validateErrorMessage = ref('');
const callServerError = ref<Array<string>>([]);

async function submit(event: Event): Promise<void> {
  event.preventDefault();
  const result: string = validateCategory(categoryName.value);
  if (result.length !== 0) {
    validateErrorMessage.value = result;
    return Promise.resolve();
  }
  const resp: CategoryResponse | ErrorResponse | undefined = await createCategory(categoryName.value);
  if (resp) {
    if ("messages" in resp) {
      callServerError.value = resp.messages;
    } else {
      const navigate = useNavigation().value;
      navigate('/admin/category');
    }
  }
}

function onInputChange(event: Event): void {
  event.preventDefault();
  const htmlInputEle: HTMLInputElement = event.target as HTMLInputElement;
  const value: string = htmlInputEle.value;
  const result: string = validateCategory(value);
  validateErrorMessage.value = result;
}

function hideAlert(): void {
  callServerError.value = [];
}
</script>

<template>
  <loading-block-u-i :loading="isLoading">
    <div class="relative">
      <nav-bars-admin :sources="navigation"/>
      <page-header title="Create category" :bread-crumbs-enable="true"/>
      <admin-content-area :min-height="adminAreaMinHeight">
        <div class="flex items-center justify-center pb-8">
          <p class="hover:cursor-default text-xl font-bold">Create new category</p>
        </div>
        <div class="grid grid-cols-3">
          <div class="col-start-2 col-end-3 flex items-center">
            <label for="default-input" class="block text-sm font-medium text-gray-900 pr-4">Category name</label>
            <input placeholder="Insert category name" 
                   v-model="categoryName" 
                   type="text" 
                   id="default-input" 
                   class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-[60%] p-2.5"
                   @change="onInputChange">
          </div>
          <div class="col-start-2 col-end-3 flex items-center justify-center pt-8">
            <button @click="submit" type="button" class="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2">Create category</button>
          </div>
        </div>
      </admin-content-area>
      <div class="absolute top-[10rem] right-0 w-[25%]">
        <ClientOnly>
          <AlertListError @hidden="hideAlert"
                          :hidden="callServerError.length === 0"
                          :error-messages="callServerError"
                          :title="'Can not create category: '"/>
        </ClientOnly>
      </div>
    </div>
  </loading-block-u-i>
</template>
