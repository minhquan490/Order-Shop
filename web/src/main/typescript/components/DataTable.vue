<script lang="ts" setup>
import {NextPage, TableData, TableFilter, TableHeader, TableNewData} from "~/types";
import {Ref} from "vue";

const props = defineProps({
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
    } as TableNewData,
    required: false
  },
  filters: {
    default: {
      title: 'Choose',
      categories: []
    } as TableFilter,
    required: false
  },
  hideDelete: {
    type: Boolean,
    default: false,
    required: false
  },
  enableIdLink: {
    type: Boolean,
    default: true,
    required: false
  },
  itemPerPage: {
    type: Number,
    default: 10,
    required: false
  },
  serverPageSize: {
    type: Number,
    default: -1,
    required: false
  },
  serverTotalItems: {
    type: Number,
    default: -1,
    required: false
  },
  serverPageNum: {
    type: Number,
    default: -1,
    required: false
  },
  enableAdvanceSearch: {
    type: Boolean,
    default: true,
    required: false
  },
  enableNewButton: {
    type: Boolean,
    default: true,
    required: false
  }
});

const emit = defineEmits(['info-data', 'new-data', 'search-data', 'delete-data', 'next-page']);

const urlReg: RegExp = useRegex().httpRegex;

const newTable = (currentPage: number, pool: TableData[]): Table => {
  let totalPage = props.tableData?.length / props.itemPerPage;
  totalPage = (totalPage - Math.floor(totalPage)) > 0 ? Math.floor(totalPage) + 1 : totalPage;
  if (totalPage < 1) {
    totalPage = 1;
  }
  return <Table>{
    headers: props.headers,
    source: pool,
    itemsPerPage: props.itemPerPage,
    currentPage: currentPage,
    totalPage: totalPage,
    paginate: []
  };
}

const dataPool = ref<TableData[]>(props.tableData as TableData[]);
const table = ref(newTable(1, unref(dataPool)));
const searchKeyword = ref('');
const displayFilterDropdown = ref(false);
const displayConfirmDialog = ref(false);
const deletedData: Ref<TableData | undefined> = ref(undefined);
const selectedCategories: Ref<Array<string>> = ref([]);
const buttonEl = ref<HTMLButtonElement[] | null>(null);

function search(event: Event) {
  filter(event);
}

function next() {
  goRight();
}

function previous() {
  goLeft();
}

function infoData(data: TableData) {
  emit('info-data', data);
}

function deleteData(data: TableData) {
  displayConfirmDialog.value = true;
  deletedData.value = data;
}

function newData() {
  emit('new-data');
}

function searchData() {
  emit('search-data', searchKeyword.value);
}

function toggleFilterDropdown() {
  displayFilterDropdown.value = !displayFilterDropdown.value;
}

function listenCloseDialog() {
  displayConfirmDialog.value = false;
}

function deleteDataListener() {
  if (deletedData.value) {
    emit('delete-data', deletedData.value);
    deletedData.value = undefined;
  }
}

function sortTable(header: string, event: Event) {
  sort(event, header);
}

function filterByCategory(event: Event) {
  event.preventDefault();
  filterByCategories(event);
}

function goToPage(page: number) {
  goTo(page);
}

onMounted(() => {
  table.value.paginate = calculatePaginate(table.value.currentPage, table.value.totalPage);
  table.value.source = sliceData(table.value.source);
});

const sliceData = (data: Array<TableData>): Array<any> => {
  table.value.source = data;
  const currentPage: number = table.value.currentPage;
  const itemsPerPage: number = table.value.itemsPerPage;
  return data.slice(itemsPerPage * (currentPage - 1), currentPage * itemsPerPage);
}

const calculatePage = (data: Array<TableHeader>): number => {
  const itemsPerPage: number = table.value.itemsPerPage;
  let totalPage: number = data.length === 0 ? 0 : Math.floor(data.length / itemsPerPage);
  if (data.length % itemsPerPage !== 0) {
    totalPage = totalPage + 1;
  }
  return totalPage;
}

