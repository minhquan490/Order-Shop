// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  modules: ["@nuxtjs/tailwindcss", "@nuxtjs/google-fonts", "nuxt-icon"],
  ssr: true,
  devtools: {
    enabled: true,
  },
  vite: {
    appType: "custom",
    build: {
      cssMinify: true,
      minify: "esbuild",
      copyPublicDir: true,
      cssCodeSplit: true,
      emptyOutDir: true,
      sourcemap: false,
    },
  },
  nitro: {
    minify: true,
  },
  pages: true,
  components: true,
  typescript: {
    tsConfig: {
      include: ["./types/*", "./services/*"],
    },
  },
  googleFonts: {
    families: {
      Roboto: {
        wght: [100, 300, 400, 500, 700, 900],
      },
    },
    display: "swap",
    preconnect: true,
    preload: true,
    prefetch: true,
    useStylesheet: false,
  },
  icon: {
    size: "24px",
    class: "icon",
  },
  app: {
    keepalive: {
      max: 10000,
    },
  },
  build: {
    transpile: ["rxjs"],
  },
  $production: {
    sourcemap: false,
  },
  tailwindcss: {
    exposeConfig: false,
    exposeLevel: 3,
    cssPath: "~/assets/styles/global.scss",
    config: {
      content() {
        return [
          "components/*.{vue,html,jsx,tsx}",
          "pages/*.{vue,html,jsx,tsx}",
        ];
      },
    },
  },
  css: ['~/assets/styles/global.scss']
});
