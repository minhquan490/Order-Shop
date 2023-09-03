import {Response} from "~/types";
import {onNotFound, onUnAuthorize, onUnLogin} from "~/utils/NavigationUtils";

const checkResponseStatus = (response: Response<any>): void => {
    const statusCode: number = response.statusCode;
    if (statusCode === 401) {
        onUnLogin();
        return;
    }
    if (statusCode === 403) {
        onUnAuthorize();
        return;
    }
    if (statusCode === 404) {
        onNotFound();
    }
}

export {
    checkResponseStatus
}