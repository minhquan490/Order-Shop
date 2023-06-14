<script lang="ts">
import { Subscription, from, map, mergeMap } from 'rxjs';
import { HttpServiceProvider } from '~/services/http.service';
import { TableHeaders } from '~/types/table-header.type';

export default {
  inject: ['httpClient'],
  data() {
    if (process.server) {
      return {};
    }
    const pageData = new PageData();
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
    const subscription = from([`${serverUrl}/admin/table/customer`, `${serverUrl}/admin/orders/new-in-date`])
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
              isId: true,
              isImg: false
            },
            {
              name: 'Name',
              dataPropertyName: 'name',
              isId: false,
              isImg: false
            },
            {
              name: 'Username',
              dataPropertyName: 'username',
              isId: false,
              isImg: false
            },
            {
              name: 'Phone',
              dataPropertyName: 'phone',
              isId: false,
              isImg: false
            },
            {
              name: 'Email',
              dataPropertyName: 'emai',
              isId: false,
              isImg: false
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
              isId: true,
              isImg: false
            },
            {
              name: 'Deposited',
              dataPropertyName: 'deposited',
              isId: false,
              isImg: false
            },
            {
              name: 'Total Deposit',
              dataPropertyName: 'totalDeposit',
              isId: false,
              isImg: false
            },
            {
              name: 'Time Order',
              dataPropertyName: 'timeOrder',
              isId: false,
              isImg: false
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
    this.subscriptions?.push(subscription);
  },
  beforeUnmount() {
    if (this.subscriptions) {
      this.subscriptions.forEach(sub => sub.unsubscribe());
    }
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
    <div class="pt-24 px-8 grid grid-cols-5 gap-4">
      <div class="col-start-2 col-span-4">
        <DataTable tableTittle="Customer basic information" tableIconName="majesticons:user-box-line" :headers="pageData?.tableUserHeaders" :datas="pageData?.tableUserData" :height="'50vh'" :currentPage="pageData?.tableUserCurrentPage" />
      </div>
      <div class="col-start-2 col-span-4">
        <DataTable tableTittle="User order" tableIconName="icon-park-outline:transaction-order" :headers="pageData?.tableOrderHeaders" :datas="pageData?.tableOrerData" :height="'50vh'" />
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
$page_bg: #f4f6f9;

.admin {
  background-color: $page_bg;
  padding-bottom: 1.5rem;
}
</style>
