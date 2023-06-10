<script lang="ts">
import { LoginService } from '~/services/login.service';

export default {
  inject: ['loginService'],
  data() {
    return {
      username: '',
      password: '',
      usernameErrorDetail: '',
      passwordErrorDetail: '',
      loginFailureDetail: [] as Array<string>
    }
  },
  methods: {
    submit(event: Event): void {
      const service = this.loginService as LoginService;
      event.preventDefault();
      const form = { username: this.username, password: this.password };
      const validateResult = service.validateLoginForm(form);
      if (validateResult.isValid) {
        const loginResult = service.login(form);
        if (!loginResult) {
          this.loginFailureDetail.push('Has problem when try to login');
        } else {
          if ("status" in loginResult) {
            this.loginFailureDetail = loginResult.messages;
          } else {
            service.storeAuthentication(loginResult);
          }
        }
        this.username = '';
        this.password = '';
      } else {
        this.usernameErrorDetail = validateResult.username as string;
        this.passwordErrorDetail = validateResult.password as string;
        setTimeout(() => {
          this.usernameErrorDetail = '';
          this.passwordErrorDetail = ''
        }, 5000);
      }
    },
  },
}
</script>

<template>
  <div class="grid grid-rows-2 login-mobile relative">
    <div class="row-span-1 grid grid-rows-6">
      <div class="row-start-1 row-end-5 flex justify-center items-center">
        <Icon class="text-green-500" name="codicon:shield" width="100" height="100" />
      </div>
      <div class="row-start-5 row-end-7 flex justify-center items-center flex-col">
        <div>
          <span class="font-mono text-xl font-semibold">Welcome !</span>
        </div>
        <div>
          <span class="text-gray-600">Taobao world in your hand</span>
        </div>
      </div>
    </div>
    <div class="row-span-1">
      <div>
        <div class="px-8 pt-0 pb-8">
          <div>
            <span class="hover:cursor-default pb-2 block">Your username</span>
            <input v-model="username" class="w-full bg-gray-200 pr-16 pl-4 leading-10 rounded-lg outline-green-600"
              type="text">
            <div v-if="usernameErrorDetail.length !== 0" class="flex justify-end">
              <small v-text="usernameErrorDetail" class="text-red-600"></small>
            </div>
          </div>
          <div class="pt-4">
            <span class="hover:cursor-default pb-2 block">Your password</span>
            <input v-model="password" class="w-full bg-gray-200 pr-16 pl-4 leading-10 rounded-lg outline-green-600"
              type="password" autocomplete="off">
            <div v-if="passwordErrorDetail.length !== 0" class="flex justify-end">
              <small v-text="passwordErrorDetail" class="text-red-600"></small>
            </div>
          </div>
          <div class="pt-8">
            <button @click="submit($event)" class="w-full bg-blue-500 leading-10 rounded-xl text-white">Login</button>
          </div>
          <div class="flex justify-end pt-2">
            <span class="hover:cursor-default pr-2">Forgot</span>
            <a class="text-green-600" href="/forgot-password">your password ?</a>
          </div>
          <div class="grid grid-cols-4 pt-4">
            <div
              class="col-span-1 border before:absolute before:right-1/2 before:content-[''] before:block before:w-20 border-green-700 border-dashed h-px relative top-1/2">
            </div>
            <div class="col-span-2 px-1">
              <a class="text-blue-500" href="/register">Register new account</a>
            </div>
            <div
              class="col-span-1 border before:absolute before:right-1/2 before:content-[''] before:block before:w-20 border-green-700 border-dashed h-px relative top-1/2">
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="absolute top-10 problem w-full flex items-center justify-center">
      <span v-if="loginFailureDetail.length !== 0" v-text="loginFailureDetail.join(', ')" class="hover:cursor-default text-red-500"></span>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.login-mobile {
    height: 100vh;
    background-color: #fff;
}
</style>
