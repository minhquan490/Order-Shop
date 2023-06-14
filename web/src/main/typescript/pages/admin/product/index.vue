<script lang="ts">
import { Subscription, map, of } from 'rxjs';
import { HttpServiceProvider } from '~/services/http.service';
import { Product } from '~/types/product.type';
import { TableHeaders } from '~/types/table-header.type';

export default {
  inject: ['httpClient'],
  data() {
    if (process.server) {
      return {};
    }
    const pageData: PageData = {
      tableData: [],
      tableHeaders: []
    }
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
    const serverUrl = useAppConfig().serverUrl;
    const httpServiceProvider: HttpServiceProvider = this.httpClient as HttpServiceProvider;
    const subscription = of(`${serverUrl}/content/product/list`)
      .pipe(
        map(url => httpServiceProvider.open(url)),
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
            tableHeaders: [] as Array<TableHeaders>
          }
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
        const tableData = result.tableData;
        tableData.forEach(data => this.pageData?.tableData.push(data));
        const tableHeaders = result.tableHeaders;
        tableHeaders.forEach(header => this.pageData?.tableHeaders.push(header));
      })
    this.subscriptions?.push(subscription);
  },
  beforeUnmount() {
    if (this.subscriptions) {
      this.subscriptions.forEach(sub => sub.unsubscribe());
    }
  }
}

type PageData = {
  tableHeaders: Array<TableHeaders>,
  tableData: Array<Product>
}
</script>

<template>
  <div class="product">
    <div class="pt-24 px-8">
      <DataTable :headers="pageData?.tableHeaders" :datas="pageData?.tableData" tableIconName="fluent-mdl2:product-list" tableTittle="Products information" />
    </div>
  </div>
</template>

<style lang="scss" scoped>
$page_bg: #f4f6f9;

.product {
  background-color: $page_bg;
  padding-bottom: 1.5rem;
}
</style>
