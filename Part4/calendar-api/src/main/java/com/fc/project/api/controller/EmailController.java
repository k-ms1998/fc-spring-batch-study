package com.fc.project.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.internet.MimeMessage;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final JavaMailSender emailSender;

    @GetMapping("/sendTest")
    public void sendTestMail(@RequestParam(required = false) String recipient) {
        final MimeMessagePreparator preparator = (MimeMessage mimeMessage) -> {
            final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setTo(recipient);
            helper.setSubject("Test Subject");
            helper.setText("Test Message.");
        };

        emailSender.send(preparator);
    }
}
