<script lang="ts">
import { Subscription, map, of } from 'rxjs';
import { Product } from '~/types/product.type';
import { TableHeaders } from '~/types/table-header.type';
import { TableActionCallback } from '~/types/table-store.type';

export default {
  setup() {
    const clientProvider = useHttpClient().value;
    const serverUrl = useAppConfig().serverUrl;
    const pathPrefix = '/content/product';
    const listUrl = `${serverUrl}${pathPrefix}/list`;
    const productStore = useProductStore();
    const tableAction: TableActionCallback = new TableActionCallback(productStore, "selectedProduct");
    return {
      clientProvider,
      listUrl,
      tableAction,
      productStore
    }
  },
  data() {
    if (process.server) {
      return {};
    }
    const pageData = new PageData()
    const subscriptions: Array<Subscription> = [];
    return {
      pageData,
      subscriptions
    }
  },
  created() {
    if (process.server) {
      return;
    }
    const subscription = of(this.listUrl)
      .pipe(
        map(url => this.clientProvider(url)),
        map(service => service.get<undefined, Product[]>()),
        map(resp => {
          if (!Array.isArray(resp) && Object.keys(resp).length === 0) {
            return [];
          }
          return resp;
        }),
        map(resp => {
          return {
            tableData: resp,
            tableHeaders: []
          } as PageData
        }),
        map(data => {
          const tableHeaers: TableHeaders[] = [
            {
              isId: true,
              name: 'ID',
              dataPropertyName: 'id',
              isImg: false
            },
            {
              isId: false,
              name: 'Name',
              dataPropertyName: 'name',
              isImg: false
            },
            {
              isId: false,
              name: 'Price',
              dataPropertyName: 'price',
              isImg: false
            },
            {
              isId: false,
              name: 'Size',
              dataPropertyName: 'size',
              isImg: false
            },
            {
              isId: false,
              name: 'Color',
              dataPropertyName: 'color',
              isImg: false
            },
            {
              isId: false,
              name: 'Taobao url',
              dataPropertyName: 'taobao_url',
              isImg: false
            },
            {
              isId: false,
              name: 'Description',
              dataPropertyName: 'description',
              isImg: false
            },
            {
              isId: false,
              name: 'Picture',
              dataPropertyName: 'pictures',
              isImg: true
            },
            {
              isId: false,
              name: 'Categories',
              dataPropertyName: 'categories',
              isImg: false
            }
          ];
          data.tableHeaders = tableHeaers
          return data;
        }),
      )
      .subscribe(result => {
        if (this.pageData) {
          this.pageData.tableData = result.tableData;
          this.pageData.tableHeaders = result.tableHeaders;
        }
      })
    this.subscriptions?.push(subscription);
  },
  beforeUnmount() {
    if (this.subscriptions) {
      this.subscriptions.forEach(sub => sub.unsubscribe());
    }
  },
  methods: {
    async redirectToCreatePage(event: MouseEvent) {
      event.preventDefault();
      const navigator = useNavigation();
      await navigator.navigate('/admin/product/create');
    },
    updateProduct(event: MouseEvent) {
      event.preventDefault();
      const selectedProduct = this.productStore.getProduct;
      console.log(selectedProduct);
    }
  }
}

class PageData {
  tableHeaders: Array<TableHeaders> = [];
  tableData: Array<Product> = []
}
</script>

<template>
  <div class="product">
    <div class="pt-24 px-8">
      <div class="pb-4 flex items-center justify-end">
        <div class="pr-4">
          <ButtonBasic :name="'Update product'" :clickFunc="updateProduct" :bgColor="'rgb(59 130 246)'" />
        </div>
        <div>
          <ButtonBasic :name="'Create product'" :clickFunc="redirectToCreatePage" />
        </div>
      </div>
      <div>
        <DataTable :headers="pageData?.tableHeaders" 
                 :datas="pageData?.tableData" 
                 tableIconName="fluent-mdl2:product-list"
                 tableTittle="Products information" 
                 :setter="tableAction" />
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
$page_bg: #f4f6f9;

.product {
  background-color: $page_bg;
  padding-bottom: 1.5rem;
  min-height: 100vh;
}
</style>
