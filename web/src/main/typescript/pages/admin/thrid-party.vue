<script lang="ts" setup></script>

<template>
  <div class="thrid-party">
    <Header />
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
    padding-top: 4.8rem;
    height: 100vh;
  }
}
</style>
