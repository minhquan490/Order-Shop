<script lang="ts">
import {Category, NavBarsSource} from "~/types";
import {
  navigationSources,
  removeCategory,
  selectCategory,
  setProductEnabled,
  submitRequest,
  validateBeforeSubmit,
  validateProductColor,
  validateProductName,
  validateProductOrderPoint,
  validateProductPrice,
  validateProductSize,
  validateTaobaoUrl
} from "~/logic/pages/admin/product/create.logic";

export default {
  setup() {
    const navigation: NavBarsSource[] = navigationSources();
    return {
      navigation
    }
  },
  data() {
    const categories: Category[] = [
      {id: '1', name: 'a'},
    ];
    const request: ProductCreateReq = this.initRequest();
    const validationError: ValidationError = this.initValidateError();
    const selectedCategories: Category[] = [];
    const callServerError: Array<string> = [];
    const isLoading: boolean = false;
    const categoryListHide = true;
    const hideAlert = true;
    const isSubmitted = false;
    return {
      selectedCategories,
      categories,
      request,
      validationError,
      callServerError,
      isLoading,
      categoryListHide,
      hideAlert,
      isSubmitted
    }
  },
  methods: {
    removeCategory(category: Category) {
      removeCategory(category, this);
    },
    selectCategory(category: Category) {
      selectCategory(category, this);
    },
    initRequest(): ProductCreateReq {
      return {
        product_categories: [],
        product_color: '',
        product_description: '',
        product_enabled: 'true',
        product_name: '',
        product_order_point: '',
        product_price: '',
        product_size: '',
        product_taobao_url: ''
      };
    },
    initValidateError(): ValidationError {
      return {
        productCategoriesError: '',
        productColorError: '',
        productDescriptionError: '',
        productNameError: '',
        productOrderPointError: '',
        productPriceError: '',
        productSizeError: '',
        productTaobaoUrlError: ''
      };
    },
    toggleEnableProduct(event: Event) {
      setProductEnabled(event, this);
    },
    submit(event: Event) {
      event.preventDefault();
      if (this.isSubmitted) {
        return;
      }
      this.isSubmitted = true;
      this.isLoading = true;
      this.request.product_categories = this.selectedCategories.map(value => value.id);
      const result = validateBeforeSubmit(this.request, this);
      if (!result) {
        this.isSubmitted = false;
        return;
      }
      submitRequest(this.request, this);
      return;
    },
    validateName() {
      validateProductName(this.request, this);
    },
    validatePrice() {
      validateProductPrice(this.request, this);
    },
    validateSize() {
      validateProductSize(this.request, this);
    },
    validateColor() {
      validateProductColor(this.request, this);
    },
    validateOrderPoint() {
      validateProductOrderPoint(this.request, this);
    },
    validateTaobaoUrl() {
      validateTaobaoUrl(this.request, this);
    },
    toggleCategoryList() {
      this.categoryListHide = !this.categoryListHide;
    },
    alertHiddenListener() {
      this.hideAlert = !this.hideAlert;
    }
  }
}

type ProductCreateReq = {
  product_name: string,
  product_price: string,
  product_size: string,
  product_color: string,
  product_taobao_url: string,
  product_description: string,
  product_enabled: string,
  product_categories: string[],
  product_order_point: string
}

type ValidationError = {
  productNameError: string,
  productPriceError: string,
  productSizeError: string,
  productColorError: string,
  productTaobaoUrlError: string,
  productDescriptionError: string,
  productCategoriesError: string,
  productOrderPointError: string,
}
</script>

