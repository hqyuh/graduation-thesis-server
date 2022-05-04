package com.hqh.graduationthesisserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.io.File;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Date;

import static com.hqh.graduationthesisserver.constant.EmailConstant.*;
import static com.hqh.graduationthesisserver.constant.FileConstant.*;

@Service
public class EmailService2 {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService2(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /***
     *
     * @param name
     * @param password
     * @param email
     * @throws MessagingException
     */
    @Async
    public void sendNewPasswordEmail(String name,
                                     String password,
                                     String email,
                                     String emailSubject) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(FROM_EMAIL);
        helper.setTo(email);
        helper.setSubject(emailSubject);
        helper.setSentDate(new Date());
        helper.setText(HELLO + name + YOUR_NEW_ACCOUNT_PASSWORD_IS
                + password + IMAGE_EMAIL + QUIZIZZ_SUPPORT_GROUP, true);

        Path currentDir = Paths.get(SRC, MAIN, RESOURCES, STATIC, IMAGE_PATH);
        Path fullPath = Path.of(currentDir.toAbsolutePath()
                                          .toString()
                                          .replace(FORWARD_SLASH_A, FORWARD_SLASH));
        FileSystemResource resource = new FileSystemResource(new File(fullPath
                + FORWARD_SLASH + NAME_IMAGE_EMAIL));
        helper.addInline(IMAGE_PATH, resource);

        mailSender.send(message);

    }

}
