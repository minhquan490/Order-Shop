<script lang="ts">
import { TableHeaders } from '~/types/table-header.type';

export default {
  props: {
    headers: Array<TableHeaders>,
    datas: Array<Record<string, any>>,
    itemPerPage: Number,
    height: String,
    currentPage: Number
  },
  data() {
    const table: Table = {
      columns: (this.headers?.length) ? this.headers?.length : 1,
      data: this.datas,
      itemsPerPage: (this.itemPerPage) ? this.itemPerPage : 5,
      height: (this.height) ? this.height : '300px',
      currentPage: (this.currentPage) ? this.currentPage : 1
    }
    const tableRender = this.datas && this.datas.length !== 0;
    let totalPage = (this.datas?.length) ? Math.floor(this.datas?.length / table.itemsPerPage) : 0;
    if (this.datas?.length as number % table.itemsPerPage !== 0) {
      totalPage = totalPage + 1;
    }
    table.data = this.datas?.slice(table.itemsPerPage * (table.currentPage - 1), table.currentPage * table.itemsPerPage);
    return {
      table,
      tableRender,
      totalPage
    }
  },
  methods: {
    sliceData() {
      return this.datas?.slice(this.table.itemsPerPage * (this.table.currentPage - 1), this.table.currentPage * this.table.itemsPerPage);
    },
    goRight() {
      if (this.table.currentPage === this.totalPage) {
        return;
      }
      this.table.currentPage = parseInt(this.table.currentPage.toString()) + parseInt('1');
      this.table.data = this.sliceData();
      this.storageCurrentPage(this.table.currentPage.toString());
    },
    goLeft() {
      if (this.table.currentPage === 1) {
        return;
      }
      this.table.currentPage = parseInt(this.table.currentPage.toString()) - parseInt('1');
      this.table.data = this.sliceData();
      this.storageCurrentPage(this.table.currentPage.toString());
    },
    jump(event: KeyboardEvent) {
      event.preventDefault();
      if (event.key !== 'Enter') {
        return;
      }
      if (this.table.currentPage < 1) {
        this.table.currentPage = 1;
      }
      if (this.table.currentPage > this.totalPage) {
        this.table.currentPage = this.totalPage;
      }
      this.table.data = this.sliceData();
      this.storageCurrentPage(this.table.currentPage.toString());
    },
    storageCurrentPage(currentPage: string) {
      const storage: Storage = useAppStorage().value;
      storage.setItem(useAppConfig().customerDataTableCurrentPage, currentPage);
    }
  }
}

type Table = {
  columns: number,
  data?: Array<Record<string, any>>,
  itemsPerPage: number,
  height: string,
  currentPage: number
}
</script>

<template>
  <div class="w-full">
    <table class="w-full border-t border-gray-400 table">
      <thead class="border-b">
        <tr class="grid p-4 row" :style="`--cols: ${table.columns}`">
          <th v-for="header in headers" class="col-span-1 border-gray-400">
            <span class="hover:cursor-default text-sm" v-text="header.name"></span>
          </th>
        </tr>
      </thead>
      <tbody :style="`--height: ${table.height}`" class="body block pt-2">
        <template v-if="tableRender">
          <tr v-for="data in table.data" class="grid p-4 hover:bg-gray-200 border-b row" :style="`--cols: ${table.columns}`">
            <td v-for="header, i in headers" class="col-span-1 flex items-center justify-center border-gray-400">
              <span class="hover:cursor-default text-sm" v-text="data[header.dataPropertyName]"></span>
            </td>
          </tr>
        </template>
        <template v-if="!tableRender">
          <tr class="p-4 bg-gray-300 flex items-center justify-center">
            <td>
              <span class="hover:cursor-default">No record available</span>
            </td>
          </tr>
        </template>
      </tbody>
      <tfoot>
        <tr class="p-4 block">
          <td class="flex items-center justify-center">
            <div @click="goLeft" class="hover:cursor-pointer">
              <Icon class="text-gray-500" name="raphael:arrowleft" width="28" height="28" />
            </div>
            <div class="flex items-center justify-center w-[5%]">
              <input type="text" v-model="table.currentPage" class="text-center w-full bg-transparent outline-none border border-gray-300" @keyup="jump($event)">
            </div>
            <div class="px-2">
              <span class="hover:cursor-default">/</span>  
            </div>
            <div class="px-2">
              <span class="hover:cursor-default" v-text="totalPage"></span>  
            </div>
            <div @click="goRight" class="hover:cursor-pointer">
              <Icon class="text-gray-500" name="raphael:arrowright" width="28" height="28" />
            </div>
          </td>
        </tr>
      </tfoot>
    </table>
  </div>
</template>

<style lang="scss" scoped>
.table {
  & .body {
    max-height: var(--height);
    overflow-y: scroll;
    
    &::-webkit-scrollbar {
      display: none;
    }
  }
  & .row {
    grid-template-columns: repeat(var(--cols), minmax(0, 1fr));
  }
}
</style>
