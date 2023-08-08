<script lang="ts">
import {
errorTitle,
register,
validateConfirmPassword,
validateEmail,
validateInputData,
validatePassword
} from "~/logic/pages/register.logic";

export default {
  data() {
    const registerErrorMsg: string[] = [];
    const hide = true;
    const registerData = {
      email: '',
      password: '',
      confirmPassword: '',
      emailError: '',
      passwordError: '',
      confirmPasswordError: ''
    }
    const registerApi: string = `${useAppConfig().serverUrl}/register`;
    const isLoading = false;
    return {
      registerErrorMsg,
      hide,
      registerData,
      registerApi,
      isLoading
    }
  },
  methods: {
    validateEmail() {
      const validated = validateEmail(this);
      if (!validated) {
        setTimeout((): void => {
          this.registerData.emailError = '';
        }, 4000);
      }
    },
    validatePassword() {
      const validated = validatePassword(this);
      if (!validated) {
        setTimeout((): void => {
          this.registerData.passwordError = '';
        }, 4000);
      }
    },
    validateConfirmPassword() {
      const validated = validateConfirmPassword(this);
      if (!validated) {
        setTimeout((): void => {
          this.registerData.confirmPasswordError = '';
        }, 4000);
      }
    },
    errorTitle() {
      return errorTitle
    },
    updateHidden(value: boolean) {
      this.hide = value;
    },
    submit(event: Event) {
      event.preventDefault();
      const validated = validateInputData(this);
      if (!validated) {
        setTimeout((): void => {
          this.registerData.emailError = '';
          this.registerData.confirmPasswordError = '';
          this.registerData.passwordError = '';
        }, 4000);
        return;
      }
      register(this, this.registerApi);
      return;
    }
  }
}
</script>

<template>
  <loading-block-u-i :loading="isLoading">
    <div class="relative">
      <section class="bg-gray-50">
        <div class="flex flex-col items-center justify-center mx-auto md:h-screen lg:py-0">
          <a href="/home" class="flex items-center text-2xl font-semibold text-gray-900">
            <img class="w-[7rem] h-[7rem] mr-2" src="/favicon.png" alt="logo">
            Bach Linh
          </a>
          <div
              class="w-full bg-white rounded-lg shadow dark:border md:mt-0 sm:max-w-md xl:p-0">
            <div class="p-6 space-y-4 md:space-y-6 sm:p-8">
              <h1 class="text-xl font-bold leading-tight tracking-tight text-gray-900 md:text-2xl text-center">
                Create and account
              </h1>
              <form class="space-y-4 md:space-y-6" action="#">
                <div>
                  <label for="email" class="block mb-2 text-sm font-medium text-gray-900">Your
                    email</label>
                  <input type="email"
                         name="email"
                         id="email"
                         class="bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-blue-600 focus:border-blue-600 block w-full p-2.5"
                         placeholder="name@company.com"
                         v-model="registerData.email"
                         @blur="validateEmail">
                  <p class="mt-2 text-sm text-red-600" v-text="registerData.emailError"></p>
                </div>
                <div>
                  <label for="password"
                         class="block mb-2 text-sm font-medium text-gray-900">Password</label>
                  <input type="password"
                         name="password"
                         id="password"
                         placeholder="••••••••"
                         class="bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-blue-600 focus:border-blue-600 block w-full p-2.5"
                         v-model="registerData.password"
                         @blur="validatePassword">
                  <p class="mt-2 text-sm text-red-600" v-text="registerData.passwordError"></p>
                </div>
                <div>
                  <label for="confirm-password" class="block mb-2 text-sm font-medium text-gray-900">Confirm
                    password</label>
                  <input type="password"
                         name="confirm-password"
                         id="confirm-password"
                         placeholder="••••••••"
                         class="bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-blue-600 focus:border-blue-600 block w-full p-2.5"
                         v-model="registerData.confirmPassword"
                         @blur="validateConfirmPassword">
                  <p class="mt-2 text-sm text-red-600" v-text="registerData.confirmPasswordError"></p>
                </div>
                <div class="flex items-start">
                  <div class="flex items-center h-5">
                    <input id="terms"
                           aria-describedby="terms"
                           type="checkbox"
                           class="w-4 h-4 border border-gray-300 rounded bg-gray-50 focus:ring-3 focus:ring-blue-300">
                  </div>
                  <div class="ml-3 text-sm">
                    <label for="terms" class="font-light text-gray-700">I accept the <a
                        class="font-medium text-blue-600 hover:underline" href="/terms">Terms and
                      Conditions</a></label>
                  </div>
                </div>
                <button type="submit"
                        class="w-full text-white bg-blue-600 hover:bg-blue-700 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center"
                        @click="submit">
                  Create an account
                </button>
                <p class="text-sm font-light text-gray-700">
                  Already have an account? <a href="/login"
                                              class="font-medium text-blue-600 hover:underline">Login
                  here</a>
                </p>
              </form>
            </div>
          </div>
        </div>
      </section>
      <div class="absolute w-[30%] top-6 right-0">
        <ClientOnly>
          <AlertListError :error-messages="registerErrorMsg"
                          :title="errorTitle()"
                          v-model:hidden="hide"
                          @hidden="updateHidden"/>
        </ClientOnly>
      </div>
    </div>
  </loading-block-u-i>
</template>