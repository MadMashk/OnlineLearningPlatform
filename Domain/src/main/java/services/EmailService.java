package services;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;
import java.util.Map;

@Service
public class EmailService {
    @Autowired
    private SpringTemplateEngine springTemplateEngine;
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String SERVICE_ADDRESS;

    @Transactional
    @SneakyThrows
    public void sendMessage(String to, String subject, Map<String, Object> templateModel){
        Context context = new Context();
        context.setVariables(templateModel);
        String body = springTemplateEngine.process("forgottenPassword-email.html", context);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(SERVICE_ADDRESS);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);
        mailSender.send(message);
    }
}
