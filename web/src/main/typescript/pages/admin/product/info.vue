<script lang="ts">
import {
carouselItems,
checkPageQueryParam,
createDefaultProduct,
deletePicture,
navigationSources,
onMounted,
submitUpdate,
uploadFile
} from "~/logic/pages/admin/product/info.logic";
import { AdminProduct, CarouselItem, Category, NavBarsSource } from "~/types";

export default {
  setup() {
    const query = useRoute().query;
    return {
      query
    }
  },
  beforeCreate() {
    checkPageQueryParam(this);
  },
  data() {
    const isLoading = false;
    const navigation: NavBarsSource[] = navigationSources();
    const slideItems: CarouselItem[] = carouselItems();
    const product: AdminProduct = createDefaultProduct();
    const updateMode = false;
    const initSelectedCategory: Category[] = [];
    const categoriesSource: Category[] = [];
    const hideAlert = true;
    const updateErrors: Array<string> = [];
    const pictureSelected = '';
    const hideConfirmAlert = true;
    return {
      isLoading,
      navigation,
      slideItems,
      product,
      updateMode,
      initSelectedCategory,
      categoriesSource,
      hideAlert,
      updateErrors,
      pictureSelected,
      hideConfirmAlert
    }
  },
  mounted() {
    this.isLoading = true;
    onMounted(this);
  },
  methods: {
    toggleUpdateForm() {
      this.updateMode = !this.updateMode;
    },
    submitUpdate() {
      submitUpdate(this);
    },
    toggleErrorAlert() {
      this.hideAlert = !this.hideAlert;
    },
    selectOptionsListener(categories: Category[]) {
      this.initSelectedCategory = categories;
    },
    removeOptionListener(categories: Category[]) {
      this.initSelectedCategory = categories;
    },
    itemSelected(item: CarouselItem) {
      this.pictureSelected = item.pictureUrl;
    },
    toggleModal() {
      this.pictureSelected = '';
    },
    toggleConfirmAlert() {
      this.hideConfirmAlert = !this.hideConfirmAlert;
    },
    deletePicture() {
      deletePicture(this);
    },
    async uploadFile(event: Event): Promise<void> {
      return uploadFile(event);
    }
  }
}
</script>

