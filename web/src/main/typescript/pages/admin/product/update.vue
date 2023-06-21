<script lang="ts">
import { Subscription, map, of } from 'rxjs';
import { ErrorMessage, Field, Form } from 'vee-validate';
import { number, object, string } from 'yup';
import { Category } from '~/types/category.type';


export default {
  components: {
    Form,
    Field,
    ErrorMessage
  },
  setup() {
    const clientProvider = useHttpClient().value;
    const productStore = useProductStore();
    const selectedProduct = productStore.getProduct
    const navigator = useNavigation();
    const serverUrl = useAppConfig().serverUrl;
    const categoriesUrl = `${serverUrl}/admin/category/list`;

    const formSchema = object({
      name: string().required('Name is required').min(4, 'Name must be greater than 4').max(32, 'Name must be less than 32'),
      price: number().required('Price must be specific').min(0, 'Price must be positive').typeError('Price must be a number').required('Price is required'),
      size: string().max(10, 'Size must be less than 10 char').required("Size is required").matches(RegexUtils.getProductSizeRegex(), 'Size is invalid'),
      color: string().required('Color is required').min(2, 'Name must be greater than 2 char').max(10, 'Name must be less than 10 char'),
      taobao_url: string().matches(RegexUtils.getHttpRegex(), 'Enter correct url!').required('Please enter website'),
      orderPoint: number().min(0, 'Order point must greater than 0').typeError('Order point must be a number').required('Order point is required')
    });

    return {
      productStore,
      selectedProduct,
      navigator,
      formSchema,
      categoriesUrl,
      clientProvider
    }
  },
  data() {
    if (process.server) {
      return {};
    }

    const pageData: PageData = new PageData();
    return {
      pageData
    }
  },
  methods: {

  },
  beforeMount() {
    this.productStore.$reset();
    if (this.selectedProduct && this.selectedProduct.id.length === 0) {
      alert('Please select product before access this page');
      this.navigator.navigate('/admin/product');
      return;
    }
    const subscription = of(this.categoriesUrl)
      .pipe(
        map(url => this.clientProvider(url)),
        map(service => service.get<undefined, Category[]>()),
        map(resp => {
          if (!Array.isArray(resp) && Object.keys(resp).length === 0) {
            return [];
          }
          return resp;
        })
      )
      .subscribe(categories => {
        categories.forEach(category => this.pageData?.categories.push(category))
      });
    this.pageData?.subscriptions.push(subscription);
  },
  beforeUnmount() {
    if (this.pageData) {
      this.pageData.subscriptions.forEach(s => s.unsubscribe());
    }
  },
}

class PageData {
  subscriptions: Array<Subscription> = [];
  categories: Array<Category> = [];
  categoryError: string = '';
}
</script>

