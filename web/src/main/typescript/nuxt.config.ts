// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
    modules: [
        '@nuxtjs/tailwindcss',
        'nuxt-csurf'
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
        appConfig: {
            beans: Map<string, object>
        },
    },
    csurf: {
        cookie: {
            path: '/',
            httpOnly: true,
            sameSite: 'strict'
        },
        methodsToProtect: ['POST', 'PUT', 'DELETE'],
        encryptAlgorithm: 'aes-256-cbc'
    },
    pages: true,
    components: true,
    typescript: {
        tsConfig: {
            "include": ["./types/*", "./services/*"],
        }
    }
})
