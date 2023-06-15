<script lang="ts">
import { LoginForm } from '~/types/login-form.type';

export default {
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
      const service = useAuth();
      event.preventDefault();
      const form: LoginForm = { username: this.username, password: this.password };
      const validateResult = service.validateFormLogin(form);
      if (validateResult.isValid) {
        const loginResult = service.login(form);
        if (!loginResult) {
          this.loginFailureDetail.push('Has problem when try to login');
        } else {
          if ("status" in loginResult) {
            this.loginFailureDetail = loginResult.messages;
          } else {
            service.storeAuth(loginResult);
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
            <div class="col-span-3 flex justify-end text-sm">
              <span class="pr-1 hover:cursor-default opacity-70">Forgot</span>
              <a href="/forgot-password" class="opacity-80 hover:text-green-700">Password ?</a>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div></div>
    <div class="absolute top-10 problem w-full flex items-center justify-center">
      <span v-if="loginFailureDetail.length !== 0" v-text="loginFailureDetail.join(', ')"
        class="hover:cursor-default text-red-500"></span>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.login {
  background: linear-gradient(90deg, rgba(33, 105, 158, 1) 0%, rgba(123, 123, 237, 1) 100%);
  height: 100vh;
  & .main {
    position: relative;
    height: 90vh;
    background-color: #fff;
    top: 5%;
  }
  & .form {
    & .detail {
      & .input {
        position: relative;
        & input {
          background-color: #e6e6e6;
          border-radius: 30px;
          line-height: 1.5;
          height: 3rem;
          padding: 0 30px 0 68px;
        }
        & svg {
          top: 0.82rem;
          left: 2rem;
          font-size: 1.2rem;
          opacity: 0.6;
          position: absolute;
        }
      }
      & .submit {
        background-color: #57b846;
        color: #fff;
        line-height: 1.5;
        height: 3rem;
      }
    }
  }
  & .problem {
    color: rgb(231, 35, 35);
  }
}</style>