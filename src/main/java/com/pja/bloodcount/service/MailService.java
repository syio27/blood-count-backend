package com.pja.bloodcount.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

    private static final String CLASSPATH_STATIC_IMAGES_LOGOBC_PNG = "classpath:static/images/logobc.PNG";
    private static final String CONTENT_TYPE = "image/png";
    private static final String CONTENT_ID = "image";
    private final JavaMailSender javaMailSender;
    private final ResourceLoader resourceLoader;

    public void sendMail(String toEmail, String subject, String message) {
        SimpleMailMessage mailMessage = getSimpleMailMessage(toEmail, subject, message);
        javaMailSender.send(mailMessage);
    }

    public void sendHtmlEmail(String toEmail, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = getMimeMessageHelper(toEmail, subject, htmlContent, mimeMessage);
            Resource resource = resourceLoader.getResource(CLASSPATH_STATIC_IMAGES_LOGOBC_PNG);
            helper.addInline(CONTENT_ID, resource, CONTENT_TYPE);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.info("Sending Email failed due to: ", e.getCause());
        }
    }

    private static SimpleMailMessage getSimpleMailMessage(String toEmail, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        return mailMessage;
    }

    private static MimeMessageHelper getMimeMessageHelper(String toEmail, String subject, String htmlContent, MimeMessage mimeMessage) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        return helper;
    }
}
