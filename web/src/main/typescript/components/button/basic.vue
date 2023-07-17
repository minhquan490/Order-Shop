<script lang="ts">
export default {
  props: {
    clickFunc: Function,
    name: String,
    textSize: String,
    color: String,
    bgColor: String
  },
  data() {
    const buttonTextContent = (this.name) ? this.name : "Button";
    const buttonTextSize = (this.textSize) ? this.textSize : "1rem";
    const buttonTextColor = (this.color) ? this.color : "white";
    const buttonBgColor = (this.bgColor) ? this.bgColor : "rgb(22 163 74)";
    return {
      buttonTextColor,
      buttonTextContent,
      buttonTextSize,
      buttonBgColor
    }
  },
  methods: {
    handleClick(event: MouseEvent) {
      if (this.clickFunc) {
        this.clickFunc(event);
      }
    }
  }
}
</script>

<template>
  <div>
    <button :style="`--bgColor: ${buttonBgColor};--textColor: ${buttonTextColor};--textSize: ${buttonTextSize}`" @click="handleClick($event)" class="button p-2 rounded-md hover:opacity-70 active:translate-y-1 relative">
      <span class="hover:cursor-pointer" v-text="buttonTextContent"></span>
    </button>
  </div>
</template>

<style lang="scss" scoped>
.button {
  width: 100%;
  height: 100%;
  background-color: var(--bgColor);
  color: var(--textColor);
  font-size: var(--textSize);
}
</style>
