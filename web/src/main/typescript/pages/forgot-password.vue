<script lang="ts">

export default {
  data() {
    const queryParam = useRoute().query;
    const token = queryParam['token'];
    return {
      token
    }
  },
}

</script>

<template>
  <div class="forgot-password bg-blue-300">
    <template v-if="token">
      <div class="">
        
      </div>
    </template>
    <template v-if="!token">
      <div class="grid grid-cols-4">
        <div class="email-form col-start-2 col-end-4 rounded-lg grid grid-rows-4">
          <div class="p-10 flex items-center justify-center row-start-1 row-end-2 flex-col">
            <Icon name="tabler:circle-key" class="icon text-green-600"/>
            <span class="hover:cursor-default text-2xl">Forgot your password?</span>
          </div>
          <div class="row-span-2 row-end-4 px-4">

          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped src="~/assets/styles/pc/forgot-password.scss"></style>
