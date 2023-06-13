<script lang="ts">
export default {
  data() {
    const queries = useRoute().query;
    let idQuery = queries['id'];
    if (Array.isArray(idQuery)) {
      idQuery = idQuery[0];
    }
    return {
      idQuery
    }
  }
}
</script>

<template>
  <div>
    <span v-text="idQuery"></span>
  </div>
</template>

<style scoped></style>
