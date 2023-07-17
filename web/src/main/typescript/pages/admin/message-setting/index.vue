<script lang="ts">
import { map, of, Subscription } from 'rxjs';
import { ErrorResponse } from '~/types/error-response.type';
import { MessageSetting } from "~/types/message-setting";
import { TableHeaders } from "~/types/table-header.type";
import { TableActionCallback } from "~/types/table-store.type";

export default {
  setup() {
    const messageSettingStore = useMessageSettingStore();
    const tableCallback: TableActionCallback = new TableActionCallback(messageSettingStore, "selectedMessage");
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
    const messageSettingListUrl = `${this.serverUrl}/admin/message-setting/list`;
    return {
      pageData,
      messageSettingListUrl
    }
  },
  methods: {
    navigateToCreatePage() {
      this.navigator.navigate('/admin/message-setting/create');
    }
  },
  beforeMount() {
    const subcription = of(this.messageSettingListUrl)
      .pipe(
        map(url => this.clientProvider(url)),
        map(service => service.get<undefined, MessageSetting[]>()),
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
              name: 'Value',
              isImg: false,
              dataPropertyName: 'value'
            }
          ];
          return { tableHeaders, resp };
        })
      )
      .subscribe(value => {
        if (value.resp.isError) {
          this.pageData.fetchMessageSettingError = (value.resp.getResponse as ErrorResponse).messages;
          setTimeout(() => {
            this.pageData.fetchMessageSettingError = [];
          }, 3000);
        } else if (value.resp.getResponse == null) {
          this.pageData.fetchMessageSettingError = ['Check your network and try again'];
        } else {
          this.pageData.tableData = value.resp.getResponse as MessageSetting[]
          this.pageData.fetchMessageSettingError = [];
        }
        this.pageData.tableHeaders = value.tableHeaders;
      });
    this.pageData.subscriptions.push(subcription);
  },
  unmounted() {
    this.pageData.tableData = [];
    this.pageData.fetchMessageSettingError = [];
    this.pageData.subscriptions.forEach(sub => sub.unsubscribe());
  },
}

class PageData {
  tableData: Array<MessageSetting> = [];
  subscriptions: Array<Subscription> = [];
  fetchMessageSettingError: Array<String> = [];
  tableHeaders: Array<TableHeaders> = [];
}
</script>

<template>
  <div class="message-setting">
    <div class="pt-[5rem] px-8">
      <div class="pb-4">
        <Breadcrumb />
      </div>
      <div class="grid grid-cols-12">
        <div class="col-start-1 col-span-12 relative">
          <DataTable table-icon-name="carbon:message-queue" 
                     table-tittle="Message setting list"
                     :setter="tableCallback"
                     :datas="pageData.tableData"
                     :headers="pageData.tableHeaders" />
          <ClientOnly>
            <div class="absolute top-[10px] right-4 z-10 flex flex-row-reverse">
              <div>
                <ButtonBasic :click-func="navigateToCreatePage" :name="'Create message'"/>
              </div>
              <div class="pr-3">
                <ButtonBasic :bg-color="'rgb(59 130 246)'" :name="'Update message'"/>
              </div>
              <div class="pr-3">
                <ButtonBasic :bg-color="'rgb(239 68 68)'" :name="'Delete message'"/>
              </div>
            </div>
          </ClientOnly>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
$page_bg: #f4f6f9;

.message-setting {
  background-color: $page_bg;
  padding-bottom: 1.5rem;
  min-height: 100vh;
  position: relative;
}
</style>
