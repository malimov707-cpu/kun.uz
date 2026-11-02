package dasturlash.uz.service;

import dasturlash.uz.dto.MessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSendingService {
    @Value("${spring.mail.username}")
    private String fromAccount;
    @Autowired
    private JavaMailSender javaMailSender;

    public String sendSimpleMessage(MessageDTO dto) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromAccount);
        msg.setTo(dto.getToAccount());
        msg.setSubject(dto.getSubject());
        msg.setText(dto.getText());
        javaMailSender.send(msg);

        return "Mail was send";
    }
}