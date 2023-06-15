export default defineNuxtRouteMiddleware(async (to, from) => {
  if (to.fullPath.startsWith("/admin")) {
    const config = useAppConfig();
    const headers = useRequestHeaders();
    const accessHeader = config.authorization;
    const refreshHeader = config.refreshToken;
    const serverUrl = config.serverUrl;
    const request = {
      access_token: headers[accessHeader],
      refresh_token: headers[refreshHeader],
    };
    const fetch = await useFetch(`${serverUrl}/verify-user`, {
      method: "post",
      body: JSON.stringify(request),
      parseResponse: (res) => {
        return JSON.parse(res);
      },
    });
    const navigate = useNavigation().navigate;
    if (fetch.error !== null) {
      return navigate("/403", true, 301);
    }
    const result: { role: string } = fetch.data.value as { role: string };
    if (result.role.toLocaleLowerCase() !== "admin") {
      return navigate("/403", true, 301);
    }
  }
});