<template>
  <loading-block-u-i :loading="isLoading">
    <div class="relative">
      <nav-bars-admin :sources="navigation"/>
      <page-header title="Product information" :bread-crumbs-enable="true"/>
      <admin-content-area>
        <div class="grid grid-cols-3 gap-10">
          <div class="col-span-1 pt-12">
            <carousel @item-selected="itemSelected" :items="slideItems"/>
            <div class="pt-4">
              <label class="block mb-2 text-sm font-medium text-gray-900" for="file_input">Add product image</label>
              <input
                  class="block w-full text-sm text-gray-900 border border-gray-300 rounded-lg cursor-pointer bg-gray-50 focus:outline-none"
                  aria-describedby="file_input_help"
                  id="file_input"
                  type="file"
                  multiple
                  @change="uploadFile">
              <p class="mt-1 text-sm text-gray-500" id="file_input_help">SVG, PNG, JPG, GIF or MP4 (MAX. 10MB).</p>
            </div>
          </div>
          <div class="col-span-2">
            <div class="flex items-center justify-center">
              <p class="font-bold text-2xl" v-text="product.name"></p>
            </div>
            <div class="pt-4 bg-white rounded p-4 mt-6 grid grid-cols-2 gap-x-3 gap-y-10 form">
              <div class="col-span-1">
                <label for="product-id" class="mb-2 text-md font-medium text-gray-900">
                  Product id:
                </label>
                <span id="product-id" class="hover:cursor-default text-gray-900 pl-2" v-text="product.id"></span>
              </div>
              <div class="col-span-1">
                <label for="product-name" class="mb-2 text-md font-medium text-gray-900">
                  Product name:
                </label>
                <template v-if="updateMode">
                  <input type="text"
                         id="product-name"
                         v-model="product.name"
                         class="p-2 text-gray-900 border border-gray-300 rounded-lg bg-gray-50 sm:text-xs focus:ring-blue-500 focus:border-blue-500 w-[65%]">
                </template>
                <template v-else>
                  <span id="product-name" class="hover:cursor-default text-gray-900 pl-2" v-text="product.name"></span>
                </template>
              </div>
              <div class="col-span-1">
                <label for="product-price" class="mb-2 text-md font-medium text-gray-900">
                  Product price:
                </label>
                <template v-if="updateMode">
                  <input type="text"
                         id="product-price"
                         v-model="product.price"
                         class="p-2 text-gray-900 border border-gray-300 rounded-lg bg-gray-50 sm:text-xs focus:ring-blue-500 focus:border-blue-500 w-[65%]">
                </template>
                <template v-else>
                  <span id="product-price" class="hover:cursor-default text-gray-900 pl-2"
                        v-text="product.price"></span>
                </template>
              </div>
              <div class="col-span-1">
                <label for="product-size" class="mb-2 text-md font-medium text-gray-900">
                  Product price:
                </label>
                <template v-if="updateMode">
                  <input type="text"
                         id="product-size"
                         v-model="product.size"
                         class="p-2 text-gray-900 border border-gray-300 rounded-lg bg-gray-50 sm:text-xs focus:ring-blue-500 focus:border-blue-500 w-[67%]">
                </template>
                <template v-else>
                  <span id="product-size" class="hover:cursor-default text-gray-900 pl-2" v-text="product.size"></span>
                </template>
              </div>
              <div class="col-span-1">
                <label for="product-color" class="mb-2 text-md font-medium text-gray-900">
                  Product color:
                </label>
                <template v-if="updateMode">
                  <input type="text"
                         id="product-color"
                         v-model="product.color"
                         class="p-2 text-gray-900 border border-gray-300 rounded-lg bg-gray-50 sm:text-xs focus:ring-blue-500 focus:border-blue-500 w-[65%]">
                </template>
                <template v-else>
                  <span id="product-color" class="hover:cursor-default text-gray-900 pl-2"
                        v-text="product.color"></span>
                </template>
              </div>
              <div class="col-span-1 relative flex">
                <label for="product-categories" class="mb-2 text-md font-medium text-gray-900">
                  Product categories:
                </label>
                <template v-if="updateMode">
                  <multi-select class="pl-2 w-[55%] absolute top-[calc(-1rem-8px)]"
                                :init-selected-option="initSelectedCategory"
                                :options="categoriesSource"
                                :rendered-key="'name'"
                                :label="'Category'"
                                @select-option="selectOptionsListener"
                                @remove-option="removeOptionListener"/>
                </template>
                <template v-else>
                  <span class="hover:cursor-default text-gray-900 pl-2" v-text="product.categories.join(' ,')"></span>
                </template>
              </div>
              <div class="col-span-2">
                <label for="product-taobao-url" class="mb-2 text-md font-medium text-gray-900">
                  Product taobao url:
                </label>
                <template v-if="updateMode">
                  <input type="text"
                         id="product-taobao-url"
                         v-model="product.taobao_url"
                         class="p-2 text-gray-900 border border-gray-300 rounded-lg bg-gray-50 sm:text-xs focus:ring-blue-500 focus:border-blue-500 w-[78%]">
                </template>
                <template v-else>
                  <a :href="product.taobao_url"
                     id="product-taobao-url"
                     class="underline text-blue-600 pl-2"
                     v-text="product.taobao_url"></a>
                </template>
              </div>
              <div class="col-span-1">
                <label for="product-order-point" class="mb-2 text-md font-medium text-gray-900">
                  Product order point:
                </label>
                <template v-if="updateMode">
                  <input type="text"
                         id="product-order-point"
                         v-model="product.orderPoint"
                         class="p-2 text-gray-900 border border-gray-300 rounded-lg bg-gray-50 sm:text-xs focus:ring-blue-500 focus:border-blue-500 w-[55%]">
                </template>
                <template v-else>
                  <span id="product-order-point" class="hover:cursor-default text-gray-900 pl-2"
                        v-text="product.orderPoint"></span>
                </template>
              </div>
              <div class="col-span-1 flex items-center">
                <label for="product-order-point" class="mb-2 text-md font-medium text-gray-900">
                  Product enabled:
                </label>
                <template v-if="updateMode">
                  <input v-model="product.enable"
                         id="product-order-point"
                         type="checkbox"
                         class="ml-6 mb-2 p-2 w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500">
                </template>
                <template v-else>
                  <Icon :name="product.enable ? 'mdi:tick-circle-outline' : 'carbon:close-outline'"
                        :color="product.enable ? 'rgb(22 163 74)' : 'rgb(220 38 38)'"
                        width="24"
                        height="24"
                        class="mb-2 ml-6"/>
                </template>
              </div>
              <div class="col-span-2">
                <label for="product-description" class="mb-2 text-md font-medium text-gray-900 pr-2">
                  Product description:
                </label>
                <template v-if="updateMode">
                  <textarea id="product-description"
                            cols="10"
                            rows="2"
                            v-model="product.description"
                            class="p-2 text-gray-900 border border-gray-300 rounded-lg bg-gray-50 sm:text-xs focus:ring-blue-500 focus:border-blue-500 w-full"></textarea>
                </template>
                <template v-else>
                  <span class="hover:cursor-default text-gray-900 break-words" v-text="product.description"></span>
                </template>
              </div>
              <div class="col-span-1"></div>
              <div class="col-span-1 flex justify-end">
                <template v-if="!updateMode">
                  <button type="button"
                          class="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 focus:outline-none w-[10rem]"
                          @click="toggleUpdateForm">
                    Update
                  </button>
                </template>
                <template v-else>
                  <button type="button"
                          class="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 focus:outline-none w-[10rem]"
                          @click="submitUpdate">
                    Submit
                  </button>
                </template>
              </div>
            </div>
          </div>
        </div>
      </admin-content-area>
      <div class="absolute top-[10rem] right-0 w-[30%]">
        <client-only>
          <alert-list-error :title="'Can not update: '"
                            :hidden="hideAlert"
                            @hidden="toggleErrorAlert"
                            :error-messages="updateErrors"/>
        </client-only>
      </div>
      <div v-if="pictureSelected.length !== 0" id="readProductModal" tabindex="-1" aria-hidden="true"
           class="flex items-center overflow-y-auto overflow-x-hidden fixed top-0 right-0 left-0 z-50 justify-center w-full md:inset-0 h-modal md:h-full">
        <div class="relative p-4 w-full max-w-xl h-full md:h-auto">
          <div class="relative p-4 bg-white rounded-lg shadow sm:p-5">
            <div class="flex justify-between mb-4 rounded-t sm:mb-5">
              <div class="text-lg text-gray-900 md:text-xl">
                <span>Review picture</span>
              </div>
              <div>
                <button type="button"
                        class="text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm p-1.5 inline-flex"
                        @click="toggleModal">
                  <svg aria-hidden="true" class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"
                       xmlns="http://www.w3.org/2000/svg">
                    <path fill-rule="evenodd"
                          d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
                          clip-rule="evenodd"></path>
                  </svg>
                  <span class="sr-only">Close modal</span>
                </button>
              </div>
            </div>
            <div class="pb-4">
              <img :src="pictureSelected" alt="Review picture">
            </div>
            <div class="flex justify-between items-center">
              <div class="flex items-center space-x-3 sm:space-x-4"></div>
              <button type="button"
                      class="inline-flex items-center text-white bg-red-600 hover:bg-red-700 focus:ring-4 focus:outline-none focus:ring-red-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center"
                      @click="toggleConfirmAlert">
                <svg aria-hidden="true" class="w-5 h-5 mr-1.5 -ml-1" fill="currentColor" viewBox="0 0 20 20"
                     xmlns="http://www.w3.org/2000/svg">
                  <path fill-rule="evenodd"
                        d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z"
                        clip-rule="evenodd"></path>
                </svg>
                Delete
              </button>
            </div>
          </div>
        </div>
      </div>
      <div class="absolute" v-if="!hideConfirmAlert">
        <delete-confirm @close-dialog="toggleConfirmAlert" @confirm-dialog="deletePicture"/>
      </div>
    </div>
  </loading-block-u-i>
</template>

<style class="css" scoped>
.form {
  box-shadow: rgba(0, 0, 0, 0.24) 0 3px 8px;
}
</style>