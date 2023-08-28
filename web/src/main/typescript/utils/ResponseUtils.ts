import {Response} from "~/types";

const checkResponseStatus = (response: Response<any>): void => {
    const navigate = useNavigation().value;
    const statusCode: number = response.statusCode;
    if (statusCode === 401) {
        navigate('/login');
        return;
    }
    if (statusCode === 403) {
        navigate('/403');
        return;
    }
    if (statusCode === 404) {
        navigate('/404');
    }
}

export {
    checkResponseStatus
}