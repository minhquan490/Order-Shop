<script lang="ts">
import {errorTitle, login, validateEmail, validateInput, validatePassword} from "~/logic/pages/login.logic";

export default {
  data() {
    const loginData = {
      email: '',
      password: '',
      emailError: '',
      passwordError: ''
    }
    const loginErrorMsg: Array<string> = [];
    const loginApi: string = `${useAppConfig().serverUrl}/login`;
    const hide = true;
    const isLoading = false;
    return {
      loginData,
      loginErrorMsg,
      loginApi,
      hide,
      isLoading
    }
  },
  methods: {
    errorTitle() {
      return errorTitle
    },
    validatePassword() {
      const result: boolean = validatePassword(this);
      if (!result) {
        setTimeout((): void => {
          this.loginData.passwordError = '';
        }, 4000)
      }
    },
    validateEmail() {
      const result = validateEmail(this);
      if (!result) {
        setTimeout((): void => {
          this.loginData.emailError = '';
        }, 4000)
      }
    },
    async submit(event: Event) {
      this.isLoading = true;
      event.preventDefault();
      const validated = validateInput(this);
      if (!validated) {
        setTimeout((): void => {
          this.loginData.emailError = '';
          this.loginData.passwordError = '';
        }, 4000);
        return;
      }
      await login(this, this.loginApi);
      return;
    },
    updateHidden(value: boolean) {
      this.hide = value;
    }
  }
}
</script>

<template>
  <loading-block-u-i :loading="isLoading">
    <div class="h-full relative">
      <div class="flex min-h-full flex-1 flex-col justify-center px-6 lg:px-8">
        <div class="sm:mx-auto sm:w-full sm:max-w-sm">
          <a href="/home">
            <img class="mx-auto h-[10rem] w-auto" src="/favicon.png"
                 alt="Order shop"/>
          </a>
          <h2 class="text-center text-2xl font-bold leading-9 tracking-tight text-gray-900">Sign in to your
            account</h2>
        </div>

        <div class="mt-10 sm:mx-auto sm:w-full sm:max-w-sm">
          <form class="space-y-6">
            <div>
              <label for="email" class="block text-sm font-medium leading-6 text-gray-900">Email address</label>
              <div class="mt-2">
                <input v-model="loginData.email"
                       name="email"
                       type="email"
                       autocomplete="email"
                       class="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                       @blur="validateEmail"/>
                <p v-text="loginData.emailError" class="mt-2 text-sm text-red-500 dark:text-red-800"></p>
              </div>
            </div>

            <div>
              <div class="flex items-center justify-between">
                <label for="password" class="block text-sm font-medium leading-6 text-gray-900">Password</label>
                <div class="text-sm">
                  <a href="/forgot-password" class="font-semibold text-indigo-600 hover:text-indigo-500">Forgot
                    password?</a>
                </div>
              </div>
              <div class="mt-2">
                <input v-model="loginData.password"
                       name="password"
                       type="password"
                       autocomplete="current-password"
                       class="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                       @blur="validatePassword"/>
                <p v-text="loginData.passwordError" class="mt-2 text-sm text-red-500 dark:text-red-800"></p>
              </div>
            </div>

            <div>
              <button type="submit"
                      class="flex w-full justify-center rounded-md bg-indigo-600 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
                      @click="submit">
                Sign in
              </button>
            </div>
          </form>

          <p class="mt-10 text-center text-sm text-gray-500">
            Not a member?
            {{ ' ' }}
            <a href="/register" class="font-semibold leading-6 text-indigo-600 hover:text-indigo-500">Register here</a>
          </p>
        </div>
      </div>
      <div class="absolute w-[30%] top-6 right-0">
        <ClientOnly>
          <AlertListError :error-messages="loginErrorMsg"
                          :title="errorTitle()"
                          v-model:hidden="hide"
                          @hidden="updateHidden"/>
        </ClientOnly>
      </div>
    </div>
  </loading-block-u-i>
</template>
