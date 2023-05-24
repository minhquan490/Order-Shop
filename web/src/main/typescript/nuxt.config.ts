// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
    modules: [
        '@nuxtjs/tailwindcss',
        'nuxt-csurf',
        '@nuxtjs/google-fonts',
        'nuxt-icon',
    ],
    ssr: true,
    devtools: {
        enabled: true
    },
    vite: {
        appType: 'custom',
        build: {
            cssMinify: true,
            minify: true,
            ssr: true
        }
    },
    nitro: {
        minify: true,
    },
    pages: true,
    components: true,
    typescript: {
        tsConfig: {
            "include": ["./types/*", "./services/*"],
        }
    },
    csurf: {
        cookie: {
            path: '/',
            httpOnly: true,
            sameSite: 'strict'
        },
        methodsToProtect: ['POST', 'PUT', 'DELETE'],
        encryptAlgorithm: 'aes-256-cbc',
    },
    googleFonts: {
        families: {
            Roboto: {
                wght: [100, 300, 400, 500, 700, 900]
            }
        },
        display: 'swap',
        preconnect: true,
        preload: true,
        prefetch: true,
        useStylesheet: false
    },
    icon: {
        size: '24px',
        class: 'icon'
    },
    app: {
        keepalive: {
            max: 10000
        },
    },
    tailwindcss: {
        viewer: false,
    },
    build: {
        transpile: ['rxjs']
    }
})
