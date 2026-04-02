package com.orchestrator.TaskOrchestrator.Model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orchestrator.TaskOrchestrator.DTO.EmailPayload;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


@Entity
@DiscriminatorValue("email")
@Slf4j
public class EmailTask extends Task {

    @Override
    public void execute(ApplicationContext applicationContext){

        ObjectMapper mapper = applicationContext.getBean(ObjectMapper.class);
        JavaMailSender mailSender = applicationContext.getBean(JavaMailSender.class);

        try {
            if (this.getPayload() == null || this.getPayload().isBlank()) {
                throw new IllegalArgumentException("Payload is missing.");
            }

            EmailPayload data = mapper.readValue(this.getPayload(), EmailPayload.class);

            if (data.getRecipient() == null || !data.getRecipient().contains("@")) {
                throw new IllegalArgumentException("Invalid recipient address.");
            }

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(data.getRecipient());
            mailMessage.setSubject(data.getSubject());
            mailMessage.setText(data.getBody());

            log.info("Sending Email to {}", data.getRecipient());
            mailSender.send(mailMessage);
            log.info("Mail sent successfully to {}", data.getRecipient());
        }
        catch(JsonProcessingException e){
            throw new RuntimeException("Failed to parse JSON",e);
        }
        catch (MailException e){
            throw new RuntimeException("Failed to send Email.",e);
        }
    }
}
