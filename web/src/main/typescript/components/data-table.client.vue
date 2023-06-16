<script lang="ts">
import { TableHeaders } from '~/types/table-header.type';
import { TableActionCallback } from '~/types/table-store.type';

export default {
  props: {
    headers: Array<TableHeaders>,
    datas: Array<Record<string, any>>,
    itemPerPage: Number,
    height: String,
    currentPage: Number,
    tableIconName: String,
    tableTittle: String,
    setter: TableActionCallback
  },
  data() {
    const table: Table = {
      columns: (this.headers?.length) ? this.headers?.length : 1,
      itemsPerPage: (this.itemPerPage) ? this.itemPerPage : 5,
      height: (this.height) ? this.height : 'auto',
      currentPage: (this.currentPage) ? this.currentPage : 1,
      source: this.datas,
    }

    const calculatePage = (datas: Array<any> | undefined) => {
      let totalPage = (datas?.length) ? Math.floor(datas?.length / table.itemsPerPage) : 0;
      if (datas?.length as number % table.itemsPerPage !== 0) {
        totalPage = totalPage + 1;
      }
      return totalPage;
    }

    const sliceData = (datas: Array<any> | undefined, table: Table) => {
      if (table.itemsPerPage < 1) {
        table.itemsPerPage = 1;
      }
      table.source = datas
      return datas?.slice(table.itemsPerPage * (table.currentPage - 1), table.currentPage * table.itemsPerPage);
    }

    const tableRender = this.datas && this.datas.length !== 0;
    const totalPage = calculatePage(this.datas);
    table.data = sliceData(this.datas, table);
    const resetButtons = (event: Event, buttons: any) => {
      [...buttons].map(button => {
        if (button !== event.target) {
          button.removeAttribute("data-dir");
        }
      });
    };
    const tableIconName = (this.tableIconName) ? this.tableIconName : '';
    const tableTittle = (this.tableTittle) ? this.tableTittle : '';
    return {
      table,
      tableRender,
      totalPage,
      calculatePage,
      sliceData,
      resetButtons,
      tableIconName,
      tableTittle
    }
  },
  methods: {
    goRight() {
      if (this.table.currentPage === this.totalPage || this.table.source?.length === 0) {
        return;
      }
      this.table.currentPage = parseInt(this.table.currentPage.toString()) + parseInt('1');
      this.table.data = this.sliceData(this.table.source, this.table);
      this.storageCurrentPage(this.table.currentPage.toString());
    },
    goLeft() {
      if (this.table.currentPage === 1 || this.table.source?.length === 0) {
        return;
      }
      this.table.currentPage = parseInt(this.table.currentPage.toString()) - parseInt('1');
      this.table.data = this.sliceData(this.table.source, this.table);
      this.storageCurrentPage(this.table.currentPage.toString());
    },
    jump(event: KeyboardEvent) {
      event.preventDefault();
      if (event.key !== 'Enter' || this.table.source?.length === 0) {
        return;
      }
      if (this.table.currentPage < 1) {
        this.table.currentPage = 1;
      }
      if (this.table.currentPage > this.totalPage) {
        this.table.currentPage = this.totalPage;
      }
      this.table.data = this.sliceData(this.table.source, this.table);
      this.storageCurrentPage(this.table.currentPage.toString());
    },
    storageCurrentPage(currentPage: string) {
      const storage: Storage = useAppStorage().value;
      storage.setItem(useAppConfig().customerDataTableCurrentPage, currentPage);
    },
    filter(event: Event) {
      event.preventDefault();
      const ele: any = event.target;
      const searchValue: string = ele.value;
      if (searchValue.length === 0) {
        this.table.data = this.sliceData(this.datas, this.table);
        this.totalPage = this.calculatePage(this.datas);
        return;
      }
      const filteredData = this.datas?.filter(value => {
        for (const header of this.headers as Array<TableHeaders>) {
          const resolvedObject = toRaw(value);
          const proxyData = resolvedObject[header.dataPropertyName];
          const data = new String(proxyData);

          if (data.toLocaleUpperCase().indexOf(searchValue.toLocaleUpperCase()) > -1) {
            return value;
          }
        }
      });
      this.table.data = this.sliceData(filteredData, this.table);
      this.totalPage = this.calculatePage(filteredData);
    },
    changeDataSize(event: KeyboardEvent) {
      event.preventDefault();
      if (event.key !== 'Enter') {
        return;
      }
      this.table.currentPage = 1;
      this.table.data = this.sliceData(this.datas, this.table);
      this.totalPage = this.calculatePage(this.datas);
    },
    sort(header: string, event: Event) {
      let source = this.table.source;
      if (Array.isArray(source?.[0][header])) {
        return;
      }
      const buttons = toRaw(this.$refs['table-head-button']) as Array<Element>;
      this.resetButtons(event, buttons);
      const element = event.target as Element;
      if (element.getAttribute("data-dir") == "desc") {
        source = source?.reverse();
        element.setAttribute('data-dir', 'asc');
      } else {
        source = RecordSortUtils.mergeSort(header, source as Array<Record<string, any>>);
        element.setAttribute('data-dir', 'desc');
      }
      this.table.source = source;
      this.table.data = this.sliceData(source, this.table);
    },
    selectRow(refIndex: number) {
      // if (!this.setter) {
      //   return;
      // }
      const data = this.table.data as Record<string, any>[];
      const selectedClass = 'selected';
      const refs = this.$refs['table-body'] as Array<Element>;
      refs.forEach((ref, index) => {
        if (index === refIndex) {
          ref.classList.add(selectedClass);
          this.setter?.dispatchAction(data[index]);
        } else {
          ref.classList.remove(selectedClass);
        }
      });
    }
  }
}

