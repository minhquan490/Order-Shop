<script lang="ts">
import { RegisterService } from '~/services/register.service';

export default {
  inject: ['registerService'],
  data() {
    const form: RegisterForm = {
      firstName: '',
      lastName: '',
      email: '',
      username: '',
      password: '',
      confirmPassword: ''
    };
    const formError: FormError = {
      usernameError: '',
      confirmPasswordError: '',
      emailError: '',
      firstNameError: '',
      lastNameError: '',
      passwordError: '',
    }
    return {
      form,
      formError
    }
  },
  methods: {
    submit() {
      const service = this.registerService as RegisterService;
      const result = service.validateForm(new Map(Object.entries(this.form)));
      let isError = false;
      result.forEach((value, key) => {
        if (value.length !== 0) {
          isError = true;
          //@ts-ignore
          this.formError[key] = value;
        }
      });
      if (!isError) {
        service.register(new Map(Object.entries(this.form)));
      }
      setTimeout(() => {
        this.formError = {
          usernameError: '',
          confirmPasswordError: '',
          emailError: '',
          firstNameError: '',
          lastNameError: '',
          passwordError: '',
        };
      }, 3000);
    }
  }
}

type RegisterForm = {
  firstName: string,
  lastName: string,
  email: string,
  username: string,
  password: string,
  confirmPassword: string
}

type FormError = {
  firstNameError: string,
  lastNameError: string,
  emailError: string,
  usernameError: string,
  passwordError: string,
  confirmPasswordError: string
}
</script>

<template>
  <div class="grid grid-cols-6 p-8 register-pc relative">
    <div class="col-span-2 flex items-center justify-center">
      <img src="/register-7424066_640.png" alt="register img">
    </div>
    <div class="col-span-4 bg-transparent rounded-lg form p-8">
      <div>
        <div>
          <h1 class="text-3xl text-green-700">Sign up</h1>
        </div>
        <div class="pt-4">
          <span class="hover:cursor-default opacity-60">Have you an account already?</span>
          <a class="pl-2 text-green-600" href="/login">Log in</a>
        </div>
      </div>
      <div class="grid grid-cols-2 gap-16 pt-16">
        <div class="row-span-1">
          <div class="flex flex-col">
            <label for="first-name" class="text-gray-700">First name</label>
            <input v-model="form.firstName"
              class="bg-transparent border border-gray-700 rounded leading-8 outline-none px-4 focus:border-green-500"
              type="text" name="first-name" id="first-name" max="32">
            <span v-text="formError.firstNameError" class="text-red-600 text-xs hover:cursor-default"></span>
          </div>
          <div class="flex flex-col pt-4">
            <label for="username" class="text-gray-700">Username</label>
            <input v-model="form.username"
              class="bg-transparent border border-gray-700 rounded leading-8 outline-none px-4 focus:border-green-500"
              type="text" name="username" id="username" max="10">
            <span v-text="formError.usernameError" class="text-red-600 text-xs hover:cursor-default"></span>
          </div>
          <div class="flex flex-col pt-4">
            <label for="password" class="text-gray-700">Password</label>
            <input v-model="form.password"
              class="bg-transparent border border-gray-700 rounded leading-8 outline-none px-4 focus:border-green-500"
              type="password" name="password" id="password" max="32">
            <span v-text="formError.passwordError" class="text-red-600 text-xs hover:cursor-default"></span>
          </div>
        </div>
        <div class="row-span-1">
          <div class="flex flex-col">
            <label for="last-name" class="text-gray-700">Last name</label>
            <input v-model="form.lastName"
              class="bg-transparent border border-gray-700 rounded leading-8 outline-none px-4 focus:border-green-500"
              type="text" name="last-name" id="last-name" max="32">
            <span v-text="formError.lastNameError" class="text-red-600 text-xs hover:cursor-default"></span>
          </div>
          <div class="flex flex-col pt-4">
            <label for="email" class="text-gray-700">Email</label>
            <input v-model="form.email"
              class="bg-transparent border border-gray-700 rounded leading-8 outline-none px-4 focus:border-green-500"
              type="email" name="email" id="email" max="32">
            <span v-text="formError.emailError" class="text-red-600 text-xs hover:cursor-default"></span>
          </div>
          <div class="flex flex-col pt-4">
            <label for="confirm-password" class="text-gray-700">Confirm password</label>
            <input v-model="form.confirmPassword"
              class="bg-transparent border border-gray-700 rounded leading-8 outline-none px-4 focus:border-green-500"
              type="password" name="confirm-password" id="confirm-password" max="10">
            <span v-text="formError.confirmPasswordError" class="text-red-600 text-xs hover:cursor-default"></span>
          </div>
        </div>
      </div>
      <div class="pt-10 flex items-center justify-end">
        <button @click="submit"
          class="relative active:translate-y-1 rounded-md border bg-green-700 text-white hover:opacity-75 py-2 px-4">Sign
          up</button>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.register-pc {
  height: 100vh;
  background: #50C9C3;
  /* fallback for old browsers */
  background: -webkit-linear-gradient(to right, #96DEDA, #50C9C3);
  /* Chrome 10-25, Safari 5.1-6 */
  background: linear-gradient(to right, #96DEDA, #50C9C3);
  /* W3C, IE 10+/ Edge, Firefox 16+, Chrome 26+, Opera 12+, Safari 7+ */
  & .form {
    background-color: #fff;
    box-shadow: rgba(0, 0, 0, 0.16) 0px 3px 6px, rgba(0, 0, 0, 0.23) 0px 3px 6px;
  }
}
</style>