const filter = (event: Event): void => {
  event.preventDefault();
  const element: HTMLInputElement = event.target as HTMLInputElement;
  const searchValue: string = element.value;
  if (searchValue.length === 0) {
    table.value.source = sliceData(unref(dataPool));
    table.value.totalPage = calculatePage(props.headers as TableHeader[]);
    return;
  }
  const result = filterByKeyword(searchValue);
  table.value.source = result.data;
  table.value.totalPage = result.totalPage;
}

const calculatePaginate = (currentPage: number, totalPage: number): Array<{ page: string, disabled: boolean }> => {
  const paginate: Array<{ page: string, disabled: boolean }> = [];
  if (totalPage <= 4) {
    for (let i = 1; i <= totalPage; i++) {
      paginate.push({page: i.toString(), disabled: i === currentPage});
    }
    return paginate;
  } else {
    if (currentPage > 3) {
      paginate.push({page: "...", disabled: false});
    }
    if (currentPage - 3 < 1) {
      for (let i = 1; i <= 3; i++) {
        paginate.push({page: i.toString(), disabled: currentPage === i});
      }
    } else if (currentPage === totalPage - 1) {
      for (let i = 2; i > 0; i--) {
        paginate.push({page: (currentPage - i).toString(), disabled: false});
      }
      paginate.push({page: currentPage.toString(), disabled: true});
    } else if (currentPage === totalPage) {
      for (let i = 3; i > 0; i--) {
        paginate.push({page: (currentPage - i).toString(), disabled: false});
      }
      paginate.push({page: currentPage.toString(), disabled: true});
    } else {
      paginate.push({page: (currentPage - 1).toString(), disabled: false});
      paginate.push({page: currentPage.toString(), disabled: true});
    }
    if (currentPage < (totalPage - 1)) {
      paginate.push({page: "...", disabled: false});
    }
    if (currentPage !== totalPage) {
      paginate.push({page: totalPage.toString(), disabled: false});
    }
    return paginate;
  }
}

const goLeft = async (): Promise<void> => {
  if (table.value.currentPage === 1 || table.value.source.length === 0) {
    return;
  }
  await goTo(parseInt(table.value.currentPage.toString()) - 1);
}

const goRight = async (): Promise<void> => {
  if (table.value.currentPage === table.value.totalPage || table.value.source.length === 0) {
    return;
  }
  await goTo(parseInt(table.value.currentPage.toString()) + 1);
}

const callNextPage = async (): Promise<void> => {
  if (props.serverPageNum > 0 && props.serverPageSize > 0) {
    const serverPageNum = props.serverPageNum;
    const itemsPerPage = props.serverPageSize;
    if (serverPageNum && itemsPerPage) {
      const next: NextPage = {
        page: serverPageNum.valueOf() + 1,
        itemsPerPage: itemsPerPage
      };
      emit('next-page', next);
    }
  }
}

const goTo = async (page: number): Promise<void> => {
  table.value.currentPage = page;
  table.value.source = sliceData(unref(dataPool));
  table.value.paginate = calculatePaginate(table.value.currentPage, table.value.totalPage);
  const pageRemaining: number = table.value.totalPage - page;
  let endOfData: boolean = false;
  if (props.serverPageNum > 0 && props.serverPageSize > 0 && props.serverTotalItems > 0) {
    endOfData = props.serverTotalItems === props.serverPageNum * props.serverPageSize;
  }
  if (pageRemaining == 0 && !endOfData) {
    await callNextPage();
  }
}

const resetButtons = (event: Event, buttons: any): void => {
  [...buttons].forEach(button => {
    if (button !== event.target) {
      button.removeAttribute("data-dir");
    }
  });
};

