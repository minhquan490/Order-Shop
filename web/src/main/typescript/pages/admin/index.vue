<script lang="ts" setup></script>

<template>
  <div class="admin">
    <div class="header flex items-center">
      <div class="flex items-center pl-4 pr-16 border-r border-r-gray-300">
        <img class="logo" src="/favicon.png" alt="Logo">
        <span class="hover:cursor-default text-white text-xl title">Bach Linh Order</span>
      </div>
      <div class="link">
        <a class="p-4 flex items-center justify-center hover:opacity-70" href="/admin">
          <Icon class="text-white" name="jam:dashboard-f" width="22" height="22" />
          <span class="hover:cursor-pointer text-white pl-1">Dashboard</span>
        </a>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
$page_bg: #f7f7f7;
$header_bg: linear-gradient(90deg, rgba(0,174,207,1) 0%, rgba(1,167,148,1) 75%, rgba(0,174,207,1) 100%);

.admin {
  background-color: $page_bg;
  & .header {
    height: 4.75rem;
    background: $header_bg;
    & .logo {
      height: 4.75rem;
      width: 4.75rem;
    }
    & .title {
      font-family: 'Pacifico', cursive;
    }
  }
}
</style>
