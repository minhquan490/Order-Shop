<script lang="ts">

export default {
  setup() {
    const service = useForgotPasswordService();
    return {
      service
    }
  },
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
      this.service.sendEmail(this.email);
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
      this.service.sendEmail(this.email);
    },
    resetPassword() {
      this.passwordErrMsg = this.validatePassword(this.newPassword);
      this.confirmPasswordErrMsg = this.validateConfirmPassword(this.newPassword, this.confirmPassword);
      setTimeout(() => {
        this.passwordErrMsg = '';
        this.confirmPasswordErrMsg = '';
      }, 3000);
      if (this.passwordErrMsg.length === 0 && this.confirmPasswordErrMsg.length === 0) {
        const error = this.service.resetPassword(this.newPassword);
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
  <div class="forgot-password bg-blue-300">
    <div class="grid grid-cols-4">
      <div class="email-form col-start-2 col-end-4 rounded-lg grid grid-rows-4">
        <template v-if="token">
          <div class="flex items-center justify-between row-start-1 row-end-2 flex-col">
            <span class="hover:cursor-default text-2xl text-blue-500 pt-5">Reset your password</span>
          </div>
          <div class="row-span-2 row-end-4 px-4 flex items-center justify-center">
            <div class="form relative">
              <div>
                <span class="hover:cursor-default">Your new password</span>
                <input v-model="newPassword" type="password" class="bg-gray-200 rounded-md w-full outline-none px-2 leading-8" >
                <span v-text="passwordErrMsg" class="text-red-600 text-xs hover:cursor-default"></span>
              </div>
              <div class="pt-5">
                <span class="hover:cursor-default">Confirm your password</span>
                <input v-model="confirmPassword" type="password" class="bg-gray-200 rounded-md w-full outline-none px-2 leading-8" >
                <span v-text="confirmPasswordErrMsg" class="text-red-600 text-xs hover:cursor-default"></span>
              </div>
              <div class="pt-5">
                <button @click="$event => resetPassword()" class="w-full border bg-blue-700 text-white rounded-md leading-10 relative active:translate-y-1 hover:bg-violet-600 px-4">Reset my password</button>
              </div>
            </div>
          </div>
        </template>
        <template v-if="!token && !isSubmitted">
          <div class="flex items-center justify-between row-start-1 row-end-2 flex-col">
            <span class="hover:cursor-default text-2xl text-blue-500 pt-5">Forgot your password ?</span>
            <span class="hover:cursor-default text-gray-500">
              Don't worry, we will send you an email for reset for password
            </span>
          </div>
          <div class="row-span-2 row-end-4 px-4 flex items-center justify-center">
            <div class="form relative">
              <div>
                <span class="hover:cursor-default text-gray-500">Your email address</span>
                <input v-model="email" name="email" type="email"
                  class="bg-gray-200 rounded-md w-full outline-none px-2 leading-8" />
                <span v-text="errorMsg" class="text-red-600 text-xs hover:cursor-default"></span>
              </div>
              <div class="pt-5">
                <button @click="$event => submit()"
                  class="w-full bg-blue-700 text-white rounded-md leading-10 relative active:translate-y-1 hover:bg-violet-600">Continue</button>
              </div>
            </div>
          </div>
        </template>
        <template v-if="!token && isSubmitted">
          <div class="flex justify-center items-center flex-col row-start-1 row-end-5">
            <div class="flex justify-center items-center flex-col py-10">
              <div>
                <span class="hover:cursor-default">
                  We sent you reset password email, please open it and click to the clink.
                </span>
              </div>
              <div>
                <span class="hover:cursor-default">If you don't receive the email, click to Resent button</span>
              </div>
            </div>
            <div style="width: 20%;">
              <button @click="$event => resentEmail()"
                class="w-full bg-blue-700 text-white rounded-md leading-10 relative active:translate-y-1 hover:bg-violet-600">Resent
                email</button>
            </div>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.forgot-password {
    height: 100vh;
    & .email-form {
        margin-top: 6rem;
        height: 60vh;
        background-color: #fff;
        box-shadow: rgba(99, 99, 99, 0.2) 0px 2px 8px 0px;
        & .icon {
            font-size: 4rem;
        }
        & .form {
            width: 40%;
        }
    }
}
</style>
