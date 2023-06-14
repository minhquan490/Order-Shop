<script lang="ts" setup>
const title: string = "Login page";
const description: string = "Login and order every thing product on taobao whatever you want";
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
      content: "/login"
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
      <LoginDesktop />
    </div>
    <div v-if="device === 'mobile'">
      <LoginMobile />
    </div>
  </div>
  <footer>
    <Footer />
  </footer>
</template>
