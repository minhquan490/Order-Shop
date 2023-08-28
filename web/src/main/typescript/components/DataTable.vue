<script lang="ts">
import {TableAction, TableData, TableHeader} from "~/types";
import {
  calculatePaginate,
  filter,
  filterByCategories,
  goLeft,
  goRight,
  sliceData,
  sort
} from "~/logic/components/datatable.logic";

export default {
  props: {
    headers: {
      type: Array<TableHeader>,
      default: [],
      required: false,
    },
    tableData: {
      type: Array<TableData>,
      default: [],
      required: false
    },
    buttonNew: {
      default: {
        icon: 'ic:round-plus',
        buttonContent: 'New'
      },
      required: false
    },
    filters: {
      default: {
        title: 'Choose',
        categories: []
      },
      required: false
    },
    actions: {
      type: Array<TableAction>,
      default: [],
      required: false
    }
  },
  setup() {
    const urlReg: RegExp = useRegex().httpRegex;
    return {
      urlReg
    };
  },
  emits: ['info-data', 'new-data', 'search-data', 'delete-data'],
  data() {
    const newTable = (): Table => {
      const defaultItemPerPage = 10;
      let totalPage = this.tableData.length / defaultItemPerPage;
      if (totalPage < 1) {
        totalPage = 1;
      }
      return {
        headers: this.headers as Array<TableHeader>,
        source: this.tableData as Array<TableData>,
        itemsPerPage: defaultItemPerPage,
        currentPage: 1,
        totalPage: totalPage,
        paginate: []
      };
    }

    const table: Table = newTable();
    const searchKeyword: string = '';
    const displayActionDropdown = false;
    const displayFilterDropdown = false;
    const displayConfirmDialog = false;
    const deletedData: TableData | undefined = undefined;
    const selectedCategories: Array<string> = [];
    return {
      table,
      searchKeyword,
      displayActionDropdown,
      displayFilterDropdown,
      displayConfirmDialog,
      deletedData,
      selectedCategories
    }
  },
  methods: {
    search(event: Event) {
      filter(event, this);
    },
    next() {
      goRight(this);
    },
    previous() {
      goLeft(this);
    },
    infoData(data: TableData) {
      this.$emit('info-data', data);
    },
    deleteData(data: TableData) {
      this.displayConfirmDialog = true;
      this.deletedData = data as any;
    },
    newData() {
      this.$emit('new-data');
    },
    searchData() {
      this.$emit('search-data', this.searchKeyword);
    },
    toggleActionDropdown() {
      this.displayActionDropdown = !this.displayActionDropdown;
    },
    toggleFilterDropdown() {
      this.displayFilterDropdown = !this.displayFilterDropdown;
    },
    listenCloseDialog() {
      this.displayConfirmDialog = false;
    },
    deleteDataListener() {
      if (this.deletedData) {
        this.$emit('delete-data', this.deletedData);
        this.deletedData = undefined;
      }
    },
    sortTable(header: string, event: Event) {
      sort(this, event, header);
    },
    filterByCategory(event: Event) {
      event.preventDefault();
      filterByCategories(event, this);
    }
  },
  beforeMount() {
    this.table.paginate = calculatePaginate(this.table.currentPage, this.table.totalPage);
    this.table.source = sliceData(this.table.source, this);
  },
}

type Table = {
  headers: Array<TableHeader>,
  itemsPerPage: number,
  currentPage: number,
  source: Array<TableData>,
  totalPage: number,
  paginate: Array<{ page: string, disabled: boolean }>
}
</script>

