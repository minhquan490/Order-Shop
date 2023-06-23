export class RegexUtils {
    private static readonly httpRegex: RegExp = /((https?):\/\/)?(www.)?[a-z0-9]+(\.[a-z]{2,}){1,3}(#?\/?[a-zA-Z0-9#]+)*\/?(\?[a-zA-Z0-9-_]+=[a-zA-Z0-9-%]+&?)?$/;
    private static readonly productSizeRegex: RegExp = /^[S|s]|[M|m]|[L|l]|([X|x]{1,}[L|l])$/;

    public static getHttpRegex(): RegExp {
        return this.httpRegex;
    }

    public static getProductSizeRegex(): RegExp {
        return this.productSizeRegex;
    }
}