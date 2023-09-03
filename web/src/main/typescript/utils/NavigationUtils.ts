const onUnAuthorize = (): void => {
    const navigate = useNavigation().value;
    navigate('/403');
}

const onNotFound = (): void => {
    const navigate = useNavigation().value;
    navigate('/404');
}

const onUnLogin = (): void => {
    const navigate = useNavigation().value;
    navigate('/login');
}

export {
    onNotFound,
    onUnAuthorize,
    onUnLogin
}