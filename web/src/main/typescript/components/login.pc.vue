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
  <div class="login grid grid-cols-6 relative">
    <div></div>
    <div class="col-span-4 main rounded-lg grid grid-cols-2 gap-1">
      <div class="flex items-center justify-center w-full">
        <img src="/login-img.webp" alt="Login image">
      </div>
      <div class="flex items-center justify-center w-full">
        <div class="form grid grid-cols-4">
          <div class="flex items-center justify-center col-span-3">
            <span class="font-semibold text-2xl opacity-80 pb-20 hover:cursor-default block">Member Login</span>
          </div>
          <div class="grid grid-cols-4 gap-y-3 col-span-4 detail">
            <div class="input w-full col-span-3">
              <Icon name="material-symbols:mail-rounded" />
              <input class="outline-none col-span-3" type="text" name="username" id="username" placeholder="Username"
                v-model="username">
              <div v-if="usernameErrorDetail.length !== 0" class="flex justify-end">
                <small v-text="usernameErrorDetail" class="text-red-600"></small>
              </div>
            </div>
            <div class="input col-span-3">
              <Icon name="solar:lock-password-bold" />
              <input class="outline-none col-span-3" type="password" name="password" id="password" placeholder="Password"
                autocomplete="off" v-model="password">
              <div v-if="passwordErrorDetail.length !== 0" class="flex justify-end">
                <small v-text="passwordErrorDetail" class="text-red-600"></small>
              </div>
            </div>
            <div class="col-span-3">
              <button class="w-full submit rounded-3xl hover:opacity-80 relative focus:translate-y-1"
                @click="submit($event)">Login</button>
            </div>
            <div class="col-span-3 flex justify-center text-sm">
              <span class="pr-1 hover:cursor-default opacity-70">Forgot</span>
              <NuxtLink class="opacity-80 hover:text-green-700" to="/forgot-password">Password ?</NuxtLink>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div></div>
    <div class="absolute top-10 problem w-full flex items-center justify-center">
      <span v-if="loginFailureDetail.length !== 0" v-text="`Can not login because: ${loginFailureDetail.join(', ')}`"
        class="hover:cursor-default"></span>
    </div>
  </div>
</template>

<style scoped src="~/assets/styles/pc/login.scss"></style>
