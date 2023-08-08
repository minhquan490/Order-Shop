// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
    devtools: {enabled: true},
    modules: [
        "@nuxtjs/tailwindcss",
        "@nuxtjs/google-fonts",
        "nuxt-icon",
        "@pinia/nuxt",
    ],
    ssr: true,
    vite: {
        appType: "custom",
        build: {
            cssMinify: true,
            minify: "esbuild",
            copyPublicDir: true,
            cssCodeSplit: true,
            emptyOutDir: true,
            sourcemap: "inline",
        }
    },
    nitro: {
        minify: true
    },
    pages: true,
    googleFonts: {
        families: {
            Roboto: {
                wght: [100, 300, 400, 500, 700, 900],
            },
            Pacifico: {
                wght: [100, 300, 400, 500, 700, 900],
            },
            Montserrat: {
                wght: [500]
            }
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
        head: {
            link: [
                {
                    rel: "icon",
                    type: "image/x-icon",
                    href: "/favicon.png",
                },
            ],
        },
    },
    build: {
        transpile: ["rxjs"]
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
                    "components/**/*.{vue,html}",
                    "pages/**/*.{vue,html}",
                ];
            },
        },
        configPath: ['~/tailwind.config.js']
    },
    css: ["~/assets/styles/global.scss"],
    experimental: {
        componentIslands: true,
    },
    pinia: {
        autoImports: ["defineStore", ["defineStore", "definePiniaStore"]],
    },
    router: {
        options: {
            strict: false
        }
    }
});
