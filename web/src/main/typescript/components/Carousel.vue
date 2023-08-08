<script lang="ts">
import {CarouselItem} from "~/types";
import {goNext, goPrevious} from "~/logic/components/carousel.logic";

export default {
  emits: ['item-selected'],
  props: {
    items: {
      type: Array<CarouselItem>,
      default: [],
      required: false
    }
  },
  data() {
    const currentIndex = 0;

    return {
      currentIndex
    }
  },
  methods: {
    previous() {
      goPrevious(this);
    },
    next() {
      goNext(this);
    },
    fireItemSelected(item: CarouselItem) {
      this.$emit('item-selected', item);
    }
  }
}
</script>

<template>
  <div id="controls-carousel" class="relative w-full">
    <div class="relative overflow-hidden rounded-lg md:h-96">
      <div v-for="item in items" :key="item"
           :class="item.current ? 'duration-700 ease-in-out' : 'hidden duration-700 ease-in-out'"
           @click="fireItemSelected(item)">
        <img :src="item.pictureUrl"
             class="absolute block w-full -translate-x-1/2 -translate-y-1/2 top-1/2 left-1/2" alt="...">
      </div>
    </div>
    <button type="button"
            class="absolute top-0 left-0 z-30 flex items-center justify-center h-full px-4 cursor-pointer group focus:outline-none"
            @click="previous">
        <span
            class="inline-flex items-center justify-center w-10 h-10 rounded-full bg-white/30 dark:bg-gray-800/30 group-hover:bg-white/50 dark:group-hover:bg-gray-800/60 group-focus:ring-4 group-focus:ring-white dark:group-focus:ring-gray-800/70 group-focus:outline-none">
            <svg class="w-4 h-4 text-white dark:text-gray-800" aria-hidden="true" xmlns="http://www.w3.org/2000/svg"
                 fill="none" viewBox="0 0 6 10">
                <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M5 1 1 5l4 4"/>
            </svg>
            <span class="sr-only">Previous</span>
        </span>
    </button>
    <button type="button"
            class="absolute top-0 right-0 z-30 flex items-center justify-center h-full px-4 cursor-pointer group focus:outline-none"
            @click="next">
        <span
            class="inline-flex items-center justify-center w-10 h-10 rounded-full bg-white/30 dark:bg-gray-800/30 group-hover:bg-white/50 dark:group-hover:bg-gray-800/60 group-focus:ring-4 group-focus:ring-white dark:group-focus:ring-gray-800/70 group-focus:outline-none">
            <svg class="w-4 h-4 text-white dark:text-gray-800" aria-hidden="true" xmlns="http://www.w3.org/2000/svg"
                 fill="none" viewBox="0 0 6 10">
                <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="m1 9 4-4-4-4"/>
            </svg>
            <span class="sr-only">Next</span>
        </span>
    </button>
  </div>
</template>
