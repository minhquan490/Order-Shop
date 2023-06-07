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
  <div class="grid grid-row-4 p-8 bg-blue-500 backdrop:blur-sm register">
    <div class="row-span-1 flex items-center justify-between flex-col">
      <h1 class="text-gray-600 text-2xl font-medium">Create your account</h1>
      <img class="logo" src="/favicon.png" alt="logo">
    </div>
    <div class="row-span-3 grid h-max gap-5">
      <div class="grid grid-cols-2 gap-4 h-max">
        <div class="col-span-1">
          <label for="first-name" class="text-gray-600">First name</label>
          <input v-model="form.firstName" class="w-full outline-none pl-2 text-sm leading-10 rounded-lg focus:border-green-400 border border-blue-100 bg-gray-200" type="text" name="first-name" id="first-name" max="32">
          <span v-text="formError.firstNameError" class="text-red-600 text-xs hover:cursor-default"></span>
        </div>
        <div class="col-span-1">
          <label for="last-name" class="text-gray-600">Last name</label>
          <input v-model="form.lastName" class="w-full outline-none pl-2 text-sm leading-10 rounded-lg focus:border-green-400 border border-blue-100 bg-gray-200" type="text" name="last-name" id="last-name" max="32">
          <span v-text="formError.lastNameError" class="text-red-600 text-xs hover:cursor-default"></span>
        </div>
      </div>
      <div class="h-max">
        <label for="email" class="text-gray-600">Email</label>
        <input v-model="form.email" class="w-full outline-none pl-2 text-sm leading-10 rounded-lg focus:border-green-400 border border-blue-100 bg-gray-200" type="email" name="email" id="email" max="32">
        <span v-text="formError.emailError" class="text-red-600 text-xs hover:cursor-default"></span>
      </div>
      <div class="h-max">
        <label for="username" class="text-gray-600">Username</label>
        <input v-model="form.username" class="w-full outline-none pl-2 text-sm leading-10 rounded-lg focus:border-green-400 border border-blue-100 bg-gray-200" type="text" name="username" id="username" max="32">
        <span v-text="formError.usernameError" class="text-red-600 text-xs hover:cursor-default"></span>
      </div>
      <div class="h-max">
        <label for="password" class="text-gray-600">Password</label>
        <input v-model="form.password" class="w-full outline-none pl-2 text-sm leading-10 rounded-lg focus:border-green-400 border border-blue-100 bg-gray-200" type="password" name="password" id="password" max="32">
        <span v-text="formError.passwordError" class="text-red-600 text-xs hover:cursor-default"></span>
      </div>
      <div class="h-max">
        <label for="confirm-password" class="text-gray-600">Confirm password</label>
        <input v-model="form.confirmPassword" class="w-full outline-none pl-2 text-sm leading-10 rounded-lg focus:border-green-400 border border-blue-100 bg-gray-200" type="password" name="confirm-password" id="confirm-password" max="32">
        <span v-text="formError.confirmPasswordError" class="text-red-600 text-xs hover:cursor-default"></span>
      </div>
      <div class="flex items-center justify-end h-max">
        <button @click="submit" class="bg-green-700 text-white py-2 px-6 rounded-lg active:translate-y-1">Sign up</button>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.register {
  height: 100vh;
  background-color:#fff;
  & .logo {
    height: 10rem;
    width: 10rem;
  }
}
</style>
