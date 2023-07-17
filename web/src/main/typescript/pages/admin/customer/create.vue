<script lang="ts">
import { ErrorMessage, Field, Form } from 'vee-validate';
import { Customer } from '~/types/customer.type';

export default {
  components: {
    Form,
    Field,
    ErrorMessage
  },
  setup() {
    const customerStore = useCustomerStore();
    const provider = useHttpClient().value;
    const serverUrl = useAppConfig().serverUrl;
    return {
      customerStore,
      provider,
      serverUrl
    }
  },
  data() {
    const initState = (): Customer => {
      return {
        id: "",
        first_name: "",
        last_name: "",
        phone: "",
        email: "",
        gender: "",
        role: "",
        username: "",
        address: [],
        is_activated: false,
        is_account_non_expired: false,
        is_account_non_locked: false,
        is_credentials_non_expired: false,
        is_enabled: false,
        picture: "",
      };
    };
    const initReq = (): CustomerCreateReq => {
      return {
        wrappedCustomer: initState(),
        password: '',
        rePassword: ''
      }
    }
    const pageData: PageData = {
      genderOptions: [
        {
          text: '--- Customer gender ---',
          value: '',
          selected: true,
          disabled: true
        },
        {
          text: 'Male',
          value: 'male',
          selected: false,
          disabled: false
        },
        {
          text: 'Female',
          value: 'female',
          selected: false,
          disabled: false
        }
      ],
      roleOptions: [
        {
          text: '--- Customer role ---',
          value: '',
          selected: true,
          disabled: true
        },
        {
          text: 'Admin',
          value: 'ADMIN',
          selected: false,
          disabled: false
        },
        {
          text: 'Customer',
          value: 'CUSTOMER',
          selected: false,
          disabled: false
        }
      ],
      provinceOptions: [
        {
          text: '--- Province ---',
          value: '',
          selected: true,
          disabled: true
        }
      ],
      districtOptions: [
        {
          text: '--- District ---',
          value: '',
          selected: true,
          disabled: true
        }
      ],
      wardOptions: [
        {
          text: '--- Ward ---',
          value: '',
          selected: true,
          disabled: true
        }
      ],
      customer: initReq()
    };
    return {
      pageData
    }
  },
  beforeMount() {
    this.customerStore.$reset();
  }
}

type PageData = {
  genderOptions: Array<Option>,
  roleOptions: Array<Option>,
  provinceOptions: Array<Option>,
  districtOptions: Array<Option>,
  wardOptions: Array<Option>,
  customer: CustomerCreateReq
}

type Option = {
  text: string,
  value: string,
  selected: boolean,
  disabled: boolean
}

type CustomerCreateReq = {
  wrappedCustomer: Customer,
  password: string,
  rePassword: string
}
</script>

