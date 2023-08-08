<script lang="ts" setup>
import AdminFooter from "~/components/AdminFooter.vue";

const props = defineProps({
  minHeight: {
    type: Number,
    default: 78,
    required: false
  }
})
</script>

<template>
  <div>
    <main :style="`--minHeight: ${props.minHeight}vh;`" class="main">
      <div class="mx-auto max-w-7xl py-6 sm:px-6 lg:px-8">
        <slot/>
      </div>
    </main>
    <admin-footer/>
  </div>
</template>

<style scoped>
.main {
  background-color: #e5e7eb;
  min-height: var(--minHeight);
}
</style>
