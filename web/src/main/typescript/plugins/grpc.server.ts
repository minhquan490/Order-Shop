
export default defineNuxtPlugin((nuxtApp) => {
    nuxtApp.hooks.hookOnce('app:rendered', (ctx) => {
        const appConfig = useAppConfig();
        if ((appConfig.beans.size === 0)) {
            
        }
    })
})