<template>
  <div class="pt-[5rem] px-8">
    <div class="pb-4">
      <Breadcrumb />
    </div>
    <div class="grid grid-cols-5">
      <div class="col-start-2 col-span-3 px-6 py-3 rounded-md form">
        <div class="pb-10 flex items-center justify-center font-semibold text-xl">
          <span class="hover:cursor-default">
            Create new customer
          </span>
        </div>
        <Form class="grid grid-cols-3 gap-3 gap-y-6">
          <div class="col-span-1">
            <div class="relative">
              <span class="hover:cursor-default pb-1 pl-1">First name</span>
              <Field v-model="pageData.customer.wrappedCustomer.first_name" name="firstName" type="text"
                class="outline-none px-3 border border-gray-400 rounded-lg leading-7 w-full" />
              <ErrorMessage name="firstName"
                class="text-red-600 absolute -bottom-[1.3rem] right-0 w-max text-sm visible" />
            </div>
          </div>
          <div class="col-span-1">
            <div class="relative">
              <span class="hover:cursor-default pb-1 pl-1">Last name</span>
              <Field v-model="pageData.customer.wrappedCustomer.last_name" name="lastName" type="text"
                class="outline-none px-3 border border-gray-400 rounded-lg leading-7 w-full" />
              <ErrorMessage name="lastName"
                class="text-red-600 absolute -bottom-[1.3rem] right-0 w-max text-sm visible" />
            </div>
          </div>
          <div class="col-span-1">
            <div class="relative">
              <span class="hover:cursor-default pb-1 pl-1">Email</span>
              <Field v-model="pageData.customer.wrappedCustomer.email" name="email" type="email"
                class="outline-none px-3 border border-gray-400 rounded-lg leading-7 w-full" />
              <ErrorMessage name="email" class="text-red-600 absolute -bottom-[1.3rem] right-0 w-max text-sm visible" />
            </div>
          </div>
          <div class="col-span-1">
            <div class="relative">
              <span class="hover:cursor-default pb-1 pl-1">Phone</span>
              <Field v-model="pageData.customer.wrappedCustomer.phone" name="phone" type="tel"
                class="outline-none px-3 border border-gray-400 rounded-lg leading-7 w-full" />
              <ErrorMessage name="phone" class="text-red-600 absolute -bottom-[1.3rem] right-0 w-max text-sm visible" />
            </div>
          </div>
          <div class="col-span-1">
            <div class="relative">
              <span class="hover:cursor-default pb-1 pl-1">Gender</span>
              <select
                class="outline-none px-3 border border-gray-400 rounded-lg py-[0.23rem] w-full hover:cursor-pointer">
                <option v-for="gender in pageData.genderOptions" v-bind:value="gender.value" v-text="gender.text"
                  :selected="gender.selected" :disabled="gender.disabled"></option>
              </select>
            </div>
          </div>
          <div class="col-span-1">
            <div class="relative">
              <span class="hover:cursor-default pb-1 pl-1">Role</span>
              <select
                class="outline-none px-3 border border-gray-400 rounded-lg py-[0.23rem] w-full hover:cursor-pointer">
                <option v-for="role in pageData.roleOptions" v-bind:value="role.value" v-text="role.text"
                  :selected="role.selected" :disabled="role.disabled">
                </option>
              </select>
            </div>
          </div>
          <div class="col-span-1">
            <div class="relative">
              <span class="hover:cursor-default pb-1 pl-1">Username</span>
              <Field v-model="pageData.customer.wrappedCustomer.username" name="username" type="text"
                class="outline-none px-3 border border-gray-400 rounded-lg leading-7 w-full" />
              <ErrorMessage name="username"
                class="text-red-600 absolute -bottom-[1.3rem] right-0 w-max text-sm visible" />
            </div>
          </div>
          <div class="col-span-1">
            <div class="relative">
              <span class="hover:cursor-default pb-1 pl-1">Password</span>
              <Field v-model="pageData.customer.password" name="password" type="text"
                class="outline-none px-3 border border-gray-400 rounded-lg leading-7 w-full" />
              <ErrorMessage name="password"
                class="text-red-600 absolute -bottom-[1.3rem] right-0 w-max text-sm visible" />
            </div>
          </div>
          <div class="col-span-1">
            <div class="relative">
              <span class="hover:cursor-default pb-1 pl-1">Re input password</span>
              <Field v-model="pageData.customer.rePassword" name="rePassword" type="text"
                class="outline-none px-3 border border-gray-400 rounded-lg leading-7 w-full" />
              <ErrorMessage name="rePassword"
                class="text-red-600 absolute -bottom-[1.3rem] right-0 w-max text-sm visible" />
            </div>
          </div>
          <div class="col-span-1">
            <div class="relative">
              <span class="hover:cursor-default pb-1 pl-1">Province</span>
              <select
                class="outline-none px-3 border border-gray-400 rounded-lg py-[0.23rem] w-full hover:cursor-pointer">
                <option v-for="province in pageData.provinceOptions" v-bind:value="province.value" v-text="province.text"
                  :selected="province.selected" :disabled="province.disabled"></option>
              </select>
            </div>
          </div>
          <div class="col-span-1">
            <div class="relative">
              <span class="hover:cursor-default pb-1 pl-1">District</span>
              <select
                class="outline-none px-3 border border-gray-400 rounded-lg py-[0.23rem] w-full hover:cursor-pointer">
                <option v-for="district in pageData.districtOptions" v-bind:value="district.value" v-text="district.text"
                  :selected="district.selected" :disabled="district.disabled"></option>
              </select>
            </div>
          </div>
          <div class="col-span-1">
            <div class="relative">
              <span class="hover:cursor-default pb-1 pl-1">Ward</span>
              <select
                class="outline-none px-3 border border-gray-400 rounded-lg py-[0.23rem] w-full hover:cursor-pointer">
                <option v-for="ward in pageData.wardOptions" v-bind:value="ward.value" v-text="ward.text"
                  :selected="ward.selected" :disabled="ward.disabled"></option>
              </select>
            </div>
          </div>
        </Form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.form {
  box-shadow: rgba(14, 30, 37, 0.12) 0 2px 4px 0, rgba(14, 30, 37, 0.32) 0 2px 16px 0;
}
</style>
