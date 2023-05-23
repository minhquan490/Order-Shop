<script lang="ts" setup>
const route = useRoute();
useServerSeoMeta({
  title: 'Forbidden',
  description: 'Forbidden page, display when you do not have permission to access page',
  ogTitle: 'Forbidden',
  ogDescription: 'Forbidden page, display when you do not have permission to access page',
  ogUrl: route.path
})
</script>

<template>
  <div>
    <div id="app">
      <div>403</div>
      <div class="txt">Forbidden<span class="blink">_</span></div>
    </div>
  </div>
</template>

<style lang="scss" scoped src="~/assets/403.scss"></style>