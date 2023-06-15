<script lang="ts">
import { Subscription, map, of } from 'rxjs';
import { Category } from '~/types/category.type';
import { TableHeaders } from '~/types/table-header.type';
import { TableActionCallback } from '~/types/table-store.type';

export default {
  setup() {
    const categoryStore = useCategoryStore();
    const tableAction: TableActionCallback = new TableActionCallback(categoryStore, "selectedCategory");
    const clientProvider = useHttpClient().value;
    return {
      categoryStore,
      tableAction,
      clientProvider
    }
  },
  data() {
    if (process.server) {
      return {};
    }
    const pageData: PageData = new PageData();
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
    const subscription = of(`${serverUrl}/admin/category/list`)
      .pipe(
        map(url => this.clientProvider(url)),
        map(service => service.get<undefined, Category[]>()),
        map(resp => {
          // if (!Array.isArray(resp) && Object.keys(resp).length === 0) {
          //   return [];
          // }
          // return resp;
          return [
            {
              id: '1',
              name: 'Test cate 1'
            },
            {
              id: '2',
              name: 'Test cate 2'
            },
            {
              id: '3',
              name: 'Test cate 3'
            },
            {
              id: '4',
              name: 'Test cate 4'
            },
            {
              id: '5',
              name: 'Test cate 5'
            },
          ] as Category[];
        }),
        map(result => {
          const pageData = new PageData();
          pageData.tableHeaders = [];
          pageData.tableData = result;
          return pageData;
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
        if (this.pageData) {
          this.pageData.tableData = toRaw(result.tableData);
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
    deleteCategory() {
      if (!this.categoryStore.selectedCategory) {
        return;
      }
      const serverUrl = useAppConfig().serverUrl;
      // of(`${serverUrl}/admin/product/delete`)
      //   .pipe(
      //     map(url => this.clientProvider(url)),
      //     map(client => {
      //       const ca
      //     })
      //   )

    }
  },
}

class PageData {
  tableHeaders!: Array<TableHeaders>;
  tableData!: Array<Category>;
}
</script>

<template>
  <div class="category">
    <div class="pt-24 px-8 grid grid-cols-4 gap-3">
      <div class="col-span-2">
        <DataTable :setter="tableAction" :headers="pageData?.tableHeaders" :datas="pageData?.tableData" tableTittle="Categories" tableIconName="quill:label" />
      </div>
      <div class="col-span-1">
        <div class="rounded-md update p-6">
          <div class="flex items-center justify-center pb-3">
            <span class="hover:cursor-default">Update category</span>
          </div>
          <div class="pb-4">
            <span v-if="!categoryStore.selectedCategory" class="hover:cursor-default">ID:</span>
            <span v-if="categoryStore.selectedCategory" v-text="`ID: ${categoryStore.selectedCategory.id}`" class="hover:cursor-default"></span>
          </div>
          <div class="flex items-center pb-4">
            <span class="hover:cursor-default pr-3">Name:</span>
            <input class="leading-10 w-full px-4 bg-gray-200 outline-none rounded-md" type="text" :value="categoryStore.selectedCategory?.name">
          </div>
          <div class="grid grid-cols-2 gap-2 h-full w-full">
            <div class="col-span-1">
              <button @click="categoryStore.setCategory(undefined)" class="h-full w-full bg-green-600 text-white text-sm p-3 rounded-md hover:opacity-70 focus:translate-y-1 relative">Reset Selected</button>
            </div>
            <div class="col-span-1">
              <button class="bg-red-600 text-white text-sm p-3 rounded-md hover:opacity-70 h-full w-full focus:translate-y-1 relative">Delete</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
$page_bg: #f4f6f9;

.category {
  background-color: $page_bg;
  padding-bottom: 1.5rem;
  & .update {
    box-shadow: rgba(0, 0, 0, 0.16) 0px 3px 6px, rgba(0, 0, 0, 0.23) 0px 3px 6px;
  }
}
</style>
