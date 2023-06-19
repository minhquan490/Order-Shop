<script lang="ts">
import { InputActionCallback } from '~/types/input-store.type';

export default {
  props: {
    minLength: Number,
    maxLength: Number,
    name: String,
    applyRegex: RegExp,
    type: String,
    id: String,
    classes: Array<string>,
    inputCallback: InputActionCallback,
    callValidate: Boolean
  },
  data() {
    const inputMinLength = (this.minLength) ? this.minLength : 4;
    const inputMaxLength = (this.maxLength) ? this.maxLength : 32;
    const inputName = (this.name) ? this.name : 'Input';
    const inputType = (this.type) ? this.type : 'text';
    const inputClasses = (this.classes) ? toRaw(this.classes).toLocaleString().replaceAll(',', ' ').concat(' w-full') : 'w-full';

    const errorMsg: string = '';

    const validateAsNumber = (input: string, target: HTMLInputElement): boolean => {
      if (Number.isNaN(input)) {
        this.errorMsg = `${this.inputName} must be a number`;
        this.resetErrMsg();
        return false;
      }

      if (input.length === 0) {
        this.errorMsg = `${this.inputName} is required.`;
        this.resetErrMsg();
        return false;
      }

      if (Number(input) < 0) {
        this.errorMsg = `${this.inputName} must be positive.`;
        this.resetErrMsg();
        return false;
      }
      return true;
    };

    const validateAsText = (input: string): boolean => {
      if (input.length === 0) {
        this.errorMsg = `${inputName} is required`;
        this.resetErrMsg();
        return false;
      }
      if (input.length < inputMinLength || input.length > inputMaxLength) {
        this.errorMsg = `${inputName} must be in range ${inputMinLength} - ${inputMaxLength}`;
        this.resetErrMsg();
        return false;
      }
      if (this.applyRegex) {
        const result = this.applyRegex.test(input);
        if (!result) {
          this.errorMsg = `Your ${inputName.toLocaleLowerCase()} is invalid`;
          this.resetErrMsg();
          return false;
        }
      }
      return true;
    }

    return {
      errorMsg,
      validateAsNumber,
      validateAsText,
      inputMaxLength,
      inputMinLength,
      inputName,
      inputType,
      inputClasses
    }
  },
  methods: {
    setValue(value: any) {
      if (this.inputCallback) {
        this.inputCallback.dispatchAction(value);
      }
    },
    bindValue(event?: Event) {
      let target: HTMLInputElement;
      if (!event) {
        target = this.$refs['input-value'] as HTMLInputElement;
      } else {
        event.preventDefault();
        target = event.target as HTMLInputElement
      }
      const value = target.value;
      const type = target.getAttribute('type') as string;

      switch (type) {
        case "number":
          const numResult = this.validateAsNumber(value, target);
          if (numResult) {
            this.setValue(value);
          }
          if (this.callValidate) {
            this.$emit("input-result", { [this.inputName]: numResult });
          }
          break;
        default:
          const textResult = this.validateAsText(value);
          if (textResult) {
            this.setValue(value);
          } else {
          }
          if (this.callValidate) {
            this.$emit("input-result", { [this.inputName]: textResult });
          }
          break;
      }
    },
    resetErrMsg() {
      setTimeout(() => {
        this.errorMsg = '';
      }, 3000);
    }
  },
  beforeUpdate() {
    if (this.callValidate) {
      this.bindValue();
      const input = this.$refs['input-value'] as HTMLInputElement;
      input.value = '';
    }
  }
}
</script>

<template>
  <div class="relative">
    <span class="hover:cursor-default pb-1 pl-1" v-text="inputName"></span>
    <input @blur="$event => bindValue($event)" :id="id" :minlength="inputMinLength" :maxlength="inputMaxLength"
      :type="inputType" :class="inputClasses" :ref="'input-value'" />
    <small v-text="errorMsg" class="text-red-600 absolute -bottom-[1.3rem] right-0 w-max"></small>
  </div>
</template>
