<script lang="ts">
import {NavBarsSource} from "~/types";
import {navigationSources} from "~/logic/pages/admin/index.logic";
import {checkAdminPermission} from "~/logic/permission.logic";

export default {
  setup() {
    const navigation: NavBarsSource[] = navigationSources();

    return {
      navigation
    }
  },
  async beforeCreate() {
    if (process.client) {
      await checkAdminPermission();
    }
  }
}
</script>

<template>
  <ClientOnly>
    <div>
      <NavBarsAdmin :sources="navigation"/>
      <PageHeader title="Dashboard"/>
      <AdminContentArea :min-height="81.5">Content</AdminContentArea>
    </div>
  </ClientOnly>
</template>