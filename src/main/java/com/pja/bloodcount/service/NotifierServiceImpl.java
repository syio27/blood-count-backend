package com.pja.bloodcount.service;

import com.pja.bloodcount.service.contract.NotifierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotifierServiceImpl implements NotifierService {
    private final MailService mailService;

    @Override
    public void notifyUser(String email, String subject, String message) {
        log.info("Notifying user by sending an email to: {}", email);
        mailService.sendMail(email, subject, message);
        log.info("User has been notified, email sent");
    }
}
