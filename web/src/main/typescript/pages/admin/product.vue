<template>
  <ClientOnly>
    <NuxtPage/>
  </ClientOnly>
</template>

<script lang="ts">
import { checkAdminPermission } from "~/logic/permission.logic";

export default {
  async beforeCreate() {
    if (process.client) {
      await checkAdminPermission();
    }
  }
}
</script>