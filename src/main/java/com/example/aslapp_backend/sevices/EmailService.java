package com.example.aslapp_backend.sevices;


import com.example.aslapp_backend.event.UserRegisteredEvent;
import com.example.aslapp_backend.models.user;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class EmailService {


    final JavaMailSender mailSender;
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;

    }
    @Async
    @TransactionalEventListener
    public void asyncSendEmail(UserRegisteredEvent event) throws MessagingException, IOException {
        user user = event.getUsersave();
        String Jwt = event.getJwt();
        sendEmail(user, Jwt);

    }



    public void sendEmail(user user, String Jwt) throws MessagingException, IOException {

        String templatePath = "src/main/resources/templates/tamplateEmail.html";
        String emailContent = new String(Files.readAllBytes(Paths.get(templatePath)), StandardCharsets.UTF_8);
        emailContent = emailContent.replace("{{name}}", user.getUsername());
        emailContent = emailContent.replace("{{JWT}}",Jwt);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(user.getEmail());
        helper.setSubject("Validat_email");
        helper.setText(emailContent, true); // تحديد `true` لتمكين HTML
        helper.setFrom("oussama.abcde200@gmail.com");

        // إرسال البريد الإلكتروني
        mailSender.send(message);

    }
}
