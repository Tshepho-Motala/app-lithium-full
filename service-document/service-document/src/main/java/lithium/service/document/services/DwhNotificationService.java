package lithium.service.document.services;

import lithium.service.document.client.objects.mail.MailRequest;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.mail.client.objects.EmailData;
import lithium.service.mail.client.stream.MailStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static lithium.service.domain.client.DomainSettings.UPLOADED_DOCUMENT_MAIL_DWH;

@Slf4j
@Service
public class DwhNotificationService {
    @Autowired
    private CachingDomainClientService cachingDomainClientService;
    @Autowired
    private MailStream mailStream;

    public void sendMail(MailRequest request) throws Status550ServiceDomainClientException {
        String domainName = request.getDomainName();
        Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
        String dwhEmail = cachingDomainClientService.getDwhNotifyMailForUploadedDocuments(domainName);

        if (dwhEmail.isEmpty()) {
            log.warn("Can't send email due to missing '" + UPLOADED_DOCUMENT_MAIL_DWH.key() + "' setting in " + domainName + " domain settings");
            return;
        }

        mailStream.process(EmailData.builder()
                .authorSystem()
                .emailTemplateName(request.getTemplate().getTemplateName())
                .emailTemplateLang(domain.getDefaultLocale().split("-")[0])
                .to(dwhEmail)
                .userGuid(request.getUserGuid())
                .priority(2)
                .placeholders(request.getPlaceholders())
                .domainName(domainName)
                .build());
    }
}
