package com.hqh.quizserver.service;

import com.sun.mail.smtp.SMTPTransport;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

import static com.hqh.quizserver.constant.EmailConstant.*;
import static javax.mail.Message.RecipientType.TO;
import static javax.mail.Message.RecipientType.CC;

@Service
public class EmailService {

    /***
     * set up email
     *
     * @return
     */
    private Session getEmailSession() {
        Properties properties = System.getProperties();
        properties.put(SMTP_HOST, GMAIL_SMTP_SERVER);
        properties.put(SMTP_AUTH, true);
        properties.put(SMTP_PORT, DEFAULT_PORT);
        properties.put(SMTP_STARTTLS_ENABLE, true);
        properties.put(SMTP_STARTTLS_REQUIRED, true);
        return Session.getInstance(properties, null);
    }

    /***
     *
     * @param name
     * @param password
     * @param email
     * @throws MessagingException
     * @return
     */
    public Message createEmail(String name, String password, String email) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(TO, InternetAddress.parse(email, false));
        message.setRecipients(CC, InternetAddress.parse(CC_EMAIL, false));
        message.setSubject(EMAIL_SUBJECT_RESET);
        message.setText(HELLO + name + YOUR_NEW_ACCOUNT_PASSWORD_IS + password + QUIZIZZ_SUPPORT_GROUP);
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }

    /***
     * sent email
     *
     * @param name
     * @param password
     * @param email
     * @throws MessagingException
     */
    @Async
    public void setNewPasswordEmail(String name, String password, String email) throws MessagingException {
        Message message = createEmail(name, password, email);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession()
                .getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }

}