const mergeUnsorted = (property: string, sources: Array<Record<string, string>>, left: number, mid: number, right: number): Array<Record<string, string>> => {
  const n1 = mid - left + 1;
  const n2 = right - mid;

  const leftArray = new Array<Record<string, string>>(n1);
  const rightArray = new Array<Record<string, string>>(n2);

  for (let i = 0; i < n1; i++) {
    leftArray[i] = sources[left + i];
  }
  for (let i = 0; i < n2; i++) {
    rightArray[i] = sources[mid + 1 + i];
  }

  let firstIndex = 0;
  let secondIndex = 0;
  let mergedIndex = left;

  while (firstIndex < n1 && secondIndex < n2) {
    const l: string = String(leftArray[firstIndex][property]).valueOf();
    const r: string = String(rightArray[secondIndex][property]).valueOf();
    const compared: boolean = compare(l, r);
    if (compared) {
      sources[mergedIndex] = leftArray[firstIndex];
      firstIndex++;
    } else {
      sources[mergedIndex] = rightArray[secondIndex];
      secondIndex++;
    }
    mergedIndex++;
  }

  while (firstIndex < n1) {
    sources[mergedIndex] = leftArray[firstIndex];
    firstIndex++;
    mergedIndex++;
  }

  while (secondIndex < n2) {
    sources[mergedIndex] = rightArray[secondIndex];
    secondIndex++;
    mergedIndex++
  }

  return sources;
}

const mergeSort = (property: string, sources: Array<Record<string, string>>): Array<Record<string, string>> => {
  const left = 0;
  const right = sources.length - 1;
  const sorted: Array<Record<string, string>> = [];
  for (const record of sources) {
    sorted.push(toRaw(record));
  }
  return merge(property, sorted, left, right);
}

const merge = (property: string, sources: Array<Record<string, string>>, left: number, right: number): Array<Record<string, string>> => {
  if (left >= right) {
    return sources;
  }
  const mid: number = left + Math.floor((right - left) / 2);
  sources = merge(property, sources, left, mid);
  sources = merge(property, sources, mid + 1, right);
  sources = mergeUnsorted(property, sources, left, mid, right);
  return sources;
}

const compare = (left: string, right: string): boolean => {
  const numberLeft: number = Number(left).valueOf();
  const numberRight: number = Number(right).valueOf();
  return (left !== '' && right !== '' && !isNaN(numberLeft) && !isNaN(numberRight)) ? numberLeft <= numberRight : left.localeCompare(right) <= 0;
}

const sort = (event: Event, header: string): void => {
  let source = unref(dataPool);
  if (Array.isArray(source?.[0][header])) {
    return;
  }
  const buttons: Element[] = toRaw(buttonEl.value) as Array<Element>;
  resetButtons(event, buttons);
  const element: Element = event.target as Element;
  if (element.getAttribute("data-dir") == "desc") {
    source = source.reverse();
    element.setAttribute('data-dir', 'asc');
  } else {
    source = mergeSort(header, source as Array<Record<string, any>>);
    element.setAttribute('data-dir', 'desc');
  }
  table.value.source = sliceData(source);
  dataPool.value = source;
}

const filterByCategories = (event: Event): void => {
  const target: HTMLInputElement = event.target as HTMLInputElement;
  if (target.checked) {
    selectedCategories.value.push(target.value);
  } else {
    const categories: Array<string> = selectedCategories.value;
    selectedCategories.value = categories.filter((value: string): boolean => value !== target.value);
  }
  if (selectedCategories.value.length === 0) {
    return;
  } else {
    const filteredData: ReturnType<typeof filterByKeyword> = {
      totalPage: 0,
      data: []
    };
    const categories: Array<string> = selectedCategories.value;
    categories.forEach(category => {
      const processedData = filterByKeyword(category, 'categories');
      filteredData.data.push(processedData.data);
      filteredData.totalPage = filteredData.totalPage + processedData.totalPage;
    });
    table.value.source = filteredData.data;
    table.value.totalPage = filteredData.totalPage;
  }
}

const filterByKeyword = (keyword: string, property?: string): {
  data: TableData[],
  totalPage: number
} => {
  const searchValue: string = keyword;
  let filteredData;
  const tableData = unref(dataPool);
  if (property) {
    filteredData = tableData.filter(value => {
      const resolvedObject = toRaw(value);
      const proxyData = resolvedObject[property];
      const data: string = String(proxyData);
      const locale: string = navigator.languages[0];
      if (data.toLocaleUpperCase(locale).indexOf(searchValue.toLocaleUpperCase(locale)) > -1) {
        return value;
      }
    });
  } else {
    filteredData = tableData.filter(value => {
      for (const header of props.headers) {
        const resolvedObject = toRaw(value);
        const proxyData = resolvedObject[header.dataPropertyName];
        const data: string = String(proxyData);
        const locale: string = navigator.languages[0];
        if (data.toLocaleUpperCase(locale).indexOf(searchValue.toLocaleUpperCase(locale)) > -1) {
          return value;
        }
      }
    });
  }
  table.value.source = sliceData(filteredData);
  table.value.totalPage = calculatePage(filteredData);
  return {
    data: sliceData(filteredData),
    totalPage: calculatePage(filteredData)
  }
}

