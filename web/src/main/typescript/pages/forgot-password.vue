<script lang="ts" setup>

const title: string = "Reset password page";
const description: string = "Reset your password.";
const img: string = '/logo-blue.png';

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
      content: img
    },
    {
      property: 'og:url',
      content: "/forgot-password"
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
      content: img
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
      href: '/favicon.png'
    }
  ],
  title: title
})

const device = useState(inject('device')).value;

</script>

<template>
  <div>
    <div v-if="device === 'pc'">
      <ForgotPasswordDesktop />
    </div>
    <div v-if="device === 'mobile'">
      <ForgotPasswordMobile />
    </div>
  </div>
  <footer>
    <Footer />
  </footer>
</template>
