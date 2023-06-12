<script lang="ts">
import { HttpServiceProvider } from '~/services/http.service';
import { TableHeaders } from '~/types/table-header.type';

export default {
  inject: ['httpClient'],
  data() {
    const tableHeaers: Array<TableHeaders> = [
      {
        name: 'ID',
        dataPropertyName: 'id'
      },
      {
        name: 'Name',
        dataPropertyName: 'name'
      },
      {
        name: 'Username',
        dataPropertyName: 'username'
      },
      {
        name: 'Phone',
        dataPropertyName: 'phone'
      },
      {
        name: 'Email',
        dataPropertyName: 'emai'
      }
    ];
    const httpServiceProvider: HttpServiceProvider = this.httpClient as HttpServiceProvider;
    const serverUrl = useAppConfig().serverUrl;
    const service = httpServiceProvider.open(`${serverUrl}/admin/table/customer`);
    let datas: TableCustomerInfo[] = service.get<undefined, TableCustomerInfo[]>();
    if (Object.keys(datas).length === 0) {
      datas = [];
    }
    let currentPage
    const storage: Storage = useAppStorage().value;
    currentPage = storage.getItem(useAppConfig().customerDataTableCurrentPage);
    return {
      tableHeaers,
      datas,
      currentPage
    }
  }
}

type TableCustomerInfo = {
  id: string,
  name: string,
  username: string,
  phone: string,
  email: string
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