<template>
  <ClientOnly>
    <div class="bg-white relative shadow-md sm:rounded-lg overflow-hidden">
      <div class="flex flex-col md:flex-row items-center justify-between space-y-3 md:space-y-0 md:space-x-4 p-4">
        <div class="w-full md:w-[60%]">
          <form class="flex items-center justify-center">
            <label for="simple-search" class="sr-only">Search</label>
            <div class="relative w-full">
              <div class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                <svg aria-hidden="true" class="w-5 h-5 text-gray-500" fill="currentColor"
                     viewbox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
                  <path fill-rule="evenodd"
                        d="M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z"
                        clip-rule="evenodd"/>
                </svg>
              </div>
              <input v-model="searchKeyword"
                     type="text"
                     id="simple-search"
                     class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full pl-10 p-2"
                     placeholder="Search"
                     @input="search">
            </div>
            <button @click="searchData"
                    type="button"
                    class="inline-block rounded px-5 pb-2 pt-2.5 text-xs font-medium uppercase leading-normal text-white shadow-[0_4px_9px_-4px_#3b71ca] transition duration-150 ease-in-out hover:bg-blue-600 hover:shadow-[0_8px_9px_-4px_rgba(59,113,202,0.3),0_4px_18px_0_rgba(59,113,202,0.2)] focus:bg-blue-600 focus:shadow-[0_8px_9px_-4px_rgba(59,113,202,0.3),0_4px_18px_0_rgba(59,113,202,0.2)] focus:outline-none focus:ring-0 active:bg-blue-700 active:shadow-[0_8px_9px_-4px_rgba(59,113,202,0.3),0_4px_18px_0_rgba(59,113,202,0.2)] bg-blue-500 w-[30%] ml-3">
              <span>Advanced Search</span>
            </button>
          </form>
        </div>
        <div
            class="w-full md:w-auto flex flex-col md:flex-row space-y-2 md:space-y-0 items-stretch md:items-center justify-end md:space-x-3 flex-shrink-0">
          <button @click="newData"
                  type="button"
                  class="inline-block rounded px-5 pb-2 pt-2.5 text-xs font-medium uppercase leading-normal text-white shadow-[0_4px_9px_-4px_#3b71ca] transition duration-150 ease-in-out hover:bg-blue-600 hover:shadow-[0_8px_9px_-4px_rgba(59,113,202,0.3),0_4px_18px_0_rgba(59,113,202,0.2)] focus:bg-blue-600 focus:shadow-[0_8px_9px_-4px_rgba(59,113,202,0.3),0_4px_18px_0_rgba(59,113,202,0.2)] focus:outline-none focus:ring-0 active:bg-blue-700 active:shadow-[0_8px_9px_-4px_rgba(59,113,202,0.3),0_4px_18px_0_rgba(59,113,202,0.2)] bg-blue-500">
            <Icon :name="buttonNew.icon" width="22" height="22"/>
            {{ buttonNew.buttonContent }}
          </button>
          <div class="flex items-center space-x-3 w-full md:w-auto relative">
            <button id="actionsDropdownButton" data-dropdown-toggle="actionsDropdown"
                    class="w-full md:w-auto flex items-center justify-center py-2 px-4 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-200"
                    type="button"
                    @click="toggleActionDropdown">
              <svg class="-ml-1 mr-1.5 w-5 h-5" fill="currentColor" viewbox="0 0 20 20"
                   xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                <path clip-rule="evenodd" fill-rule="evenodd"
                      d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z"/>
              </svg>
              Actions
            </button>
            <div v-if="displayActionDropdown"
                 class="z-10 w-44 bg-white rounded divide-y divide-gray-100 shadow absolute top-10 -left-3">
              <template v-if="actions.length === 0">
                <span class="p-1 text-sm text-gray-700 hover:cursor-default">No actions available</span>
              </template>
              <template v-else>
                <ul class="py-1 text-sm text-gray-700" aria-labelledby="actionsDropdownButton">
                  <li v-for="action in actions" :key="action.name">
                    <button class="block py-2 px-4 hover:bg-gray-100 w-full flex"
                            @click="action.callback($event, this)">
                      <span v-text="action.name"></span>
                    </button>
                  </li>
                </ul>
              </template>
            </div>
            <button id="filterDropdownButton" data-dropdown-toggle="filterDropdown"
                    class="w-full md:w-auto flex items-center justify-center py-2 px-4 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-200"
                    type="button"
                    @click="toggleFilterDropdown">
              <svg xmlns="http://www.w3.org/2000/svg" aria-hidden="true" class="h-4 w-4 mr-2 text-gray-400"
                   viewbox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd"
                      d="M3 3a1 1 0 011-1h12a1 1 0 011 1v3a1 1 0 01-.293.707L12 11.414V15a1 1 0 01-.293.707l-2 2A1 1 0 018 17v-5.586L3.293 6.707A1 1 0 013 6V3z"
                      clip-rule="evenodd"/>
              </svg>
              Filter
              <svg class="-mr-1 ml-1.5 w-5 h-5" fill="currentColor" viewbox="0 0 20 20"
                   xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                <path clip-rule="evenodd" fill-rule="evenodd"
                      d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z"/>
              </svg>
            </button>
            <div v-if="displayFilterDropdown" id="filterDropdown"
                 class="z-10 w-48 p-3 bg-white rounded-lg shadow absolute top-10 right-0">
              <template v-if="filters.categories.length === 0">
                <span class="hover:cursor-default">No filter available</span>
              </template>
              <template v-else>
                <h6 class="mb-3 text-sm font-medium text-gray-900 hover:cursor-default" v-text="filters.title"></h6>
                <ul class="space-y-2 text-sm" aria-labelledby="filterDropdownButton">
                  <li class="flex items-center" v-for="(category, i) in filters.categories" :key="category">
                    <input :id="i"
                           :value="category"
                           type="checkbox"
                           class="w-4 h-4 bg-gray-100 border-gray-300 rounded text-blue-600 focus:ring-blue-500"
                           @change="filterByCategory">
                    <label :for="i" class="ml-2 text-sm font-medium text-gray-900" v-text="category"></label>
                  </li>
                </ul>
              </template>
            </div>
          </div>
        </div>
      </div>
      <div class="overflow-x-auto">
        <table class="w-full text-sm text-left text-gray-500">
          <caption style="display: none;">Table data</caption>
          <thead class="text-xs text-gray-700 uppercase bg-gray-50">
          <tr>
            <th v-for="header in this.table.headers" scope="col" class="px-4 py-3" :key="header">
              <div class="flex items-center justify-center">
                <span class="w-max hover:cursor-default" v-text="header.name"></span>
                <button ref="table-head-button"
                        :disabled="table.source.length === 0"
                        @click="sortTable(header.dataPropertyName, $event)">
                  <svg class="w-3 h-3 ml-1.5" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="currentColor"
                       viewBox="0 0 24 24">
                    <path
                        d="M8.574 11.024h6.852a2.075 2.075 0 0 0 1.847-1.086 1.9 1.9 0 0 0-.11-1.986L13.736 2.9a2.122 2.122 0 0 0-3.472 0L6.837 7.952a1.9 1.9 0 0 0-.11 1.986 2.074 2.074 0 0 0 1.847 1.086Zm6.852 1.952H8.574a2.072 2.072 0 0 0-1.847 1.087 1.9 1.9 0 0 0 .11 1.985l3.426 5.05a2.123 2.123 0 0 0 3.472 0l3.427-5.05a1.9 1.9 0 0 0 .11-1.985 2.074 2.074 0 0 0-1.846-1.087Z"/>
                  </svg>
                </button>
              </div>
            </th>
            <th scope="col" class="px-4 text-center w-[10rem]">
              Actions
            </th>
          </tr>
          </thead>
          <tbody v-if="table.source.length !== 0">
          <tr v-for="data in table.source" class="border-b">
            <td v-for="header in this.table.headers" class="px-4 py-3 w-max text-center">
              <template v-if="urlReg.test(data[header.dataPropertyName])">
                <a class="underline text-blue-600 block w-full min-w-max" :href="data[header.dataPropertyName]"
                   v-text="data[header.dataPropertyName]"></a>
              </template>
              <template v-else>
                <template v-if="header.dataPropertyName === 'id'">
                  <a class="underline text-blue-600 hover:cursor-pointer block w-full min-w-max"
                     v-text="data[header.dataPropertyName]"
                     @click="infoData(data)"></a>
                </template>
                <template v-else>
                  <span class="hover:cursor-default w-max block text-center mx-auto"
                        v-text="data[header.dataPropertyName]"></span>
                </template>
              </template>
            </td>
            <td class="py-3 flex items-center justify-around">
              <button @click="deleteData(data)"
                      class="inline-block rounded px-3 pb-2 pt-2.5 text-xs font-medium uppercase leading-normal text-white shadow-[0_4px_9px_-4px_#3b71ca] transition duration-150 ease-in-out hover:bg-red-600 hover:shadow-[0_8px_9px_-4px_rgba(59,113,202,0.3),0_4px_18px_0_rgba(59,113,202,0.2)] focus:bg-red-600 focus:shadow-[0_8px_9px_-4px_rgb(202,72,59,0.3),0_4px_18px_0_rgb(202,59,59,0.2)] focus:outline-none focus:ring-0 active:bg-red-700 active:shadow-[0_8px_9px_-4px_rgba(59,113,202,0.3),0_4px_18px_0_rgba(59,113,202,0.2)] bg-red-500"
                      type="button"
                      v-text="'Delete'">
              </button>
            </td>
          </tr>
          </tbody>
          <tbody v-else>
          <tr>
            <td :colspan="this.table.headers.length + 1" class="bg-gray-300 text-center">No data available</td>
          </tr>
          </tbody>
        </table>
      </div>
      <nav class="flex flex-col md:flex-row justify-between items-start md:items-center space-y-3 md:space-y-0 p-4"
           aria-label="Table navigation">
        <template v-if="tableData.length !== 0">
                  <span class="text-sm font-normal text-gray-500">
                    Showing
                    <span class="font-semibold text-gray-900"
                          v-text="`1-${table.source.length > table.itemsPerPage ? table.itemsPerPage : table.source.length}`"></span>
                    of
                    <span class="font-semibold text-gray-900" v-text="tableData.length"></span>
                </span>
        </template>
        <template v-if="tableData.length !== 0">
          <ul class="inline-flex items-stretch -space-x-px">
            <li>
              <button @click="previous"
                      class="flex items-center justify-center h-full py-1.5 px-3 ml-0 text-gray-500 bg-white rounded-l-lg border border-gray-300 hover:bg-gray-100 hover:text-gray-700">
                <span class="sr-only">Previous</span>
                <svg class="w-5 h-5" aria-hidden="true" fill="currentColor" viewbox="0 0 20 20"
                     xmlns="http://www.w3.org/2000/svg">
                  <path fill-rule="evenodd"
                        d="M12.707 5.293a1 1 0 010 1.414L9.414 10l3.293 3.293a1 1 0 01-1.414 1.414l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 0z"
                        clip-rule="evenodd"/>
                </svg>
              </button>
            </li>
            <li v-for="(value) in table.paginate" :key="value.page">
              <button v-if="value.disabled"
                      class="flex items-center justify-center text-sm py-2 px-3 leading-tight text-gray-500 bg-gray-300 border border-gray-300"
                      v-text="value.page"
                      disabled></button>
              <button v-else
                      class="flex items-center justify-center text-sm py-2 px-3 leading-tight text-gray-500 bg-white border border-gray-300 hover:bg-gray-100 hover:text-gray-700"
                      v-text="value.page"></button>
            </li>
            <li>
              <button @click="next"
                      class="flex items-center justify-center h-full py-1.5 px-3 leading-tight text-gray-500 bg-white rounded-r-lg border border-gray-300 hover:bg-gray-100 hover:text-gray-700">
                <span class="sr-only">Next</span>
                <svg class="w-5 h-5" aria-hidden="true" fill="currentColor" viewbox="0 0 20 20"
                     xmlns="http://www.w3.org/2000/svg">
                  <path fill-rule="evenodd"
                        d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z"
                        clip-rule="evenodd"/>
                </svg>
              </button>
            </li>
          </ul>
        </template>
      </nav>
    </div>
  </ClientOnly>
  <div v-if="displayConfirmDialog">
    <DeleteConfirm @close-dialog="listenCloseDialog" @confirm-dialog="deleteDataListener"/>
  </div>
</template>
