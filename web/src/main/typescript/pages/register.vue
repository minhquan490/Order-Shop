<script lang="ts" setup>
const title: string = "Register page";
const description: string = "Register and world of taobao will be in your hand";

useHead({
  meta: [
    {
      name: 'description',
      content: description
    },
    {
      property: 'og:title',
      content: title
    },
    {
      property: 'og:description',
      content: description
    },
    {
      property: 'og:image',
      content: "/login-img.webp"
    },
    {
      property: 'og:url',
      content: "/register"
    },
    {
      name: 'twitter:title',
      content: title
    },
    {
      name: 'twitter:description',
      content: description
    },
    {
      name: 'twitter:image',
      content: "/login-img.webp"
    },
    {
      name: 'twitter:card',
      content: 'web page'
    }
  ],
  htmlAttrs: {
    lang: 'en'
  },
  link: [
    {
      rel: 'icon',
      type: 'image/png',
      href: '/favicon.ico'
    }
  ],
  title: title
})

const device = useState(inject('device')).value;
</script>

<template>
  <div>
    <div v-if="device === 'pc'">
      <RegisterPc />
    </div>
    <div v-if="device === 'mobile'">
      <RegisterMobile />
    </div>
  </div>
</template>