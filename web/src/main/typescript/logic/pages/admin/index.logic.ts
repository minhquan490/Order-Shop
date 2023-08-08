import pageIndex from '~/pages/admin/index.vue';
import {NavBarsSource} from "~/types";
import {currentPath, navSources} from "~/logic/components/navbars.logic";

type IndexPageComponent = InstanceType<typeof pageIndex>

const navigationSources = (): NavBarsSource[] => {
    return currentPath(
        navSources(),
        useRoute()
    );
}

export {
    navigationSources
}