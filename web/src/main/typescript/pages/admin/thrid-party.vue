<script lang="ts" setup></script>

<template>
  <div class="thrid-party">
    <Nav />
    <iframe class="w-full frame" src="http://my.orderhangquangchau.com/login"></iframe>
  </div>
</template>

<style lang="scss" scoped>
.thrid-party {
  scrollbar-width: none;
  -ms-overflow-style: none;
  &::-webkit-scrollbar {
    display: none;
  }
  & .frame {
    height: calc(100vh - 4.75rem);
  }
}
</style>
