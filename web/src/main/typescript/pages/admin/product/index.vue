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
    const deleteUrl = `${serverUrl}/admin/product/delete`;
    const productStore = useProductStore();
    const tableAction: TableActionCallback = new TableActionCallback(productStore, "selectedProduct");
    const navigator = useNavigation();
    return {
      clientProvider,
      listUrl,
      tableAction,
      productStore,
      navigator,
      deleteUrl
    }
  },
  data() {
    const pageData = new PageData()
    const subscriptions: Array<Subscription> = [];
    return {
      pageData,
      subscriptions
    }
  },
  beforeMount() {
    const subscription = of(this.listUrl)
      .pipe(
        map(url => this.clientProvider(url)),
        map(service => service.get<undefined, Product[]>()),
        map(resp => {
          if (resp.isError || resp.getResponse === null) {
            return [];
          }
          return resp.getResponse as Product[];
        }),
        map(resp => {
          const data = new PageData();
          data.tableData = resp;
          return data;
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
  mounted() {
    this.productStore.$reset();
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
      if (selectedProduct.id.length === 0) {
        (this.$refs['info-alert'] as Element).classList.toggle('hidden');
        setTimeout(() => {
          if (this.pageData) {
            this.pageData.updateMsgInfo = ['Select product to continue update'];
          }
        }, 100);
        setTimeout(() => {
          setTimeout(() => {
            (this.$refs['info-alert'] as Element).classList.toggle('hidden');
          }, 1000);
          if (this.pageData) {
            this.pageData.updateMsgInfo = [];
          }
        }, 3000);
        return;
      }
      this.navigator.navigate('/admin/product/update');
    },
    deleteProduct(event: MouseEvent) {
      event.preventDefault();
      const selectedProduct = this.productStore.getProduct;
      if (selectedProduct.id.length === 0) {
        (this.$refs['info-alert'] as Element).classList.toggle('hidden');
        setTimeout(() => {
          if (this.pageData) {
            this.pageData.updateMsgInfo = ['Select product to continue delete it'];
          }
        }, 100);
        setTimeout(() => {
          setTimeout(() => {
            (this.$refs['info-alert'] as Element).classList.toggle('hidden');
          }, 1000);
          if (this.pageData) {
            this.pageData.updateMsgInfo = [];
          }
        }, 3000);
        return;
      }
      of(this.deleteUrl)
        .pipe(
          map(url => this.clientProvider(url)),
          map(service => service.delete<{ product_id: string }, { status: number, messages: string[] }>({ product_id: this.productStore.getProduct.id }))
        )
        .subscribe(res => {
          if (res.isError) {
            (this.$refs['danger-alert'] as Element).classList.toggle('hidden');
            setTimeout(() => {
              if (this.pageData && res.getResponse !== null) {
                this.pageData.deleteMsgErr = res.getResponse.messages;
              }
            }, 100);
            setTimeout(() => {
              setTimeout(() => {
                (this.$refs['danger-alert'] as Element).classList.toggle('hidden');
              }, 1000);
              if (this.pageData) {
                this.pageData.deleteMsgErr = [];
              }
            }, 3000);
          } else {
            (this.$refs['success-alert'] as Element).classList.toggle('hidden');
            setTimeout(() => {
              if (this.pageData && res.getResponse !== null) {
                this.pageData.deleteMsgSuccess = res.getResponse.messages[0];
              }
            }, 100);
            setTimeout(() => {
              setTimeout(() => {
                (this.$refs['success-alert'] as Element).classList.toggle('hidden');
              }, 1000);
              if (this.pageData) {
                this.pageData.deleteMsgSuccess = '';
              }
            }, 3000);
          }
        })
    }
  }
}

class PageData {
  tableHeaders: Array<TableHeaders> = [];
  tableData: Array<Product> = [];
  updateMsgInfo: Array<string> = [];
  deleteMsgErr: Array<string> = [];
  deleteMsgSuccess: string = '';
}
</script>

<template>
  <div class="product">
    <div class="pt-24 px-8">
      <div class="pb-4 flex items-center justify-end">
        <div class="pr-4">
          <ButtonBasic :name="'Delete product'" :clickFunc="deleteProduct" :bgColor="'rgb(239 68 68)'" />
        </div>
        <div class="pr-4">
          <ButtonBasic :name="'Update product'" :clickFunc="updateProduct" :bgColor="'rgb(59 130 246)'" />
        </div>
        <div>
          <ButtonBasic :name="'Create product'" :clickFunc="redirectToCreatePage" />
        </div>
      </div>
      <div>
        <DataTable :headers="pageData?.tableHeaders" :datas="pageData?.tableData" tableIconName="fluent-mdl2:product-list"
          tableTittle="Products information" :setter="tableAction" />
      </div>
    </div>
    <div class="fixed right-0 top-20 backdrop-blur-md hidden" ref="info-alert">
      <AlertInfo :class="(pageData?.updateMsgInfo.length === 0) ? 'translate-x-16' : 'translate-x-0'"
        :contents="pageData?.updateMsgInfo" />
    </div>
    <div class="fixed right-0 top-20 backdrop-blur-md hidden" ref="danger-alert">
      <AlertDanger :class="(pageData?.deleteMsgErr.length === 0) ? 'translate-x-16' : 'translate-x-0'"
        :contents="pageData?.deleteMsgErr" />
    </div>
    <div class="fixed right-0 top-20 backdrop-blur-md hidden" ref="success-alert">
      <AlertSuccess :class="(pageData?.deleteMsgSuccess.length === 0) ? 'translate-x-16' : 'translate-x-0'"
        :content="pageData?.deleteMsgSuccess" />
    </div>
  </div>
</template>

<style lang="scss" scoped>
$page_bg: #f4f6f9;

.product {
  background-color: $page_bg;
  padding-bottom: 1.5rem;
  min-height: 100vh;
  position: relative;
}
</style>
