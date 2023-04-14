package lithium.service.mail.stream;

import lithium.service.mail.client.objects.EmailData;
import lithium.service.mail.data.entities.EmailTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import lithium.service.mail.services.MailService;
import lombok.extern.slf4j.Slf4j;


@Component
@EnableBinding(MailQueueSink.class)
@Slf4j
public class MailQueueProcessor {
    @Autowired
    private MailService mailService;

    @StreamListener(MailQueueSink.INPUT)
    void handle(EmailData emailData) throws Exception {
        log.info("Received mail from queue for processing :: " + emailData.getUserGuid());
        String logStr = "Received mail from queue for processing :: " +
                "aguid :" + emailData.getAuthorGuid() +
                " dn :" + emailData.getDomainName() +
                " tmpl :" + emailData.getEmailTemplateName() +
                " lang :" + emailData.getEmailTemplateLang() +
                " to :" + emailData.getTo() +
                " p :" + emailData.getPriority() +
                " guid :" + emailData.getUserGuid() +
                " ph :" + emailData.resolvePlaceholders() +
                " a :" + emailData.getAttachmentName();
        log.debug(logStr);

        EmailTemplate emailTemplate = mailService.getTemplate(
                emailData.getDomainName(),
                emailData.getEmailTemplateName(),
                emailData.getEmailTemplateLang(),
                logStr);

        if (emailTemplate == null) {
            logStr += " -emailTemplate not found, exiting..";
            log.debug(logStr);
            return;
        }

        mailService.save(
                emailTemplate,
                emailData.getAuthorGuid(),
                emailData.getDomainName(),
                emailData.getTo(),
                emailData.getPriority(),
                emailData.getUserGuid(),
                emailData.resolvePlaceholders(),
                emailData.getAttachmentName(),
                emailData.getAttachmentData()
        );
    }
}