export const useRegex = () => {
    const httpRegex: RegExp = /((https?):\/\/)?(www.)?[a-z0-9]+(\.[a-z]{2,}){1,3}(#?\/?[a-zA-Z0-9#]+)*\/?(\?[a-zA-Z0-9-_]+=[a-zA-Z0-9-%]+&?)?$/gm;
    const productSizeRegex: RegExp = /^[S|s]|[M|m]|[L|l]|([X|x]+[L|l])$/gm;
    const phoneRegex: RegExp = /(84|0[3|5|7|8|9])+([0-9]{8})\b/gm;
    const emailRegex: RegExp = /([!#-'*+/-9=?A-Z^-~-]+(\.[!#-'*+/-9=?A-Z^-~-]+)*|"([]!#-[^-~ \t]|(\\[\t -~]))+")@([!#-'*+/-9=?A-Z^-~-]+(\.[!#-'*+/-9=?A-Z^-~-]+)*|\[[\t -Z^-~]*])/gm;
    const strongPasswordRegex: RegExp = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/gm;
    const numberRegex: RegExp = /^[0-9]+$/;

    return {
        httpRegex,
        productSizeRegex,
        phoneRegex,
        emailRegex,
        strongPasswordRegex,
        numberRegex
    }
}
