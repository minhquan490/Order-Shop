<script lang="ts" setup>

import {TableHeader} from "~/types";

const props = defineProps({
  headers: {
    type: Array<TableHeader>,
    default: [],
    required: false
  },
  data: {
    type: Array<Record<string, any>>,
    default: [],
    required: false
  }
})

</script>

<template>
  <div class="relative overflow-x-auto rounded-md">
    <table class="w-full text-sm text-left text-gray-500">
      <caption style="display: none">Table data read only</caption>
      <thead class="text-xs text-gray-700 uppercase bg-gray-50">
      <tr>
        <th v-for="header in props.headers" :key="JSON.stringify(header)" v-text="header.name" scope="col"
            class="px-6 py-3 text-center"></th>
      </tr>
      </thead>
      <tbody v-if="props.data?.length !== 0">
      <tr class="bg-white border-b" v-for="data in props.data" :key="JSON.stringify(data)">
        <td v-for="header in props.headers"
            :key="data[header.dataPropertyName]"
            v-text="data[header.dataPropertyName]"
            class="px-6 py-4 text-center"></td>
      </tr>
      </tbody>
      <tbody v-else>
      <tr>
        <td :colspan="props.headers?.length" class="bg-gray-300 text-center py-4">
          No data available
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>
