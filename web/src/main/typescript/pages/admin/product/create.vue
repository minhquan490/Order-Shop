<script lang="ts">
import { Subject, Subscription, filter, map, mergeMap, of, takeUntil } from 'rxjs';
import { ErrorMessage, Field, Form } from 'vee-validate';
import { number, object, string } from 'yup';
import { Category } from '~/types/category.type';
import { UploadFileResult } from '~/types/file-upload-result.type';
import { Product } from '~/types/product.type';

export default {
  components: {
    Form,
    Field,
    ErrorMessage
  },
  setup() {
    const clientProvider = useHttpClient().value;
    const fileUpload = useFileUpload().value;
    const serverUrl = useAppConfig().serverUrl;
    const categoriesUrl = `${serverUrl}/admin/category/list`;
    const productCreateUrl = `${serverUrl}/admin/product/create`;
    const fileUploadUrl = `${serverUrl}/admin/files/upload`;
    const flushFileUrl = `${serverUrl}/admin/files/flush`;
    
    const formSchema = object({
      name: string().required('Name is required').min(4, 'Name must be greater than 4').max(32, 'Name must be less than 32'),
      price: number().required('Price must be specific').min(0, 'Price must be positive').typeError('Price must be a number').required('Price is required'),
      size: string().min(1, 'Size must be greater than 1 char').max(10, 'Size must be less than 10 char').required("Size is required").matches(RegexUtils.getProductSizeRegex(), 'Size is invalid'),
      color: string().required('Color is required').min(2, 'Name must be greater than 2 char').max(10, 'Name must be less than 10 char'),
      taobao_url: string().matches(RegexUtils.getHttpRegex(), 'Enter correct url!').required('Please enter website'),
      orderPoint: number().min(0, 'Order point must greater than 0').typeError('Order point must be a number').required('Order point is required')
    });

    return {
      clientProvider,
      categoriesUrl,
      productCreateUrl,
      fileUploadUrl,
      flushFileUrl,
      formSchema,
      fileUpload
    }
  },
  data() {
    const subscriptions: Array<Subscription> = [];
    const pageData: PageData = {
      categories: [],
      categoryError: '',
      createProductSuccessMsg: '',
      createProductErrorMsg: []
    };
    return {
      subscriptions,
      pageData
    }
  },
  beforeMount() {
    const subscription = of(this.categoriesUrl)
      .pipe(
        map(url => this.clientProvider(url)),
        map(service => service.get<undefined, Category[]>()),
        map(resp => {
          if (resp.isError || resp.getResponse === null) {
            return [];
          }
          return resp.getResponse as Category[];
        })
      )
      .subscribe(categories => {
        categories.forEach(category => this.pageData?.categories.push(category))
      });
    this.subscriptions?.push(subscription);
  },
  beforeUnmount() {
    if (this.subscriptions) {
      this.subscriptions.forEach(sub => sub.unsubscribe());
    }
  },
  methods: {
    submit(value: Record<string, string>) {
      const elements = this.$refs['categories'] as HTMLInputElement[];
      let checkedElements: HTMLInputElement[];
      if (elements) {
        checkedElements = elements.filter(ele => ele.checked);
      } else {
        checkedElements = [];
      }
      if (checkedElements.length === 0 && this.pageData) {
        this.pageData.categoryError = 'Categories is required';
        return;
      }
      const productActive = (this.$refs['product-active'] as HTMLInputElement).checked;
      const textDescription = (this.$refs['product-description'] as HTMLTextAreaElement).value;
      const files = (this.$refs['product-pictures'] as HTMLInputElement).files;
      const stopSignal = new Subject<UploadFileResult | null>();

      stopSignal.subscribe(value => {
        if (this.pageData && value?.isError) {
          (this.$refs['danger-alert'] as Element).classList.toggle('hidden');
          setTimeout(() => {
            if (this.pageData) {
              this.pageData.createProductErrorMsg = value.getMessages;
            }
          }, 100);
          setTimeout(() => {
            setTimeout(() => {
              (this.$refs['danger-alert'] as Element).classList.toggle('hidden');
            }, 1000);
            if (this.pageData) {
              this.pageData.createProductErrorMsg = [];
            }
          }, 3000);
        }
      });

      of(this.productCreateUrl)
        .pipe(
          map(url => {
            const service = this.clientProvider(url);
            const product: Product = JSON.parse(JSON.stringify(value));
            return { service, product };
          }),
          map(mappedValue => {
            mappedValue.product.categories = checkedElements.map(ele => ele.value);
            mappedValue.product.isActive = productActive;
            mappedValue.product.description = textDescription;
            return mappedValue;
          }),
          map(mappedValue => {
            const req: ProductReq = {
              product_name: mappedValue.product.name,
              product_price: mappedValue.product.price,
              product_size: mappedValue.product.size,
              product_taobao_url: mappedValue.product.taobao_url,
              product_description: mappedValue.product.description,
              product_enabled: mappedValue.product.isActive.toString(),
              product_categories: mappedValue.product.categories,
              product_order_point: mappedValue.product.orderPoint,
              product_color: mappedValue.product.color
            }
            const service = mappedValue.service;
            return { req, service };
          }),
          map(mappedValue => {
            const res = mappedValue.service.post<ProductReq, ProductResp>(mappedValue.req);
            if (res.isError) {
              stopSignal.next(new UploadFileResult(true, ['Has problem when create product']));
            }
            return res.getResponse;
          }),
          mergeMap(async res => {
            const service = this.fileUpload(this.fileUploadUrl, this.flushFileUrl);

            if (files !== null) {
              for (const file of files) {
                const result = service.uploadFile(file);
                stopSignal.next(result);
              }
            } else {
              stopSignal.next(null);
            }

            return res;
          }),
          takeUntil(stopSignal.pipe(filter(signal => signal === null || signal.isError)))
        )
        .subscribe(value => {
          if (this.pageData) {
            (this.$refs['success-alert'] as Element).classList.toggle('hidden');
            setTimeout(() => {
              if (this.pageData) {
                this.pageData.createProductSuccessMsg = 'Create product successfully';
              }
            }, 100);
            setTimeout(() => {
              setTimeout(() => {
                (this.$refs['success-alert'] as Element).classList.toggle('hidden');
              }, 1000);
              if (this.pageData) {
                this.pageData.createProductSuccessMsg = '';
              }
            }, 3000);
          }
          //TODO handle create failure
        });
      return;
    }
  }
}

