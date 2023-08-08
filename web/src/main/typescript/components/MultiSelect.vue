<script lang="ts">
export default {
  emits: ['select-option', 'remove-option'],
  methods: {
    toggleSelectList() {
      this.listHide = !this.listHide;
    },
    selectOption(option: Record<string, string>) {
      if (this.selectedOptions.indexOf(option) >= 0) {
        return;
      }
      this.selectedOptions.push(option);
      this.$emit('select-option', this.selectedOptions);
    },
    removeOption(option: Record<string, string>) {
      this.selectedOptions = this.selectedOptions.filter(opt => opt != option);
      this.$emit('remove-option', this.selectedOptions);
    }
  },
  data() {
    const listHide = true;
    const selectedOptions: Array<Record<string, string>> = this.initSelectedOption;
    return {
      listHide,
      selectedOptions
    }
  },
  props: {
    options: {
      type: Array<Record<string, string>>,
      required: false,
      default: []
    },
    renderedKey: {
      type: String,
      required: false,
      default: ''
    },
    label: {
      type: String,
      required: false,
      default: ''
    },
    initSelectedOption: {
      type: Array<Record<string, string>>,
      required: false,
      default: []
    }
  }
}
</script>

<template>
  <div class="relative">
    <div @click="toggleSelectList"
         tabindex="0"
         id="underline_select"
         class="block py-2.5 px-0 w-full text-sm text-gray-500 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-gray-200 focus:cursor-text hover:cursor-text peer z-10 relative">
      <h1>&nbsp;</h1>
    </div>
    <div
        :class="listHide ? 'select-box max-h-[5rem] overflow-scroll hidden' : 'select-box max-h-[5rem] overflow-scroll block absolute w-[96%]'">
      <div @click="selectOption(option)"
           v-for="option in options"
           :key="option"
           class="py-1 px-3 text-sm hover:bg-gray-300 hover:cursor-pointer flex items-center justify-between w-full border rounded">
        <span v-text="option[renderedKey]"></span>
        <Icon v-if="selectedOptions.indexOf(option) >= 0"
              name="typcn:tick" width="16" height="16"
              color="rgb(34 197 94)"/>
      </div>
    </div>
    <label v-if="selectedOptions.length === 0" for="underline_select"
           class="peer-focus:font-medium peer-focus:hidden absolute text-sm text-gray-500 duration-300 transform top-3 origin-[0] peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0">
      {{ label }}
    </label>
    <div class="absolute top-3 w-full overflow-hidden z-20">
      <span v-for="option in selectedOptions"
            :key="option"
            class="inline-flex items-center rounded-md bg-gray-50 text-xs font-medium text-gray-600 ring-1 ring-inset ring-gray-500/10">
        <span class="pl-2 border-r-2 border-gray-300 pr-2 hover:cursor-default py-1"
              v-text="option[renderedKey]"></span>
        <span class="pl-1 hover:cursor-pointer pr-2 py-1 hover:bg-gray-200 rounded-r-md"
              @click="removeOption(option)">x</span>
      </span>
    </div>
  </div>
</template>

<style scoped></style>
