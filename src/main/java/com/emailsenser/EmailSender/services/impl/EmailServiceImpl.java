package com.emailsenser.EmailSender.services.impl;

import com.emailsenser.EmailSender.helper.Message;
import com.emailsenser.EmailSender.services.EmailService;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    // ⚠️ MUST BE BREVO VERIFIED EMAIL
    private static final String FROM_EMAIL = "pavandhangude28@gmail.com";

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // ===================== SIMPLE TEXT EMAIL =====================

    @Async
    @Override
    public void sendEmail(String to, String subject, String message) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(message);
            mail.setFrom(FROM_EMAIL);

            mailSender.send(mail);
            logger.info("Text email sent to {}", to);
        } catch (Exception e) {
            logger.error("Failed to send text email", e);
        }
    }

    @Override
    public void sendEmail(String[] to, String subject, String message) {

    }

    // ===================== HTML EMAIL =====================

    @Async
    @Override
    public void sendEmailWithHtml(String to, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(FROM_EMAIL);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            logger.info("HTML email sent to {}", to);

        } catch (Exception e) {
            logger.error("Failed to send HTML email", e);
        }
    }

    @Override
    public void sendEmailWithFile(String to, String subject, String message, File file) {

    }

    // ===================== EMAIL WITH FILE =====================

    @Async
    @Override
    public void sendEmailWithFile(String to, String subject, String message, InputStream is) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(FROM_EMAIL);
            helper.setText(message, true);

            File tempFile = new File("attachment-temp");
            Files.copy(is, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            FileSystemResource resource = new FileSystemResource(tempFile);
            helper.addAttachment(resource.getFilename(), resource);

            mailSender.send(mimeMessage);
            logger.info("Email with attachment sent to {}", to);

        } catch (Exception e) {
            logger.error("Failed to send email with attachment", e);
        }
    }

    // ===================== EMAIL READING CONFIG =====================

    @Value("${mail.store.protocol:imaps}")
    private String protocol;

    @Value("${mail.imaps.host:imap.gmail.com}")
    private String host;

    @Value("${mail.imaps.port:993}")
    private String port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Override
    public List<Message> getInboxMessages() {
        List<Message> list = new ArrayList<>();
        try {
            Properties props = new Properties();
            props.setProperty("mail.store.protocol", protocol);
            props.setProperty("mail.imaps.host", host);
            props.setProperty("mail.imaps.port", port);

            Session session = Session.getDefaultInstance(props);
            Store store = session.getStore();
            store.connect(username, password);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            jakarta.mail.Message[] messages = inbox.getMessages();

            for (jakarta.mail.Message msg : messages) {
                list.add(
                        Message.builder()
                                .subjects(msg.getSubject())
                                .content(getContent(msg))
                                .build()
                );
            }

        } catch (Exception e) {
            logger.error("Failed to read inbox", e);
        }
        return list;
    }

    private String getContent(jakarta.mail.Message message) throws Exception {
        if (message.isMimeType("text/plain") || message.isMimeType("text/html")) {
            return message.getContent().toString();
        }
        return "";
    }
}
