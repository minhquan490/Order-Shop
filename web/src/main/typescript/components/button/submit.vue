<script lang="ts">
export default {
  props: {
    name: String,
    textSize: String,
    color: String,
    bgColor: String,
    disabled: Boolean
  },
  data() {
    const buttonTextContent = (this.name) ? this.name : "Button";
    const buttonTextSize = (this.textSize) ? this.textSize : "1rem";
    const buttonTextColor = (this.color) ? this.color : "white";
    const buttonBgColor = (this.bgColor) ? this.bgColor : "rgb(22 163 74)";
    const butonDisabled = (this.disabled) ? this.disabled.valueOf() : false;
    return {
      buttonTextColor,
      buttonTextContent,
      buttonTextSize,
      buttonBgColor,
      butonDisabled
    }
  }
}
</script>

<template>
  <div>
    <button type="submit" :style="`--bgColor: ${buttonBgColor};--textColor: ${buttonTextColor};--textSize: ${buttonTextSize}`" :class="butonDisabled ? 'button p-2 rounded-md' : 'button p-2 rounded-md hover:opacity-70 active:translate-y-1 relative'" :disabled="butonDisabled">
      <span :class="butonDisabled ? 'hover:cursor-default' : 'hover:cursor-pointer'" v-text="buttonTextContent"></span>
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
