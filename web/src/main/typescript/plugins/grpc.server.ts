import { GrpcHttpMessageHandler } from "~/server/core/grpc/grpc-http-message-handler";

export default defineNuxtPlugin((nuxtApp) => {
    const beans: Map<string, object> = new Map();
    beans.set('grpcHttpMessageHandler', new GrpcHttpMessageHandler());
    updateAppConfig({'beans': beans});
})
