package com.bachlinh.order.web.service.business;

import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.web.dto.form.LoginForm;
import com.bachlinh.order.web.dto.resp.LoginResp;

public interface LoginService {
    LoginResp login(LoginForm loginForm, NativeRequest<?> request);
}
