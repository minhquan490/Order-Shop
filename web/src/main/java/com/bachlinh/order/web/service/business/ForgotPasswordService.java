package com.bachlinh.order.web.service.business;

public interface ForgotPasswordService {
    void sendEmailResetPassword(String email);

    void resetPassword(String temporaryToken, String newPassword);
}
