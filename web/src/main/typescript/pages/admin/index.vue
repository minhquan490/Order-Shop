<script lang="ts">
import { TableHeaders } from '~/types/table-header.type';

export default {
  data() {
    const tableHeaers: Array<TableHeaders> = [
      {
        name: 'Name',
        dataPropertyName: 'name'
      },
      {
        name: 'Phone',
        dataPropertyName: 'phone'
      }
    ];
    const datas = [
      { name: '1', phone: 'Test Phone' },
      { name: '2', phone: 'Test Phone' },
      { name: '3', phone: 'Test Phone' },
      { name: '4', phone: 'Test Phone' },
      { name: '5', phone: 'Test Phone' },
      { name: '84', phone: 'Test Phone' },
      { name: '7', phone: 'Test Phone' },
      { name: '90', phone: 'Test Phone' },
      { name: '9', phone: 'Test Phone' },
      { name: '10', phone: 'Test Phone' },
      { name: '11', phone: 'Test Phone' },
      { name: '46', phone: 'Test Phone' },
      { name: '13', phone: 'Test Phone' },
      { name: '14', phone: 'Test Phone' },
      { name: '15', phone: 'Test Phone1' },
      { name: '16', phone: 'Test Phone' }
    ];
    let currentPage
    if (process.client) {
      const storage: Storage = useAppStorage().value;
      currentPage = storage.getItem(useAppConfig().customerDataTableCurrentPage);
    }
    return {
      tableHeaers,
      datas,
      currentPage
    }
  }
}
</script>

<template>
  <div class="admin">
    <Nav />
    <div class="pt-4 px-8 grid grid-cols-2 gap-4">
      <div class="col-span-1 rounded-xl table">
        <div>
          <div class="p-4">
            <Icon name="mdi:user" width="28" height="28" />
            <span class="hover:cursor-default p-1">User detail</span>
          </div>
        </div>
        <div class="rounded-b-xl overflow-hidden border-t border-gray-400">
          <DataTable :headers="tableHeaers" :datas="datas" :height="'50vh'" :currentPage="currentPage" />
        </div>
      </div>
      <div class="col-span-1"></div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
$page_bg: #f4f6f9;

.admin {
  background-color: $page_bg;
  height: 100vh;

  & .table {
    box-shadow: rgba(0, 0, 0, 0.35) 0px 5px 15px;
  }
}
</style>
