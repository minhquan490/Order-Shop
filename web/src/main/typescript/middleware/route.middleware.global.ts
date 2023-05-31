export default defineNuxtRouteMiddleware((to, from) => {
    if (to.path === '/home') {
        return navigateTo('/');
    }
    const router = useRouter();
    const routes = router.getRoutes().map(r => r.path);
    if (!routes.includes(to.path) || to.path === '/api') {
        return navigateTo('/404');
    }
})
