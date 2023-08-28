<script lang="ts">
import {NavBarsSource} from "~/types";
import {
  clearError,
  getDistrict,
  getWard,
  initPageData,
  navigationSources,
  PageData,
  submitData,
  validateInput
} from "~/logic/pages/admin/customer/create-new-customer.logic";

export default {
  setup() {
    const isLoading: boolean = false;
    const navigationSource: NavBarsSource[] = navigationSources();
    const hideListInfo: boolean = true;
    const hideListError: boolean = true;
    return {
      isLoading,
      navigationSource,
      hideListError,
      hideListInfo
    }
  },
  data() {
    const pageData: PageData = {
      districts: [],
      wards: [],
      provinces: [],
      roles: [],
      customerCreateData: {
        username: '',
        role: '',
        phone: '',
        password: '',
        last_name: '',
        gender: '',
        first_name: '',
        email: '',
        address: {
          district: '',
          ward: '',
          house_address: '',
          province: ''
        }
      },
      selectedData: {
        wardId: '',
        provinceId: '',
        districtId: '',
        houseAddress: ''
      },
      validationError: {
        emailError: '',
        genderError: '',
        firstNameError: '',
        lastNameError: '',
        passwordError: '',
        phoneError: '',
        roleError: '',
        usernameError: '',
        provinceError: '',
        districtError: '',
        houseAddressError: '',
        wardError: ''
      },
      submitDataSuccessMsg: '',
      submitDataFailureMsg: []
    };
    return {
      pageData
    }
  },
  beforeMount() {
    initPageData().then(value => this.pageData = value);
  },
  methods: {
    getDistrict(provinceId: string): void {
      this.isLoading = true;
      this.pageData.selectedData.wardId = '';
      this.pageData.selectedData.districtId = '';
      getDistrict(provinceId, this).then(value => this.pageData.districts = value);
    },
    getWard(districtId: string): void {
      this.isLoading = true;
      this.pageData.selectedData.wardId = '';
      getWard(districtId, this).then(value => this.pageData.wards = value);
    },
    submit(event: Event): void {
      event.preventDefault();
      const validationResult: boolean = validateInput(this.pageData);
      if (!validationResult) {
        setTimeout(() => this.pageData.validationError = clearError(this.pageData), 3000);
      } else {
        submitData(this);
      }
    },
    hideInfoAlert() {
      this.hideListInfo = true;
    },
    hideErrorAlert() {
      this.hideListError = true;
    }
  }
}
</script>

