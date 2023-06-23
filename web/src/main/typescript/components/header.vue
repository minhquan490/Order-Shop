<script lang="ts">

export default {
  data() {
    const path: string = useRoute().path;
    const nav: NavBar = {
      adminUrl: '/admin',
      productUrl: '/admin/product',
      categoryUrl: '/admin/category',
      userUrl: '/admin/customer',
      orderUrl: '/admin/order',
      thridPartyUrl: '/admin/thrid-party',
      batchUrl: '/admin/batch',
      countryAddressUrl: '/admin/country-address',
      emailTemplateUrl: '/admin/email-template',
      triggerSettingUrl: '/admin/trigger',
      voucherListUrl: '/admin/voucher'
    };
    return {
      nav,
      path
    }
  }
}

type NavBar = {
  adminUrl: string,
  productUrl: string,
  categoryUrl: string,
  userUrl: string,
  orderUrl: string,
  thridPartyUrl: string,
  batchUrl: string,
  countryAddressUrl: string,
  emailTemplateUrl: string,
  triggerSettingUrl: string,
  voucherListUrl: string
};
</script>

<template>
  <div class="header flex items-center">
    <div class="flex items-center pl-4 pr-8">
      <img class="logo" src="/favicon.png" alt="Logo">
      <span class="hover:cursor-default text-white text-xl title">Bach Linh Order</span>
    </div>
    <div :class="nav.adminUrl === path ? 'border-l line' : 'border-l'">
      <a class="p-4 flex items-center justify-center hover:opacity-70" :href="nav.adminUrl">
        <Icon class="text-white" name="jam:dashboard-f" width="22" height="22" />
        <span class="hover:cursor-pointer text-white pl-1">Dashboard</span>
      </a>
    </div>
    <div :class="nav.productUrl === path ? 'border-l line' : 'border-l'">
      <a class="p-4 flex items-center justify-center hover:opacity-70" :href="nav.productUrl">
        <Icon class="text-white" name="icon-park-outline:ad-product" width="22" height="22" />
        <span class="hover:cursor-pointer text-white pl-1">Product list</span>
      </a>
    </div>
    <div :class="nav.categoryUrl === path ? 'border-l line' : 'border-l'">
      <a class="p-4 flex items-center justify-center hover:opacity-70" :href="nav.categoryUrl">
        <Icon class="text-white" name="ep:list" width="22" height="22" />
        <span class="hover:cursor-pointer text-white pl-1">Category list</span>
      </a>
    </div>
    <div :class="nav.userUrl === path ? 'border-l line' : 'border-l'">
      <a class="p-4 flex items-center justify-center hover:opacity-70" :href="nav.userUrl">
        <Icon class="text-white" name="heroicons:user-group-20-solid" width="22" height="22" />
        <span class="hover:cursor-pointer text-white pl-1">User list</span>
      </a>
    </div>
    <div :class="nav.orderUrl === path ? 'border-l line' : 'border-l'">
      <a class="p-4 flex items-center justify-center hover:opacity-70" :href="nav.orderUrl">
        <Icon class="text-white"
          name="streamline:interface-file-clipboard-text-edition-form-task-checklist-edit-clipboard" width="22"
          height="22" />
        <span class="hover:cursor-pointer text-white pl-1">Order list</span>
      </a>
    </div>
    <div :class="nav.thridPartyUrl === path ? 'border-l line' : 'border-l'">
      <a class="p-4 flex items-center justify-center hover:opacity-70" :href="nav.thridPartyUrl">
        <Icon class="text-white" name="mdi:web" width="22" height="22" />
        <span class="hover:cursor-pointer text-white pl-1">Order web</span>
      </a>
    </div>
  </div>
  <div v-if="nav.adminUrl === path" class="nav border-t">
    <div class="p-4">
      <a :href="nav.batchUrl" :class="nav.batchUrl === path ? 'line flex items-center leading-10 pl-2' : 'flex items-center leading-10 pl-2 btn'">
        <Icon class="text-white" name="material-symbols:auto-timer-outline" width="24" height="24" />
        <span class="hover:cursor-pointer text-white pl-2">Batch</span>
      </a>
    </div>
    <div class="p-4">
      <a :href="nav.countryAddressUrl" :class="nav.countryAddressUrl === path ? 'line flex items-center leading-10 pl-2' : 'flex items-center leading-10 pl-2 btn'">
        <Icon class="text-white" name="entypo:address" width="24" height="24" />
        <span class="hover:cursor-pointer text-white pl-2">Country Address</span>
      </a>
    </div>
    <div class="p-4">
      <a :href="nav.emailTemplateUrl" :class="nav.emailTemplateUrl === path ? 'line flex items-center leading-10 pl-2' : 'flex items-center leading-10 pl-2 btn'">
        <Icon class="text-white" name="mdi:email-plus-outline" width="24" height="24" />
        <span class="hover:cursor-pointer text-white pl-2">Email Template</span>
      </a>
    </div>
    <div class="p-4">
      <a :href="nav.triggerSettingUrl" :class="nav.triggerSettingUrl === path ? 'line flex items-center leading-10 pl-2' : 'flex items-center leading-10 pl-2 btn'">
        <Icon class="text-white" name="grommet-icons:trigger" width="24" height="24" />
        <span class="hover:cursor-pointer text-white pl-2">Trigger Setting</span>
      </a>
    </div>
    <div class="p-4">
      <a :href="nav.voucherListUrl" :class="nav.voucherListUrl === path ? 'line flex items-center leading-10 pl-2' : 'flex items-center leading-10 pl-2 btn'">
        <Icon class="text-white" name="mdi:voucher-outline" width="24" height="24" />
        <span class="hover:cursor-pointer text-white pl-2">Voucher List</span>
      </a>
    </div>
  </div>
</template>

<style lang="scss" scoped>
$header_bg: linear-gradient(90deg, rgba(0, 174, 207, 1) 0%, rgba(1, 167, 148, 1) 75%, rgba(0, 174, 207, 1) 100%);

.header {
  height: 4.75rem;
  background: $header_bg;
  position: fixed;
  width: 100%;
  z-index: 1000;

  & .logo {
    height: 4.75rem;
    width: 4.75rem;
  }

  & .title {
    font-family: 'Pacifico', cursive;
  }
}

.nav {
  position: fixed;
  top: 4.75rem;
  background: $header_bg;
  width: 20%;
  height: 100%;

  & .btn {
    position: relative;

    &::after {
      display: block;
      position: absolute;
      content: "";
      width: 0;
      height: 0;
      transition: .5s;
      bottom: 0;
      right: 0;
      border-bottom: 1px solid transparent;
    }

    &:hover {
      color: #fff;

      &::after {
        width: 100%;
        border-color: #fff;
      }
    }
  }
}

.line {
  position: relative;

  &::after {
    position: absolute;
    content: '';
    width: 90%;
    border-bottom-width: 1px;
    border-color: rgba(255, 255, 255, 0.726);
    display: block;
    bottom: 0;
    left: calc(5% - 1px);
  }
}
</style>
