<script lang="ts">
export default {
  props: {
    errorMessages: {
      type: Array<string>,
      required: false,
      default: []
    },
    title: {
      type: String,
      required: false,
      default: ''
    },
    hide: {
      type: Boolean,
      required: false,
      default: false
    }
  },
  methods: {
    toggle(event: Event) {
      event.preventDefault();
      this.$emit('hidden', true);
    }
  }
}
</script>

<template>
  <div :class="this.hide.valueOf() ? 'relative px-3 invisible' : 'relative px-3'">
    <button type="button"
            class="ml-auto -mx-1.5 -my-1.5 bg-blue-50 text-blue-500 rounded-lg focus:ring-2 focus:ring-blue-400 p-1.5 hover:bg-blue-200 inline-flex items-center justify-center h-8 w-8 absolute right-6 top-2"
            data-dismiss-target="#alert-1"
            aria-label="Close"
            @click="toggle">
      <span class="sr-only">Close</span>
      <svg class="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 14">
        <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6"/>
      </svg>
    </button>
    <div class="flex p-4 mb-4 text-sm text-blue-800 rounded-lg bg-blue-50"
         role="alert">
      <svg class="flex-shrink-0 inline w-4 h-4 mr-3 mt-[2px]" aria-hidden="true" xmlns="http://www.w3.org/2000/svg"
           fill="currentColor" viewBox="0 0 20 20">
        <path
            d="M10 .5a9.5 9.5 0 1 0 9.5 9.5A9.51 9.51 0 0 0 10 .5ZM9.5 4a1.5 1.5 0 1 1 0 3 1.5 1.5 0 0 1 0-3ZM12 15H8a1 1 0 0 1 0-2h1v-3H8a1 1 0 0 1 0-2h2a1 1 0 0 1 1 1v4h1a1 1 0 0 1 0 2Z"/>
      </svg>
      <span class="sr-only">Info</span>
      <div>
        <span class="font-medium" v-text="this.title"></span>
        <ul class="mt-1.5 ml-4 list-disc list-inside">
          <li v-for="message in this.errorMessages" :key="message" v-text="message"></li>
        </ul>
      </div>
    </div>
  </div>
</template>