<template>
  <div class="relative">
    <loading-block-u-i :loading="isLoading">
      <nav-bars-admin :sources="navigationSource"/>
      <page-header title="Create new customer" :bread-crumbs-enable="true"/>
      <admin-content-area>
        <div class="mx-auto w-[65%]">
          <form class="bg-white rounded-lg grid grid-cols-2 gap-x-8 gap-y-4 p-8">
            <h3 class="col-span-2 font-bold text-center pb-3 text-xl">
              <icon name="tdesign:user-add" width="48" height="48"/>
            </h3>
            <div class="relative z-0 w-full mb-6 group col-span-1">
              <input type="text"
                     name="firstName"
                     id="firstName"
                     class="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                     placeholder=" "
                     v-model="pageData.customerCreateData.first_name"/>
              <label for="firstName"
                     class="peer-focus:font-medium absolute text-sm text-gray-500 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">
                First name
              </label>
              <p v-if="pageData.validationError.firstNameError.length !== 0" class="mt-2 text-sm text-red-600 relative">
                <span class="text-sm" v-text="pageData.validationError.firstNameError"></span>
              </p>
            </div>
            <div class="relative z-0 w-full mb-6 group col-span-1">
              <input type="text"
                     name="lastName"
                     id="lastName"
                     class="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                     placeholder=" "
                     v-model="pageData.customerCreateData.last_name"/>
              <label for="lastName"
                     class="peer-focus:font-medium absolute text-sm text-gray-500 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">
                Last name
              </label>
              <p v-if="pageData.validationError.lastNameError.length !== 0" class="mt-2 text-sm text-red-600 relative">
                <span class="text-sm" v-text="pageData.validationError.lastNameError"></span>
              </p>
            </div>
            <div class="relative z-0 w-full mb-6 group col-span-1">
              <input type="tel"
                     name="phone"
                     id="phone"
                     class="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                     placeholder=" "
                     v-model="pageData.customerCreateData.phone"/>
              <label for="phone"
                     class="peer-focus:font-medium absolute text-sm text-gray-500 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">
                Phone number
              </label>
              <p v-if="pageData.validationError.phoneError.length !== 0" class="mt-2 text-sm text-red-600 relative">
                <span class="text-sm" v-text="pageData.validationError.phoneError"></span>
              </p>
            </div>
            <div class="relative z-0 w-full mb-6 group col-span-1">
              <input type="email"
                     name="email"
                     id="email"
                     class="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                     placeholder=" "
                     v-model="pageData.customerCreateData.email"/>
              <label for="email"
                     class="peer-focus:font-medium absolute text-sm text-gray-500 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">
                Email address
              </label>
              <p v-if="pageData.validationError.emailError.length !== 0" class="mt-2 text-sm text-red-600 relative">
                <span class="text-sm" v-text="pageData.validationError.emailError"></span>
              </p>
            </div>
            <div class="relative z-0 w-full mb-6 group col-span-1">
              <span class="mb-1 block text-gray-500 text-sm font-medium hover:cursor-default">Gender</span>
              <div class="flex h-[75%]">
                <div class="pr-4 flex items-center justify-center">
                  <input class="peer/male"
                         id="gender_male"
                         type="radio"
                         name="gender"
                         value="male"
                         v-model="pageData.customerCreateData.gender">
                  <label class="pl-2 text-sm text-gray-500 peer-checked/male:text-sky-700"
                         for="gender_male">Male</label>
                </div>
                <div class="flex items-center justify-center">
                  <input class="peer/female"
                         id="gender_female"
                         type="radio"
                         name="gender"
                         value="female"
                         v-model="pageData.customerCreateData.gender">
                  <label class="pl-2 text-sm text-gray-500 peer-checked/female:text-sky-700"
                         for="gender_female">Female</label>
                </div>
              </div>
              <p v-if="pageData.validationError.genderError.length !== 0" class="mt-2 text-sm text-red-600 relative">
                <span class="text-sm" v-text="pageData.validationError.genderError"></span>
              </p>
            </div>
            <div class="relative z-0 w-full mb-6 group col-span-1">
              <label for="role" class="block mb-2 text-sm font-medium text-gray-500">
                Role
              </label>
              <select id="role"
                      class="block w-full p-2 text-sm text-gray-900 border border-gray-300 rounded-lg bg-gray-50 focus:ring-blue-500 focus:border-blue-500"
                      v-model="pageData.customerCreateData.role">
                <option selected disabled value="">Choose a role</option>
                <option v-for="role in pageData.roles" :key="role" :value="role" v-text="role"></option>
              </select>
              <p v-if="pageData.validationError.roleError.length !== 0" class="mt-2 text-sm text-red-600 relative">
                <span class="text-sm" v-text="pageData.validationError.roleError"></span>
              </p>
            </div>
            <div class="relative z-0 w-full mb-6 group col-span-1">
              <input type="text"
                     name="username"
                     id="username"
                     class="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                     placeholder=" "
                     v-model="pageData.customerCreateData.username"/>
              <label for="username"
                     class="peer-focus:font-medium absolute text-sm text-gray-500 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">
                Username
              </label>
              <p v-if="pageData.validationError.usernameError.length !== 0" class="mt-2 text-sm text-red-600 relative">
                <span class="text-sm" v-text="pageData.validationError.usernameError"></span>
              </p>
            </div>
            <div class="relative z-0 w-full mb-6 group col-span-1">
              <input type="password"
                     name="password"
                     id="password"
                     class="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                     placeholder=" "
                     v-model="pageData.customerCreateData.password"/>
              <label for="password"
                     class="peer-focus:font-medium absolute text-sm text-gray-500 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">
                Password
              </label>
              <p v-if="pageData.validationError.passwordError.length !== 0" class="mt-2 text-sm text-red-600 relative">
                <span class="text-sm" v-text="pageData.validationError.passwordError"></span>
              </p>
            </div>
            <div class="relative z-0 w-full mb-6 group col-span-2 flex justify-between">
              <div class="w-full">
                <label for="province" class="block mb-2 text-sm font-medium text-gray-500">
                  Province
                </label>
                <select id="province"
                        class="block w-full p-2 text-sm text-gray-900 border border-gray-300 rounded-lg bg-gray-50 focus:ring-blue-500 focus:border-blue-500"
                        v-model="pageData.selectedData.provinceId"
                        @change="getDistrict(pageData.selectedData.provinceId)">
                  <option selected disabled value="">Choose a province</option>
                  <option v-for="province in pageData.provinces"
                          :key="province"
                          :value="province.id"
                          v-text="province.name "></option>
                </select>
                <p v-if="pageData.validationError.provinceError.length !== 0"
                   class="mt-2 text-sm text-red-600 relative">
                  <span class="text-sm" v-text="pageData.validationError.provinceError"></span>
                </p>
              </div>
              <div class="w-full px-4">
                <label for="district" class="block mb-2 text-sm font-medium text-gray-500">
                  District
                </label>
                <select id="district"
                        class="block w-full p-2 text-sm text-gray-900 border border-gray-300 rounded-lg bg-gray-50 focus:ring-blue-500 focus:border-blue-500"
                        v-model="pageData.selectedData.districtId"
                        @change="getWard(pageData.selectedData.districtId)">
                  <option selected disabled value="">Choose a district</option>
                  <option v-for="district in pageData.districts"
                          :key="district"
                          :value="district.id"
                          v-text="district.name"></option>
                </select>
                <p v-if="pageData.validationError.districtError.length !== 0"
                   class="mt-2 text-sm text-red-600 relative">
                  <span class="text-sm" v-text="pageData.validationError.districtError"></span>
                </p>
              </div>
              <div class="w-full">
                <label for="ward" class="block mb-2 text-sm font-medium text-gray-500">
                  Ward
                </label>
                <select id="ward"
                        class="block w-full p-2 text-sm text-gray-900 border border-gray-300 rounded-lg bg-gray-50 focus:ring-blue-500 focus:border-blue-500"
                        v-model="pageData.selectedData.wardId">
                  <option selected disabled value="">Choose a ward</option>
                  <option v-for="ward in pageData.wards"
                          :key="ward"
                          :value="ward.id"
                          v-text="ward.name"></option>
                </select>
                <p v-if="pageData.validationError.wardError.length !== 0" class="mt-2 text-sm text-red-600 relative">
                  <span class="text-sm" v-text="pageData.validationError.wardError"></span>
                </p>
              </div>
            </div>
            <div class="relative z-0 w-full mb-6 group col-span-2">
              <input type="text"
                     name="house_number"
                     id="house_number"
                     class="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                     placeholder=" "
                     v-model="pageData.selectedData.houseAddress"/>
              <label for="house_number"
                     class="peer-focus:font-medium absolute text-sm text-gray-500 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6">
                House address
              </label>
              <p v-if="pageData.validationError.houseAddressError.length !== 0"
                 class="mt-2 text-sm text-red-600 relative">
                <span class="text-sm" v-text="pageData.validationError.houseAddressError"></span>
              </p>
            </div>
            <div class="relative z-0 w-full mb-6 group col-span-2 flex justify-end">
              <button type="button"
                      class="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 focus:outline-none w-[20%]"
                      @click="submit">
                Submit
              </button>
            </div>
          </form>
        </div>
      </admin-content-area>
      <div class="absolute top-[8%] right-0">
        <alert-list-info :hide="hideListInfo"
                         :title="'Create customer'"
                         :error-messages="[pageData.submitDataSuccessMsg]"
                         @hidden="hideInfoAlert"/>
        <alert-list-error :hidden="hideListError"
                          :title="'Create customer'"
                          :error-messages="pageData.submitDataFailureMsg"
                          @hidden="hideErrorAlert"/>
      </div>
    </loading-block-u-i>
  </div>
</template>
