package com.fc.project.api.controller.dev;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/test/email")
@RequiredArgsConstructor
public class TestEmailController {

    private final JavaMailSender emailSender;

    @GetMapping("/sendTest")
    public @ResponseBody
    void sendTestMail(@RequestParam(required = false) String recipient) {
        final MimeMessagePreparator preparator = (MimeMessage mimeMessage) -> {
            final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setTo(recipient);
            helper.setSubject("Test Subject");
            helper.setText("Test Message.");
        };

        emailSender.send(preparator);
    }
}
