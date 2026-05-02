package com.spring.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.mail.from:}")
    private String from;

    public EmailService(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSenderProvider = mailSenderProvider;
    }

    public void enviarCorreo(String para, String asunto, String mensaje) {
        if (!StringUtils.hasText(para)) {
            return;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            logger.info("Correo no enviado a {} porque no hay SMTP configurado.", para);
            return;
        }

        try {
            SimpleMailMessage email = new SimpleMailMessage();
            if (StringUtils.hasText(from)) {
                email.setFrom(from);
            }
            email.setTo(para);
            email.setSubject(asunto);
            email.setText(mensaje);

            mailSender.send(email);
            logger.info("Correo enviado a {} con asunto '{}'.", para, asunto);
        } catch (RuntimeException e) {
            logger.warn("No se pudo enviar el correo a {}: {}", para, e.getMessage());
        }
    }
}
