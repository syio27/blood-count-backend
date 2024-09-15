package com.pja.bloodcount.service.contract;

public interface MailService {

    void sendMail(String toEmail, String subject, String content);
}
