<script lang="ts" setup>
const props = defineProps({
  title: {
    type: String,
    required: false,
    default: ''
  },
  breadCrumbsEnable: {
    type: Boolean,
    required: false,
    default: false
  }
})
</script>

<template>
  <header class="bg-white shadow">
    <div
        :class="props.breadCrumbsEnable ? 'pb-4 mx-auto max-w-7xl px-2 sm:px-4 lg:px-6' : 'py-4 mx-auto max-w-7xl px-2 sm:px-4 lg:px-6'">
      <Breadcrumbs v-if="props.breadCrumbsEnable"/>
      <h1 class="text-lg font-bold tracking-tight text-gray-900" v-text="props.title"></h1>
    </div>
  </header>
</template>

<style scoped></style>