watch(() => props.tableData, async () => {
  dataPool.value = props.tableData as TableData[];
  table.value = newTable(table.value.currentPage, dataPool.value);
  table.value.paginate = calculatePaginate(table.value.currentPage, table.value.totalPage);
  table.value.source = sliceData(table.value.source);
})

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
  <div class="bg-white relative shadow-md sm:rounded-lg overflow-hidden">
    <div class="flex flex-col md:flex-row items-center justify-between space-y-3 md:space-y-0 md:space-x-4 p-4">
      <div class="w-full md:w-[75%]">
        <form class="flex items-center justify-start">
          <label for="simple-search" class="sr-only">Search</label>
          <div class="relative w-[70%]">
            <div class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
              <svg aria-hidden="true" class="w-5 h-5 text-gray-500" fill="currentColor" viewbox="0 0 20 20"
                   xmlns="http://www.w3.org/2000/svg">
                <path fill-rule="evenodd"
                      d="M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z"
                      clip-rule="evenodd"/>
              </svg>
            </div>
            <input v-model="searchKeyword" type="text" id="simple-search"
                   class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full pl-10 p-2"
                   placeholder="Search" @input="search">
          </div>
          <button v-if="props.enableAdvanceSearch" @click="searchData" type="button"
                  class="inline-block rounded px-5 pb-2 pt-2.5 text-xs font-medium uppercase leading-normal text-white shadow-[0_4px_9px_-4px_#3b71ca] transition duration-150 ease-in-out hover:bg-blue-600 hover:shadow-[0_8px_9px_-4px_rgba(59,113,202,0.3),0_4px_18px_0_rgba(59,113,202,0.2)] focus:bg-blue-600 focus:shadow-[0_8px_9px_-4px_rgba(59,113,202,0.3),0_4px_18px_0_rgba(59,113,202,0.2)] focus:outline-none focus:ring-0 active:bg-blue-700 active:shadow-[0_8px_9px_-4px_rgba(59,113,202,0.3),0_4px_18px_0_rgba(59,113,202,0.2)] bg-blue-500 w-max ml-3">
            <span>Advanced Search</span>
          </button>
        </form>
      </div>
      <div v-if="props.enableNewButton"
           class="w-full md:w-auto flex flex-col md:flex-row space-y-2 md:space-y-0 items-stretch md:items-center justify-end md:space-x-3 flex-shrink-0">
        <button @click="newData" type="button"
                class="inline-block rounded px-5 pb-2 pt-2.5 text-xs font-medium uppercase leading-normal text-white shadow-[0_4px_9px_-4px_#3b71ca] transition duration-150 ease-in-out hover:bg-blue-600 hover:shadow-[0_8px_9px_-4px_rgba(59,113,202,0.3),0_4px_18px_0_rgba(59,113,202,0.2)] focus:bg-blue-600 focus:shadow-[0_8px_9px_-4px_rgba(59,113,202,0.3),0_4px_18px_0_rgba(59,113,202,0.2)] focus:outline-none focus:ring-0 active:bg-blue-700 active:shadow-[0_8px_9px_-4px_rgba(59,113,202,0.3),0_4px_18px_0_rgba(59,113,202,0.2)] bg-blue-500">
          <Icon :name="buttonNew.icon" width="22" height="22"/>
          {{ buttonNew.buttonContent }}
        </button>
      </div>
    </div>
    <div class="overflow-x-auto">
      <table class="w-full text-sm text-left text-gray-500">
        <caption style="display: none;">Table data</caption>
        <thead class="text-xs text-gray-700 uppercase bg-gray-50">
        <tr>
          <th v-for="header in table.headers" scope="col" class="px-4 py-3" :key="header.name">
            <div class="flex items-center justify-center">
              <span class="w-max hover:cursor-default" v-text="header.name"></span>
              <button ref="buttonEl" :disabled="table.source.length === 0"
                      @click="sortTable(header.dataPropertyName, $event)">
                <svg class="w-3 h-3 ml-1.5" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="currentColor"
                     viewBox="0 0 24 24">
                  <path
                      d="M8.574 11.024h6.852a2.075 2.075 0 0 0 1.847-1.086 1.9 1.9 0 0 0-.11-1.986L13.736 2.9a2.122 2.122 0 0 0-3.472 0L6.837 7.952a1.9 1.9 0 0 0-.11 1.986 2.074 2.074 0 0 0 1.847 1.086Zm6.852 1.952H8.574a2.072 2.072 0 0 0-1.847 1.087 1.9 1.9 0 0 0 .11 1.985l3.426 5.05a2.123 2.123 0 0 0 3.472 0l3.427-5.05a1.9 1.9 0 0 0 .11-1.985 2.074 2.074 0 0 0-1.846-1.087Z"/>
                </svg>
              </button>
            </div>
          </th>
          <th v-if="!hideDelete" scope="col" class="px-4 text-center w-[10rem]">
            Actions
          </th>
        </tr>
        </thead>
        <tbody v-if="table.source.length !== 0">
        <tr v-for="data in table.source" class="border-b">
          <td v-for="header in table.headers" class="px-4 py-3 w-max text-center">
            <template v-if="urlReg.test(data[header.dataPropertyName])">
              <a class="underline text-blue-600 block w-full min-w-max" :href="data[header.dataPropertyName]"
                 v-text="data[header.dataPropertyName]"></a>
            </template>
            <template v-else-if="header.dataPropertyName === 'id' && enableIdLink">
              <a class="underline text-blue-600 hover:cursor-pointer block w-full min-w-max"
                 v-text="data[header.dataPropertyName]" @click="infoData(data)"></a>
            </template>
            <template v-else>
                <span class="hover:cursor-default w-max block text-center mx-auto"
                      v-text="data[header.dataPropertyName]"></span>
            </template>
          </td>
          <td v-if="!hideDelete" class="py-3 flex items-center justify-around">
            <button @click="deleteData(data)"
                    class="inline-block rounded px-3 pb-2 pt-2.5 text-xs font-medium uppercase leading-normal text-white shadow-[0_4px_9px_-4px_#3b71ca] transition duration-150 ease-in-out hover:bg-red-600 hover:shadow-[0_8px_9px_-4px_rgba(59,113,202,0.3),0_4px_18px_0_rgba(59,113,202,0.2)] focus:bg-red-600 focus:shadow-[0_8px_9px_-4px_rgb(202,72,59,0.3),0_4px_18px_0_rgb(202,59,59,0.2)] focus:outline-none focus:ring-0 active:bg-red-700 active:shadow-[0_8px_9px_-4px_rgba(59,113,202,0.3),0_4px_18px_0_rgba(59,113,202,0.2)] bg-red-500"
                    type="button" v-text="'Delete'">
            </button>
          </td>
        </tr>
        </tbody>
        <tbody v-else>
        <tr>
          <td :colspan="table.headers.length + 1" class="bg-gray-300 text-center">No data available</td>
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
          <li v-for="(value) in table.paginate" :key="JSON.stringify(value)">
            <button v-if="value.disabled"
                    class="flex items-center justify-center text-sm py-2 px-3 leading-tight text-gray-500 bg-gray-300 border border-gray-300"
                    v-text="value.page" disabled></button>
            <button v-else
                    class="flex items-center justify-center text-sm py-2 px-3 leading-tight text-gray-500 bg-white border border-gray-300 hover:bg-gray-100 hover:text-gray-700"
                    v-text="value.page"
                    @click="goToPage(Number.parseInt(value.page))"></button>
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
  <div v-if="displayConfirmDialog">
    <DeleteConfirm @close-dialog="listenCloseDialog" @confirm-dialog="deleteDataListener"/>
  </div>
</template>
