<script lang="ts">
import { from, map, mergeMap } from 'rxjs';
import { HttpServiceProvider } from '~/services/http.service';
import { TableHeaders } from '~/types/table-header.type';

export default {
  inject: ['httpClient'],
  data() {
    if (process.server) {
      return {};
    }
    const pageData = new PageData();
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
    this.subscription = from([`${serverUrl}/admin/table/customer`, `${serverUrl}/admin/orders/new-in-date`])
      .pipe(
        map(url => httpServiceProvider.open(url)),
        mergeMap(async service => service.get<undefined, any>()),
        map(resp => {
          if (Object.keys(resp).length === 0) {
            return [];
          }
          return resp;
        }),
        map(array => {
          const pageData = new PageData();
          const resultCondition = Array.isArray(array) && (array as Array<any>).length !== 0;
          if (resultCondition) {
            const firstEle = (array as Array<any>)[0];
            if (firstEle instanceof TableCustomerInfo) {
              pageData.tableUserData = array;
            }
            if (firstEle instanceof TableOrderInDate) {
              pageData.tableOrerData = array;
            }
          } else {
            pageData.tableUserData = [];
            pageData.tableOrerData = [];
          }
          return pageData;
        }),
        map(page => {
          const storage: Storage = useAppStorage().value;
          page.tableUserCurrentPage = storage.getItem(useAppConfig().customerDataTableCurrentPage);
          return page;
        }),
        map(page => {
          const tableHeaers: Array<TableHeaders> = [
            {
              name: 'ID',
              dataPropertyName: 'id',
              isId: true
            },
            {
              name: 'Name',
              dataPropertyName: 'name',
              isId: false
            },
            {
              name: 'Username',
              dataPropertyName: 'username',
              isId: false
            },
            {
              name: 'Phone',
              dataPropertyName: 'phone',
              isId: false
            },
            {
              name: 'Email',
              dataPropertyName: 'emai',
              isId: false
            }
          ];
          page.tableUserHeaders = tableHeaers;
          return page;
        }),
        map(page => {
          const tableHeaers: Array<TableHeaders> = [
            {
              name: 'ID',
              dataPropertyName: 'id',
              isId: true
            },
            {
              name: 'Deposited',
              dataPropertyName: 'deposited',
              isId: false
            },
            {
              name: 'Total Deposit',
              dataPropertyName: 'totalDeposit',
              isId: false
            },
            {
              name: 'Time Order',
              dataPropertyName: 'timeOrder',
              isId: false
            }
          ];
          page.tableOrderHeaders = tableHeaers;
          return page;
        })
      )
      .subscribe(data => {
        if (this.pageData) {
          this.pageData.tableUserCurrentPage = data.tableUserCurrentPage;
          this.pageData.tableUserData = data.tableUserData;
          this.pageData.tableUserHeaders = data.tableUserHeaders;
          this.pageData.tableOrderHeaders = data.tableOrderHeaders;
          this.pageData.tableOrerData = data.tableOrerData;
        }
      });
  },
  beforeUnmount() {
    this.subscription.unsubscribe();
  }
}

class PageData {
  tableUserHeaders!: TableHeaders[];
  tableUserData!: TableCustomerInfo[];
  tableUserCurrentPage!: string | null;
  tableOrderHeaders!: TableHeaders[];
  tableOrerData!: TableOrderInDate[];
}

class TableCustomerInfo {
  id!: string;
  name!: string;
  username!: string;
  phone!: string;
  email!: string;
}

class TableOrderInDate {
  id!: string;
  deposited!: string;
  totalDeposit!: string;
  timeOrder!: string;
}
</script>

<template>
  <div class="admin">
    <Header />
    <div class="pt-4 px-8 grid grid-cols-5 gap-4">
      <div class="col-start-2 col-span-4 rounded-xl table">
        <div>
          <div class="p-4">
            <Icon name="mdi:user" width="28" height="28" />
            <span class="hover:cursor-default p-1">User detail</span>
          </div>
        </div>
        <div class="rounded-b-xl overflow-hidden border-t border-gray-400">
          <DataTable :headers="pageData?.tableUserHeaders" :datas="pageData?.tableUserData" :height="'50vh'"
            :currentPage="pageData?.tableUserCurrentPage" />
        </div>
      </div>
      <div class="col-start-2 col-span-4 rounded-xl table">
        <div>
          <div class="p-4">
            <Icon name="icon-park-outline:transaction-order" width="28" height="28" />
            <span class="hover:cursor-default p-1">User order</span>
          </div>
        </div>
        <div class="rounded-b-xl overflow-hidden border-t border-gray-400">
          <DataTable :headers="pageData?.tableOrderHeaders" :datas="pageData?.tableOrerData" :height="'50vh'" />
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
$page_bg: #f4f6f9;

.admin {
  background-color: $page_bg;
  padding-bottom: 1.5rem;

  & .table {
    box-shadow: rgba(0, 0, 0, 0.35) 0px 5px 15px;
  }
}
</style>
