import {NavBarsSource} from "~/types";

type Route = ReturnType<typeof useRoute>

const navSources = (): NavBarsSource[] => {
    return [
        {name: 'Dashboard', href: '/admin', current: false},
        {name: 'Batch', href: '/admin/batch', current: false},
        {name: 'Category', href: '/admin/category', current: false},
        {name: 'Customer', href: '/admin/customer', current: false},
        {name: 'Crawl', href: '/admin/crawl', current: false},
        {name: 'Email template', href: '/admin/email-template', current: false},
        {name: 'Message setting', href: '/admin/message-setting', current: false},
        {name: 'Product', href: '/admin/product', current: false},
        {name: 'Order', href: '/admin/order', current: false},
    ];
}

const currentPath = (sources: NavBarsSource[], route: Route): Array<NavBarsSource> => {
    return sources.map((value: NavBarsSource) => {
        if (route.fullPath === value.href) {
            value.current = true;
        } else {
            const routePart: string[] = route.fullPath.split('/');
            const currentPart: string[] = value.href.split('/');
            if (currentPart.length === 2) {
                return value;
            }
            if (routePart[currentPart.length - 1] === currentPart[currentPart.length - 1]) {
                value.current = true;
            }
        }
        return value;
    });
}

export {
    currentPath,
    navSources
}