export default defineNuxtRouteMiddleware((to, from) => {
    if (to.path === '/home') {
        return '/';
    }
    const router = useRouter();
    const routes = router.getRoutes().map(r => r.path);
    if (!routes.includes(to.path)) {
        return '/404';
    }
})