<template>
  <div class="update">
    <div class="pt-24 px-8">
      <div class="flex items-center justify-center col-span-4 pb-4">
        <span class="text-xl font-semibold">Update product</span>
      </div>
      <div class="grid grid-cols-6">
        <Form class="col-start-2 col-span-4 p-8 grid grid-cols-4 gap-3 rounded-lg form" :validation-schema="formSchema">
          <div class="col-span-2">
            <div class="row-span-1">
              <CarouselBasic :piture-urls="[]" />
            </div>
            <div class="row-span-1 pt-6">
              <span class="hover:cursor-default">Upload product img</span>
              <input ref="product-pictures" type="file" class="w-full">
            </div>
          </div>
          <div class="col-span-2 grid grid-cols-2 gap-6">
            <div class="col-span-1">
              <div class="relative">
                <span class="hover:cursor-default pb-1 pl-1">Name</span>
                <Field name="name" type="text"
                  class="outline-none px-3 border border-gray-400 rounded-lg leading-7 w-full"
                  v-model="selectedProduct.name" />
                <ErrorMessage name="name" class="text-red-600 absolute -bottom-[1.3rem] right-0 w-max text-sm visible" />
              </div>
            </div>
            <div class="col-span-1">
              <div class="relative">
                <span class="hover:cursor-default pb-1 pl-1">Price</span>
                <Field name="price" type="number"
                  class="outline-none px-3 border border-gray-400 rounded-lg leading-7 w-full"
                  v-model="selectedProduct.price" />
                <ErrorMessage name="price" class="text-red-600 absolute -bottom-[1.3rem] right-0 w-max text-sm visible" />
              </div>
            </div>
            <div class="col-span-1">
              <div class="relative">
                <span class="hover:cursor-default pb-1 pl-1">Size</span>
                <Field name="size" type="text"
                  class="outline-none px-3 border border-gray-400 rounded-lg leading-7 w-full"
                  v-model="selectedProduct.size" />
                <ErrorMessage name="size" class="text-red-600 absolute -bottom-[1.3rem] right-0 w-max text-sm visible" />
              </div>
            </div>
            <div class="col-span-1">
              <div class="relative">
                <span class="hover:cursor-default pb-1 pl-1">Color</span>
                <Field name="color" type="text"
                  class="outline-none px-3 border border-gray-400 rounded-lg leading-7 w-full"
                  v-model="selectedProduct.color" />
                <ErrorMessage name="color" class="text-red-600 absolute -bottom-[1.3rem] right-0 w-max text-sm visible" />
              </div>
            </div>
            <div class="col-span-1">
              <div class="relative">
                <span class="hover:cursor-default pb-1 pl-1">Taobao url</span>
                <Field name="taobao_url" type="text"
                  class="outline-none px-3 border border-gray-400 rounded-lg leading-7 w-full"
                  v-model="selectedProduct.taobao_url" />
                <ErrorMessage name="taobao_url"
                  class="text-red-600 absolute -bottom-[1.3rem] right-0 w-max text-sm visible" />
              </div>
            </div>
            <div class="col-span-1">
              <div class="relative">
                <span class="hover:cursor-default pb-1 pl-1">Order point</span>
                <Field name="orderPoint" type="number"
                  class="outline-none px-3 border border-gray-400 rounded-lg leading-7 w-full"
                  v-model="selectedProduct.orderPoint" />
                <ErrorMessage name="orderPoint" class="text-red-600 absolute -bottom-[1.3rem] right-0 w-max text-sm" />
              </div>
            </div>
            <div class="col-span-1">
              <div class="col-span-1 relative">
                <span class="hover:cursor-default">Category</span>
                <details>
                  <summary
                    class="text-sm border px-1 py-[0.25rem] rounded-md border-gray-400 bg-white hover:cursor-pointer">
                    --- Choose category---</summary>
                  <ul v-if="pageData?.categories.length !== 0"
                    class="absolute bg-white w-full mt-1 rounded-md shadow-[0_5px_15px_5px_rgba(0,0,0,0.35)]">
                    <li v-for="data in pageData?.categories" class="hover:bg-gray-300 py-2 px-4">
                      <label>
                        <input type="checkbox" ref="categories" :value="data.id" />
                        {{ data.name }}
                      </label>
                    </li>
                  </ul>
                  <ul v-else-if="pageData?.categories.length === 0"
                    class="absolute bg-white w-full mt-1 rounded-md shadow-[0_5px_15px_5px_rgba(0,0,0,0.35)]">
                    <li class="hover:bg-gray-300 py-2 px-1">
                      <label class="text-sm">
                        No data available
                      </label>
                    </li>
                  </ul>
                </details>
                <span class="hover:cursor-default text-red-600 absolute -bottom-[1.3rem] right-0 w-max text-sm"
                  v-text="pageData?.categoryError"></span>
              </div>
            </div>
            <div class="col-span-1 flex justify-between flex-col">
              <div></div>
              <div class="row-span-1 flex items-center justify-center flex-row-reverse">
                <span class="hover:cursor-default pl-1">Enabled</span>
                <input v-if="selectedProduct.isActive" type="checkbox" class="w-max" ref="product-active" checked>
                <input v-if="!selectedProduct.isActive" type="checkbox" class="w-max" ref="product-active">
              </div>
            </div>
            <div class="col-span-2">
              <span class="hover:cursor-default">Description</span>
              <textarea ref="product-description" class="w-full outline-none border border-gray-400" rows="3"
                v-bind:value="selectedProduct.description"></textarea>
            </div>
            <div class="col-span-2">
              <ButtonSubmit :name="'Update product'" :textSize="'0.875rem'" :color="'white'"
                :bgColor="'rgb(96 165 250)'" />
            </div>
          </div>
        </Form>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
$page_bg: #f4f6f9;

.update {
  background-color: $page_bg;
  padding-bottom: 1.5rem;
  min-height: 100vh;
  position: relative;

  & .form {
    box-shadow: rgba(0, 0, 0, 0.19) 0px 10px 20px, rgba(0, 0, 0, 0.23) 0px 6px 6px;
  }
}
</style>