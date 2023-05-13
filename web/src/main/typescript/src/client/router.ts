import { createMemoryHistory, createRouter, createWebHistory } from "vue-router";

const pages = import.meta.glob('./views/*.vue');

const routes = Object.keys(pages).map((path: string) => {
    console.log(pages);
    const match = path.match(/\.\/views(.*)\.vue$/);
    if (match != null) {
        const name = match[1].split('View')[0].toLocaleLowerCase();
        return {
            path: name === '/home' ? '/' : name,
            component: pages[path],
        };
    }
    throw new Error("Could not create router");
});

export function createAppRouter() {
    return createRouter({
        history: import.meta.env.SSR ? createMemoryHistory('/') : createWebHistory('/'),
        routes,
    });
}