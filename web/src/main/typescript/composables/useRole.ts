export const useRole = () => {

    const getRoles = (): Array<string> => {
        return ['customer', 'admin', 'seo', 'marketing']
    }

    return {
        getRoles
    }
}
