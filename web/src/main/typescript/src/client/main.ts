import { createSSRApp, inject, type App } from 'vue';

import { JsonStringConverter, ObjectConverter } from '@core/implementer/json-converter';
import { HttpJsonOpener } from '@services/implementer/http-json-opener';
import { SocketConnector } from '@services/implementer/socket-connector';
import { createPinia } from 'pinia';
import VueApp from './App.vue';
import { createAppRouter } from './router';

export function createApp() {
    const app = createSSRApp(VueApp);
    const store = createPinia();
    app.use(store);
    const router = createAppRouter();
    app.use(router);
    decorateApp(app);
    return {app, router, store};
}

function decorateApp(app: App<Element>): void {
    app.provide('jsonConverter', new JsonStringConverter());
    app.provide('objectConverter', new ObjectConverter());
    app.provide('httpServiceProvider', new HttpJsonOpener(inject('jsonConverter', new JsonStringConverter()), inject('objectConverter', new ObjectConverter())));
    app.provide('socketService', new SocketConnector());
}