<template>
  <LoadingBlockUI :loading="isLoading">
    <div class="relative">
      <NavBarsAdmin :sources="navigation"/>
      <PageHeader title="Product create" :bread-crumbs-enable="true"/>
      <AdminContentArea>
        <div class="mx-auto w-1/2">
          <form class="bg-white rounded-md grid grid-cols-2 gap-x-8 gap-y-4 p-8">
            <h3 class="col-span-2 font-bold text-center pb-3 text-xl">Create new product</h3>
            <div class="relative z-0 w-full mb-6 group col-span-1">
              <input type="text"
                     name="productName"
                     id="productName"
                     class="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                     placeholder=" "
                     v-model="request.product_name"
                     @blur="validateName"/>
              <label for="productName"
                     class="peer-focus:font-medium absolute text-sm text-gray-500 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">
                Name
              </label>
              <p v-if="validationError.productNameError.length !== 0" class="mt-2 text-sm text-red-600 relative">
                <span class="text-sm" v-text="validationError.productNameError"></span>
              </p>
            </div>
            <div class="relative z-0 w-full mb-6 group col-span-1">
              <input type="text"
                     name="productPrice"
                     id="productPrice"
                     class="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                     placeholder=" "
                     v-model="request.product_price"
                     @blur="validatePrice"/>
              <label for="productPrice"
                     class="peer-focus:font-medium absolute text-sm text-gray-500 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">
                Price
              </label>
              <p v-if="validationError.productPriceError.length !== 0" class="mt-2 text-sm text-red-600 relative">
                <span class="text-sm" v-text="validationError.productPriceError"></span>
              </p>
            </div>
            <div class="relative z-0 w-full mb-6 group col-span-1">
              <input type="text"
                     name="productSize"
                     id="productSize"
                     class="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                     placeholder=" "
                     v-model="request.product_size"
                     @blur="validateSize"/>
              <label for="productSize"
                     class="peer-focus:font-medium absolute text-sm text-gray-500 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">
                Size
              </label>
              <p v-if="validationError.productSizeError.length !== 0" class="mt-2 text-sm text-red-600 relative">
                <span class="text-sm" v-text="validationError.productSizeError"></span>
              </p>
            </div>
            <div class="relative z-0 w-full mb-6 group col-span-1">
              <input type="text"
                     name="productColor"
                     id="productColor"
                     class="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                     placeholder=" "
                     v-model="request.product_color"
                     @blur="validateColor"/>
              <label for="productColor"
                     class="peer-focus:font-medium absolute text-sm text-gray-500 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">
                Color
              </label>
              <p v-if="validationError.productColorError.length !== 0" class="mt-2 text-sm text-red-600 relative">
                <span class="text-sm" v-text="validationError.productColorError"></span>
              </p>
            </div>
            <div class="relative z-0 w-full mb-6 group col-span-1">
              <input type="text"
                     name="productOrderPoint"
                     id="productOrderPoint"
                     class="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                     placeholder=" "
                     v-model="request.product_order_point"
                     @blur="validateOrderPoint"/>
              <label for="productOrderPoint"
                     class="peer-focus:font-medium absolute text-sm text-gray-500 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">
                Order point
              </label>
              <p v-if="validationError.productOrderPointError.length !== 0" class="mt-2 text-sm text-red-600 relative">
                <span class="text-sm" v-text="validationError.productOrderPointError"></span>
              </p>
            </div>
            <div class="relative z-0 w-full mb-6 group col-span-1">
              <div @click="toggleCategoryList"
                   tabindex="0"
                   id="underline_select"
                   class="block py-2.5 px-0 w-full text-sm text-gray-500 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-gray-200 focus:cursor-text hover:cursor-text peer">
                <h1 class="test">a</h1>
              </div>
              <div
                  :class="categoryListHide ? 'select-box max-h-[5rem] overflow-scroll hidden' : 'select-box max-h-[5rem] overflow-scroll block'">
                <div @click="selectCategory(category)"
                     v-for="category in categories"
                     :key="category.id"
                     class="py-1 px-3 text-sm hover:bg-gray-300 hover:cursor-pointer flex items-center justify-between">
                  <span v-text="category.name"></span>
                  <Icon v-if="selectedCategories.indexOf(category) >= 0"
                        name="typcn:tick" width="16" height="16"
                        color="rgb(34 197 94)"/>
                </div>
              </div>
              <label v-if="selectedCategories.length === 0" for="underline_select"
                     class="peer-focus:font-medium peer-focus:hidden absolute text-sm text-gray-500 duration-300 transform top-3 -z-10 origin-[0] peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0">
                Category
              </label>
              <div class="absolute top-3 w-full overflow-hidden">
                  <span v-for="category in selectedCategories"
                        :key="category.id"
                        class="inline-flex items-center rounded-md bg-gray-50 text-xs font-medium text-gray-600 ring-1 ring-inset ring-gray-500/10">
                    <span class="pl-2 border-r-2 border-gray-300 pr-2 hover:cursor-default py-1"
                          v-text="category.name"></span>
                    <span class="pl-1 hover:cursor-pointer pr-2 py-1 hover:bg-gray-200 rounded-r-md"
                          @click="removeCategory(category)">x</span>
                  </span>
              </div>
              <p v-if="validationError.productCategoriesError.length !== 0" class="mt-2 text-sm text-red-600 relative">
                <span class="text-sm" v-text="validationError.productCategoriesError"></span>
              </p>
            </div>
            <div class="relative z-0 w-full mb-6 group col-span-2">
              <div class="grid grid-cols-3 gap-3">
                <div class="col-span-2">
                  <input type="text"
                         name="productTaobaoUrl"
                         id="productTaobaoUrl"
                         class="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                         placeholder=" "
                         v-model="request.product_taobao_url"
                         @blur="validateTaobaoUrl"/>
                  <label for="productTaobaoUrl"
                         class="peer-focus:font-medium absolute text-sm text-gray-500 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">
                    Taobao url
                  </label>
                  <p v-if="validationError.productTaobaoUrlError.length !== 0"
                     class="mt-2 text-sm text-red-600 relative">
                    <span class="text-sm" v-text="validationError.productTaobaoUrlError"></span>
                  </p>
                </div>
                <div class="col-span-1 flex justify-center items-end">
                  <div>
                    <input id="productEnable"
                           type="checkbox"
                           value="true"
                           class="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500 focus:ring-2"
                           @change="toggleEnableProduct">
                    <label for="productEnable" class="ml-2 text-sm font-medium text-gray-900">Enable</label>
                  </div>
                </div>
              </div>
            </div>
            <div class="relative z-0 w-full mb-6 group col-span-2">
              <label for="message" class="block mb-2 text-sm font-medium text-gray-900">
                Description
              </label>
              <textarea id="message" rows="3"
                        class="block p-2.5 w-full text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300 focus:ring-blue-500 focus:border-blue-500"
                        placeholder="Description..."
                        v-model="request.product_description"></textarea>
              <p v-if="validationError.productDescriptionError.length !== 0" class="mt-2 text-sm text-red-600 relative">
                <span class="text-sm" v-text="validationError.productDescriptionError"></span>
              </p>
            </div>
            <div class="relative z-0 w-full mb-6 group col-span-2">
              <button @click="submit"
                      type="submit"
                      class="flex w-full justify-center rounded-md bg-indigo-600 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
                      :disabled="isSubmitted">
                Create product
              </button>
            </div>
          </form>
        </div>
      </AdminContentArea>
      <div class="absolute top-[10rem] right-0 w-[25%]">
        <ClientOnly>
          <AlertListError @hidden="alertHiddenListener"
                          :hidden="hideAlert"
                          :error-messages="callServerError"
                          :title="'Can not create product: '"/>
        </ClientOnly>
      </div>
    </div>
  </LoadingBlockUI>
</template>

<style scoped>
.test {
  text-indent: -9999px;
}

.select-box {
  box-shadow: rgba(0, 0, 0, 0.05) 0 6px 24px 0, rgba(0, 0, 0, 0.08) 0 0 0 1px;
}
</style>
