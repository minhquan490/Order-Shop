export default defineNuxtRouteMiddleware((to, from) => {
    const navigate = useNavigation().navigate;
    if (to.path === '/home') {
        return navigate('/', true, 301);
    }
    const router = useRouter();
    const routes = router.getRoutes().map(r => r.path);
    if (!routes.includes(to.path)) {
        return navigate('/404', true, 301);
    }
})