export class CookieParser {
    parseCookie(cookieString?: string): Map<string, string> {
        if (!cookieString) {
            return new Map<string, string>;
        }
        const cookies = cookieString.split('; ');
        const result = new Map<string, string>;
        cookies.forEach(value => {
            const keyPair = value.split('=');
            result.set(keyPair[0], keyPair[1]);
        });
        return result;
    }
}