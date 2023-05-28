import { Strategy } from "../strategy";

export class RequestHeaderStrategy extends Strategy<XMLHttpRequest> {
  override applyStrategy(target: XMLHttpRequest): XMLHttpRequest {
    const appConfig = useAppConfig();
    const authorization = appConfig.authorization;
    const refresh = appConfig.refreshToken;
    if (!authorization) {
      throw new Error('No config for Auth header');
    }
    if (!refresh) {
      throw new Error('No config for refresh header');
    }
    const authHeader = localStorage.getItem(authorization);
    if (authHeader != null) {
      target.setRequestHeader(authorization, authHeader);
    }
    const refreshHeader = localStorage.getItem(refresh);
    if (refreshHeader != null) {
      target.setRequestHeader(refresh, refreshHeader);
    }
    return target;
  }

}
