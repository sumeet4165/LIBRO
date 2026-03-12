package LIBRO.libro.Service.Implementations;

import LIBRO.libro.Service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class EmailServiceimpl implements EmailService {


    private final  JavaMailSender javaMailSender;
    @Override
    public void sendEmail(String to, String subject, String body) throws MessagingException {


        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");


        helper.setSubject(subject);
        helper.setText(body);
        helper.setTo(to);

        try {
            javaMailSender.send(mimeMessage);
        }
        catch (MailException me) {
            throw new MailSendException("Failed to send email", me);
        }
    }
}


