<script lang="ts">
import { map, of } from 'rxjs';
import { HttpServiceProvider } from '~/services/http.service';
import { Category } from '~/types/category.type';
import { TableHeaders } from '~/types/table-header.type';

export default {
  inject: ['httpClient'],
  data() {
    if (process.server) {
      return {};
    }
    const pageData: PageData = {
      tableHeaders: [],
      tableData: []
    }
    return {
      pageData
    }
  },
  created() {
    if (process.server) {
      return;
    }
    const serverUrl = useAppConfig().serverUrl;
    const httpServiceProvider: HttpServiceProvider = this.httpClient as HttpServiceProvider;
    this.subscription = of(`${serverUrl}/admin/category/list`)
      .pipe(
        map(url => httpServiceProvider.open(url)),
        map(service => service.get<undefined, Category[]>()),
        map(resp => {
          if (!Array.isArray(resp) && Object.keys(resp).length === 0) {
            return [];
          }
          return resp;
        }),
        map(resp => {
          return {
            tableHeaders: [],
            tableData: resp
          } as PageData
        }),
        map(data => {
          const tableHeaders: TableHeaders[] = [
            {
              isId: true,
              isImg: false,
              name: 'ID',
              dataPropertyName: 'id'
            },
            {
              isId: false,
              isImg: false,
              name: 'Name',
              dataPropertyName: 'name'
            }
          ];
          data.tableHeaders = tableHeaders;
          return data;
        })
      )
      .subscribe(result => {
        const tableData = result.tableData;
        tableData.forEach(data => this.pageData?.tableData.push(data));
        const tableHeaders = result.tableHeaders;
        tableHeaders.forEach(header => this.pageData?.tableHeaders.push(header));
      })
  },
  beforeUnmount() {
    this.subscription.unsubscribe();
  }
}

type PageData = {
  tableHeaders: Array<TableHeaders>,
  tableData: Array<Category>
}
</script>

<template>
  <div class="category">
    <div class="pt-24 px-8 grid grid-cols-2 gap-3">
      <div class="col-span-1">
        <DataTable :headers="pageData?.tableHeaders" :datas="pageData?.tableData" tableTittle="Categories" tableIconName="quill:label" />
      </div>
      <div class="col-span-1">

      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
$page_bg: #f4f6f9;

.category {
  background-color: $page_bg;
  padding-bottom: 1.5rem;
}
</style>