type PageData = {
  categories: Array<Category>,
  categoryError: string,
  createProductSuccessMsg: string,
  createProductErrorMsg: Array<string>
}

type ProductReq = {
  product_name: string,
  product_price: string,
  product_size: string,
  product_color: string,
  product_taobao_url: string,
  product_description: string,
  product_enabled: string,
  product_categories: Array<string>,
  product_order_point: string
}

type ProductResp = {
  id: string,
  name: string,
  price: string,
  size: string,
  color: string,
  taobao_url: string,
  description: string,
  pictures: Array<string>,
  categories: Array<string>
}
</script>

<template>
  <div class="product-create">
    <div class="pt-24 px-8 grid grid-cols-4">
      <div class="absolute w-[32rem]">
        <Breadcrumb />  
      </div>
      <div class="flex items-center justify-center col-span-4 pb-4">
        <h1 class="text-xl font-semibold">Create new product</h1>
      </div>
      <Form @submit="(value) => submit(value)" :validation-schema="formSchema"
        class="col-start-2 col-span-2 p-8 grid grid-cols-2 gap-6 rounded-lg form">
        <div class="col-span-1">
          <div class="relative">
            <span class="hover:cursor-default pb-1 pl-1">Name</span>
            <Field name="name" type="text" class="outline-none px-3 border border-gray-400 rounded-lg leading-7 w-full" />
            <ErrorMessage name="name" class="text-red-600 absolute -bottom-[1.3rem] right-0 w-max text-sm visible" />
          </div>
        </div>
        <div class="col-span-1">
          <div class="relative">
            <span class="hover:cursor-default pb-1 pl-1">Price</span>
            <Field name="price" type="number"
              class="outline-none px-3 border border-gray-400 rounded-lg leading-7 w-full" />
            <ErrorMessage name="price" class="text-red-600 absolute -bottom-[1.3rem] right-0 w-max text-sm visible" />
          </div>
        </div>
        <div class="col-span-1">
          <div class="relative">
            <span class="hover:cursor-default pb-1 pl-1">Size</span>
            <Field name="size" type="text" class="outline-none px-3 border border-gray-400 rounded-lg leading-7 w-full" />
            <ErrorMessage name="size" class="text-red-600 absolute -bottom-[1.3rem] right-0 w-max text-sm visible" />
          </div>
        </div>
        <div class="col-span-1">
          <div class="relative">
            <span class="hover:cursor-default pb-1 pl-1">Color</span>
            <Field name="color" type="text"
              class="outline-none px-3 border border-gray-400 rounded-lg leading-7 w-full" />
            <ErrorMessage name="color" class="text-red-600 absolute -bottom-[1.3rem] right-0 w-max text-sm visible" />
          </div>
        </div>
        <div class="col-span-1">
          <div class="relative">
            <span class="hover:cursor-default pb-1 pl-1">Taobao url</span>
            <Field name="taobao_url" type="text"
              class="outline-none px-3 border border-gray-400 rounded-lg leading-7 w-full" />
            <ErrorMessage name="taobao_url"
              class="text-red-600 absolute -bottom-[1.3rem] right-0 w-max text-sm visible" />
          </div>
        </div>
        <div class="col-span-1">
          <div class="relative">
            <span class="hover:cursor-default pb-1 pl-1">Order point</span>
            <Field name="orderPoint" type="number"
              class="outline-none px-3 border border-gray-400 rounded-lg leading-7 w-full" />
            <ErrorMessage name="orderPoint" class="text-red-600 absolute -bottom-[1.3rem] right-0 w-max text-sm" />
          </div>
        </div>
        <div class="col-span-1 grid grid-cols-2 gap-4">
          <div class="col-span-1 relative">
            <span class="hover:cursor-default">Category</span>
            <details>
              <summary class="text-sm border px-1 py-[0.25rem] rounded-md border-gray-400 bg-white hover:cursor-pointer">
                --- Choose ---</summary>
              <ul v-if="pageData?.categories.length !== 0" class="absolute bg-white w-full mt-1 rounded-md shadow-[0_5px_15px_5px_rgba(0,0,0,0.35)]">
                <li v-for="data in pageData?.categories" class="hover:bg-gray-300 py-2 px-4">
                  <label>
                    <input type="checkbox" ref="categories" :value="data.id" />
                    {{ data.name }}
                  </label>
                </li>
              </ul>
              <ul v-else-if="pageData?.categories.length === 0" class="absolute bg-white w-full mt-1 rounded-md shadow-[0_5px_15px_5px_rgba(0,0,0,0.35)]">
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
          <div class="col-span-1 grid grid-rows-2">
            <div class="row-span-1"></div>
            <div class="row-span-1 flex items-center justify-center flex-row-reverse">
              <span class="hover:cursor-default pl-4">Is enable</span>
              <input type="checkbox" class="w-max" ref="product-active">
            </div>
          </div>
        </div>
        <div class="col-span-1">
          <span class="hover:cursor-default">Media</span>
          <input ref="product-pictures" type="file" class="w-full">
        </div>
        <div class="col-span-2">
          <span class="hover:cursor-default">Description</span>
          <textarea ref="product-description" class="w-full outline-none border border-gray-400" rows="3"></textarea>
        </div>
        <div class="col-span-2">
          <ButtonSubmit :name="'Create product'" :textSize="'0.875rem'" :color="'white'" :bgColor="'rgb(22 163 74)'" />
        </div>
      </Form>
    </div>
    <div class="fixed right-0 top-20 backdrop-blur-md hidden" ref="success-alert">
      <AlertSuccess :class="(pageData?.createProductSuccessMsg.length === 0) ? 'translate-x-14' : 'translate-x-0'"
        :content="pageData?.createProductSuccessMsg" />
    </div>
    <div class="fixed right-0 top-20 backdrop-blur-md hidden" ref="danger-alert">
      <AlertDanger :class="(pageData?.createProductErrorMsg.length === 0) ? 'translate-x-14' : 'translate-x-0'"
        :contents="pageData?.createProductErrorMsg" />
    </div>
  </div>
</template>

<style lang="scss" scoped>
$page_bg: #f4f6f9;

.product-create {
  background-color: $page_bg;
  padding-bottom: 1.5rem;
  min-height: 100vh;
  position: relative;

  & .form {
    box-shadow: rgba(0, 0, 0, 0.19) 0px 10px 20px, rgba(0, 0, 0, 0.23) 0px 6px 6px;
  }
}
</style>
