export const useNavigation = () => {
    const navigate = (url: string, replace?: boolean, redirectCode?: number) => {
        if (process.server) {
            navigateTo(url, {
                replace: replace,
                redirectCode: redirectCode,
            });
        } else {
            window.location.href = url;
        }
    }

    return ref(navigate);
}
