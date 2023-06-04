<script lang="ts">
import { ForgotPasswordService } from '~/services/forgot-password.service';

export default {
  inject: ['forgotPasswordService'],
  data() {
    const queryParam = useRoute().query;
    const token = queryParam['token'];
    return {
      token,
      email: '',
      errorMsg: '',
      emailPattern: /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/,
      isSubmitted: false,
      newPassword: '',
      confirmPassword: '',
      confirmPasswordErrMsg: '',
      passwordErrMsg: ''
    }
  },
  methods: {
    submit() {
      this.errorMsg = this.validateEmail();
      setTimeout(() => { this.errorMsg = '' }, 3000);
      if (this.errorMsg.length !== 0) {
        return;
      }
      const service = this.forgotPasswordService as ForgotPasswordService;
      service.requestRequestPasswordEmail(this.email);
      this.email = '';
      this.isSubmitted = true;
    },
    validateEmail() {
      if (this.email.length === 0) {
        return 'Email is required';
      }
      if (!this.emailPattern.test(this.email)) {
        return 'Email not valid';
      }
      if (this.email.length > 64 || this.email.length < 8) {
        return 'Length of email in range 8 - 64';
      }
      return '';
    },
    resentEmail() {
      const service = this.forgotPasswordService as ForgotPasswordService;
      service.requestRequestPasswordEmail(this.email);
    },
    resetPassword() {
      this.isSubmitted = true;
      this.passwordErrMsg = this.validatePassword(this.newPassword);
      this.confirmPasswordErrMsg = this.validateConfirmPassword(this.newPassword, this.confirmPassword);
      setTimeout(() => {
        this.passwordErrMsg = '';
        this.confirmPasswordErrMsg = '';
      }, 3000);
      if (this.passwordErrMsg.length === 0 && this.confirmPasswordErrMsg.length === 0) {
        const service = this.forgotPasswordService as ForgotPasswordService;
        let error;
        if (Array.isArray(this.token)) {
          const t = this.token[0] === null ? '' : this.token[0];
          error = service.resetPassword(t, this.newPassword);
        } else {
          error = service.resetPassword(this.token === null ? '' : this.token, this.newPassword);
        }
        if (!error) {
          window.location.href = '/login';
        } else {
          this.newPassword = '';
          this.confirmPassword = '';
        }
      }
    },
    validateConfirmPassword(password: string, confirmPassword: string) {
      if (confirmPassword.length === 0) {
        return 'Confirm password must not be empty';
      }
      if (password !== confirmPassword) {
        return 'Password not match together';
      }
      return '';
    },
    validatePassword(password: string) {
      if (password.length === 0) {
        return 'Password must not be empty';
      }
      if (password.length < 6 || password.length > 32) {
        return "Password must be in range 6 - 32";
      }
      return '';
    }
  }
}
</script>

<template>
  <div class="grid grid-rows-4 p-4 forgot-password">
    <div class="row-span-1 flex items-center justify-center flex-col">
      <span class="hover:cursor-default text-blue-500 text-2xl">Forgot your password ?</span>
      <span class="hover:cursor-default text-gray-500 text-base pt-8">
        We will send you an email for reset for password
      </span>
    </div>
    <div class="row-span-2 px-4 form rounded-lg grid grid-rows-4">
      <template v-if="!token && !isSubmitted">
        <div class="row-span-1 flex items-end justify-center">
          <Icon name="mdi:email-fast-outline" width="80" height="80" class="text-green-400" />
        </div>
        <div class="row-span-2 px-4 flex flex-col justify-center items-center relative">
          <div class="flex flex-col">
            <span class="hover:cursor-default pb-1">Your email address</span>
            <input v-model="email" type="email" class="bg-gray-200 rounded-md outline-none leading-8 px-4">
            <span v-text="errorMsg" class="text-red-600 text-xs hover:cursor-default"></span>
          </div>
          <div class="custom-bottom custom-width pt-6">
            <button @click="submit"
              class="w-full bg-blue-700 text-white rounded-lg leading-10 focus:bg-violet-700 focus:rounded-lg">Continue</button>
          </div>
        </div>
        <div class="row-span-1"></div>
      </template>
      <template v-if="!token && isSubmitted">
        <div class="row-span-4 flex items-center justify-center flex-col">
          <div class="flex justify-center items-center pb-10">
            <div class="flex justify-center items-center flex-col">
              <span class="hover:cursor-default block">
                We sent you reset password email.
              </span>
              <span class="hover:cursor-default block">If you don't receive an email.</span>
              <span class="hover:cursor-default block">Click to Resent button.</span>
            </div>
          </div>
          <div class="w-full">
            <button @click="resentEmail"
              class="w-full bg-blue-700 text-white rounded-md leading-10 relative active:translate-y-1 hover:bg-violet-600">
              Resent email
            </button>
          </div>
        </div>
      </template>
      <template v-if="token">
        <div class="flex items-center justify-center">
          <Icon class="text-blue-500" name="gg:password" width="70" height="70" />
        </div>
        <div class="row-span-2 row-end-4 px-4 flex items-center justify-center flex-col">
          <div>
            <span class="hover:cursor-default">Your new password</span>
            <input v-model="newPassword" type="password" class="bg-gray-200 rounded-md w-full outline-none px-2 leading-10">
            <span v-text="passwordErrMsg" class="text-red-600 text-xs hover:cursor-default"></span>
          </div>
          <div class="pt-5">
            <span class="hover:cursor-default">Confirm your password</span>
            <input v-model="confirmPassword" type="password" class="bg-gray-200 rounded-md w-full outline-none px-2 leading-10">
            <span v-text="confirmPasswordErrMsg" class="text-red-600 text-xs hover:cursor-default"></span>
          </div>
          <div class="pt-5 w-full">
            <button @click="resetPassword" class="w-full border bg-blue-700 text-white rounded-md leading-10 relative active:translate-y-1 hover:bg-violet-600 px-4">
              Reset my password
            </button>
          </div>
        </div>
      </template>
    </div>
    <div class="row-span-1"></div>
  </div>
</template>

<style scoped src="~/assets/styles/mobile/forgot-password.scss"></style>
