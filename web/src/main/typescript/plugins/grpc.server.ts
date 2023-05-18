export default defineNuxtPlugin((nuxtApp) => {
    nuxtApp.hooks.hookOnce('app:rendered', (ctx) => {
        const appConfig = useAppConfig();
        if ((appConfig.beans.size === 0)) {
            // const beans: Map<string, object> = new Map();
            // beans.set('grpcHttpMessageHandler', new GrpcHttpMessageHandler());
            // updateAppConfig({beans: beans});
        }
    })
})
