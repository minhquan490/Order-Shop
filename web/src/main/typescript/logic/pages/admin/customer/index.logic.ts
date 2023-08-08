import {NavBarsSource, TableHeader} from "~/types";
import {currentPath, navSources} from "~/logic/components/navbars.logic";

const navigationSources = (): NavBarsSource[] => {
    return currentPath(
        navSources(),
        useRoute()
    );
}

const tableHeaders = (): TableHeader[] => {
    return [
        {name: 'ID', dataPropertyName: 'id'},
        {name: 'First name', dataPropertyName: 'first_name'},
        {name: 'Last name', dataPropertyName: 'last_name'},
        {name: 'Phone', dataPropertyName: 'phone'},
        {name: 'Email', dataPropertyName: 'email'},
        {name: 'Gender', dataPropertyName: 'gender'},
        {name: 'Username', dataPropertyName: 'username'},
        {name: 'Is activated', dataPropertyName: 'is_activated'},
        {name: 'Account non expired', dataPropertyName: 'is_account_non_expired'},
        {name: 'Account non locked', dataPropertyName: 'is_account_non_locked'},
        {name: 'Credentials non expired', dataPropertyName: 'is_credentials_non_expired'},
        {name: 'Enabled', dataPropertyName: 'is_enabled'}
    ];
}

export {
    navigationSources,
    tableHeaders
}