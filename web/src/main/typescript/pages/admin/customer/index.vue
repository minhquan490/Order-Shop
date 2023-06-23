<script lang="ts">
import { Subscription, map, of } from 'rxjs';
import { Customer } from '~/types/customer.type';
import { ErrorResponse } from '~/types/error-response.type';
import { TableHeaders } from '~/types/table-header.type';
import { TableActionCallback } from '~/types/table-store.type';

export default {
  setup() {
    const customerStore = useCustomerStore();
    const tableCallback: TableActionCallback = new TableActionCallback(customerStore, "selectedCustomer");
    const serverUrl = useAppConfig().serverUrl;
    const customerListUrl = `${serverUrl}/admin/customer/list`;
    const clientProvider = useHttpClient().value;
    return {
      tableCallback,
      customerListUrl,
      clientProvider
    }
  },
  data() {
    const pageData: PageData = new PageData();
    return {
      pageData
    };
  },
  beforeMount() {
    const subscription = of(this.customerListUrl)
      .pipe(
        map(url => this.clientProvider(url)),
        map(service => service.get<undefined, Customer[]>()),
        map(resp => {
          const tableHeaders: Array<TableHeaders> = [
            {
              isId: true,
              name: 'Id',
              isImg: false,
              dataPropertyName: 'id'
            },
            {
              isId: false,
              name: 'Picture',
              isImg: true,
              dataPropertyName: 'picture'
            },
            {
              isId: false,
              name: 'First name',
              isImg: false,
              dataPropertyName: 'first_name'
            },
            {
              isId: false,
              name: 'Last name',
              isImg: false,
              dataPropertyName: 'last_name'
            },
            {
              isId: false,
              name: 'Phone',
              isImg: false,
              dataPropertyName: 'phone'
            },
            {
              isId: false,
              name: 'Email',
              isImg: false,
              dataPropertyName: 'email'
            },
            {
              isId: false,
              name: 'Gender',
              isImg: false,
              dataPropertyName: 'gender'
            },
            {
              isId: false,
              name: 'Role',
              isImg: false,
              dataPropertyName: 'role'
            },
            {
              isId: false,
              name: 'Username',
              isImg: false,
              dataPropertyName: 'username'
            },
            {
              isId: false,
              name: 'Addresses',
              isImg: false,
              dataPropertyName: 'address'
            },
            {
              isId: false,
              name: 'Activated',
              isImg: false,
              dataPropertyName: 'is_activated'
            },
            {
              isId: false,
              name: 'Enabled',
              isImg: false,
              dataPropertyName: 'is_enabled'
            },
            // {
            //   isId: false,
            //   name: 'Account non expired',
            //   isImg: false,
            //   dataPropertyName: 'is_account_non_expired'
            // },
            // {
            //   isId: false,
            //   name: 'Non locked',
            //   isImg: false,
            //   dataPropertyName: 'is_account_non_locked'
            // },
            // {
            //   isId: false,
            //   name: 'Credentials non expired',
            //   isImg: false,
            //   dataPropertyName: 'is_credentials_non_expired'
            // }
          ];
          
          return { resp, tableHeaders }
        })
      )
      .subscribe(value => {
        if (value.resp.isError) {
          this.pageData.fetchUserError = (value.resp.getResponse as ErrorResponse).messages;
        } else if (value.resp.getResponse === null) {
          this.pageData.fetchUserError = ['No data available'];
        } else {
          this.pageData.tableDatas = value.resp.getResponse as Customer[];
        }
        this.pageData.tableHeaders = value.tableHeaders;
      });
    this.pageData.subscriptions.push(subscription);
  },
  unmounted() {
    this.pageData.tableDatas = [];
    this.pageData.fetchUserError = [];
    this.pageData.subscriptions.forEach(sub => sub.unsubscribe());
  }
}

class PageData {
  tableDatas: Array<Customer> = [];
  subscriptions: Array<Subscription> = [];
  fetchUserError: Array<string> = [];
  tableHeaders: Array<TableHeaders> = [];
}
</script>

<template>
  <div class="user">
    <div class="pt-20 px-8">
      <div class="pb-2">
        <Breadcrumb />
      </div>
      <div class="grid grid-cols-12">
        <div class="col-start-1 col-span-12">
          <DataTable table-icon-name="solar:user-id-linear" table-tittle="Customer information list"
            :setter="tableCallback" :datas="pageData.tableDatas" :headers="pageData.tableHeaders" />
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
$page_bg: #f4f6f9;

.user {
  background-color: $page_bg;
  padding-bottom: 1.5rem;
  min-height: 100vh;
  position: relative;
}
</style>