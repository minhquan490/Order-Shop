<script lang="ts">
import { map, of, Subscription } from 'rxjs';
import { Customer } from '~/types/customer.type';
import { ErrorResponse } from '~/types/error-response.type';
import { TableHeaders } from '~/types/table-header.type';
import { TableActionCallback } from '~/types/table-store.type';

export default {
  setup() {
    const customerStore = useCustomerStore();
    const tableCallback: TableActionCallback = new TableActionCallback(customerStore, "selectedCustomer");
    const serverUrl = useAppConfig().serverUrl;
    const clientProvider = useHttpClient().value;
    const navigator = useNavigation();
    return {
      tableCallback,
      clientProvider,
      navigator,
      serverUrl
    }
  },
  data() {
    const pageData: PageData = new PageData();
    const customerListUrl = `${this.serverUrl}/admin/customer/list`;
    return {
      pageData,
      customerListUrl
    };
  },
  methods: {
    navigateToCreatePage() {
      this.navigator.navigate('/admin/customer/create');
    }
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
                {
                  isId: false,
                  name: 'Account non expired',
                  isImg: false,
                  dataPropertyName: 'is_account_non_expired'
                },
                {
                  isId: false,
                  name: 'Non locked',
                  isImg: false,
                  dataPropertyName: 'is_account_non_locked'
                },
                {
                  isId: false,
                  name: 'Credentials non expired',
                  isImg: false,
                  dataPropertyName: 'is_credentials_non_expired'
                }
              ];
              return {resp, tableHeaders}
            })
        )
        .subscribe(value => {
          if (value.resp.isError) {
            this.pageData.fetchUserError = (value.resp.getResponse as ErrorResponse).messages;
            setTimeout(() => {
              this.pageData.fetchUserError = [];
            }, 3000);
          } else if (value.resp.getResponse === null) {
            this.pageData.fetchUserError = ['Check your network and try again'];
          } else {
            this.pageData.tableData = value.resp.getResponse as Customer[];
            this.pageData.fetchUserError = [];
          }
          this.pageData.tableHeaders = value.tableHeaders;
        });
    this.pageData.subscriptions.push(subscription);
  },
  unmounted() {
    this.pageData.tableData = [];
    this.pageData.fetchUserError = [];
    this.pageData.subscriptions.forEach(sub => sub.unsubscribe());
  }
}

class PageData {
  tableData: Array<Customer> = [];
  subscriptions: Array<Subscription> = [];
  fetchUserError: Array<string> = [];
  tableHeaders: Array<TableHeaders> = [];
}
</script>

<template>
  <div class="user">
    <div class="pt-[5rem] px-8">
      <div class="pb-4">
        <Breadcrumb/>
      </div>
      <div class="grid grid-cols-12">
        <div class="col-start-1 col-span-12 relative">
          <DataTable table-icon-name="solar:user-id-linear" 
                     table-tittle="Customer information list"
                     :setter="tableCallback" 
                     :datas="pageData.tableData" 
                     :headers="pageData.tableHeaders"/>
          <ClientOnly>
            <div class="absolute top-[10px] right-4 z-10 flex flex-row-reverse">
              <div>
                <ButtonBasic :click-func="navigateToCreatePage" :name="'Create customer'"/>
              </div>
              <div class="pr-3">
                <ButtonBasic :bg-color="'rgb(59 130 246)'" :name="'Update customer'"/>
              </div>
              <div class="pr-3">
                <ButtonBasic :bg-color="'rgb(239 68 68)'" :name="'Delete customer'"/>
              </div>
            </div>
          </ClientOnly>
        </div>
      </div>
    </div>
    <div class="fixed top-20 right-0">
      <AlertDanger v-if="pageData.fetchUserError.length !== 0" :contents="pageData.fetchUserError"/>
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