type Table = {
  columns: number,
  data?: Array<Record<string, any>>,
  itemsPerPage: number,
  height: string,
  currentPage: number,
  source?: Array<Record<string, any>>
}
</script>

<template>
  <div>
    <div class="rounded-xl box-shadow w-full">
      <div>
        <div class="p-4">
          <Icon :name="tableIconName" width="28" height="28" />
          <span class="hover:cursor-default p-1" v-text="tableTittle"></span>
        </div>
      </div>
      <div class="rounded-b-xl overflow-hidden border-t border-gray-400">
        <div class="w-full px-8 pt-4 pb-0">
          <div class="flex items-center justify-between">
            <div class="flex">
              <div class="pr-2">
                <span>Show</span>
              </div>
              <div class="flex items-center justify-center w-[10%]">
                <input type="text" v-model="table.itemsPerPage"
                  class="text-center w-full bg-transparent outline-none border border-gray-300"
                  @keyup="$event => changeDataSize($event)">
              </div>
              <div class="pl-2">
                <span>entries</span>
              </div>
            </div>
            <div class="flex">
              <label for="search" class="pr-2">Search</label>
              <input @input="$event => filter($event)" id="search" type="text"
                class="border border-gray-400 rounded-lg outline-none px-3 bg-transparent">
            </div>
          </div>
          <table class="w-full table">
            <thead class="border-b">
              <tr class="grid p-4 row" :style="`--cols: ${table.columns}`">
                <th :id="header.dataPropertyName" v-for="header in headers" class="col-span-1 border-gray-400 w-3/4">
                  <button class="flex items-center w-full" @click="$event => sort(header.dataPropertyName, $event)"
                    ref="table-head-button">
                    <span class="hover:cursor-pointer text-xs w-max" v-text="header.name"></span>
                  </button>
                </th>
              </tr>
            </thead>
            <tbody :style="`--height: ${table.height}`" class="body block">
              <template v-if="tableRender">
                <tr v-for="(data, i) in table.data" class="grid p-4 hover:bg-gray-200 border-b row"
                  :style="`--cols: ${table.columns}`" @click="$event => selectRow(i)" ref="table-body">
                  <td v-for="header, i in headers" class="col-span-1 flex items-center border-gray-400">
                    <a v-if="header.isId && !header.isImg" :href="`/admin/product/info?id=${data[header.dataPropertyName]}`"
                      class="underline text-blue-500 text-sm" v-text="data[header.dataPropertyName]"></a>
                    <span v-if="!header.isId && !header.isImg" class="hover:cursor-default text-sm"
                      v-text="data[header.dataPropertyName]"></span>
                    <img v-if="header.isImg" :src="data[header.dataPropertyName][0]" class="h-full">
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
                  <button @click="$event => goLeft()" class="hover:cursor-pointer">
                    <Icon class="text-gray-500" name="raphael:arrowleft" width="28" height="28" />
                  </button>
                  <div class="flex items-center justify-center w-[5%]">
                    <input type="text" v-model="table.currentPage"
                      class="text-center w-full bg-transparent outline-none border border-gray-300" @keyup="$event => jump($event)">
                  </div>
                  <div class="px-2">
                    <span class="hover:cursor-default">/</span>
                  </div>
                  <div class="px-2">
                    <span class="hover:cursor-default" v-text="totalPage"></span>
                  </div>
                  <button @click="$event => goRight()" class="hover:cursor-pointer">
                    <Icon class="text-gray-500" name="raphael:arrowright" width="28" height="28" />
                  </button>
                </td>
              </tr>
            </tfoot>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.table {
  & .body {
    height: var(--height);
    overflow-y: scroll;

    &::-webkit-scrollbar {
      display: none;
    }
  }

  & .row {
    grid-template-columns: repeat(var(--cols), minmax(0, 1fr));
  }

  th {
    & button[data-dir="asc"] {
      &::after {
        padding-left: 1rem;
        content: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='8' height='8'%3E%3Cpolygon points='0, 0 8,0 4,8 8' fill='%23818688'/%3E%3C/svg%3E");
      }
    }

    & button[data-dir="desc"] {
      &::after {
        padding-left: 1rem;
        content: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='8' height='8'%3E%3Cpolygon points='4 0,8 8,0 8' fill='%23818688'/%3E%3C/svg%3E");
      }
    }
  }
}

.box-shadow {
  box-shadow: rgba(0, 0, 0, 0.35) 0px 5px 15px;
}
</style>
