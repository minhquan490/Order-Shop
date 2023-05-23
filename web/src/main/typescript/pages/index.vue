<script lang="ts" setup>
const route = useRoute();
useServerSeoMeta({
  title: 'Home',
  description: 'Home page of application',
  ogTitle: 'Home',
  ogDescription: 'Home page of application',
  ogUrl: route.path
})
</script>

<template>
  <div>
    Page: foo
  </div>
</template>

<style scoped></style>
