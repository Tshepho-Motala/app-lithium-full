package lithium.service.mail.services;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.client.page.SimplePageImpl;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.DomainClient;
import lithium.service.domain.client.objects.Domain;
import lithium.service.mail.client.objects.SystemEmailData;
import lithium.service.mail.data.entities.Email;
import lithium.service.mail.data.entities.EmailTemplate;
import lithium.service.mail.data.entities.EmailTemplateRevision;
import lithium.service.mail.data.repositories.EmailRepository;
import lithium.service.mail.data.specifications.EmailSpecification;
import lithium.service.mail.exceptions.MailTemplateUserIsNotOpenException;
import lithium.service.mail.exceptions.MailToIsEmptyException;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.user.client.objects.User;
import lithium.tokens.LithiumTokenUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
@AllArgsConstructor
public class MailService {
    private EmailTemplateService emailTemplateService;
    private LithiumServiceClientFactory lithiumServiceClientFactory;
    private EmailRepository emailRepository;
    private UserService userService;
    private DomainService domainService;
    private CachingDomainClientService cachingDomainClientService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("dd/MM/yyyy");

    private Domain getExternalDomain(String domainName) {
        Response<Domain> domain = getDomainClient().findByName(domainName);
        if (!domain.isSuccessful())
            throw new IllegalArgumentException("Cound not find domain with name (" + domainName + ")");
        domainService.findOrCreate(domainName);
        return domain.getData();
    }

    private DomainClient getDomainClient() {
        DomainClient domainClient = null;
        try {
            domainClient = lithiumServiceClientFactory.target(DomainClient.class, "service-domain", true);
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Problem getting domain client (" + e.getMessage() + ")");
        }
        return domainClient;
    }

    private UserApiInternalClient getUserApiInternalClient() {
        UserApiInternalClient userApiInternalClient = null;
        try {
            userApiInternalClient = lithiumServiceClientFactory.target(UserApiInternalClient.class, "service-user", true);
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Problem getting userapiinternal client (" + e.getMessage() + ")");
        }
        return userApiInternalClient;
    }

    private String encodeValue(Placeholder placeholder, String logStr) {
        try {
            return URLEncoder.encode(placeholder.getValue(), StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            logStr += "Failed encoding placeholder (" + placeholder.getKey() + ") value: " + placeholder.getValue();
            log.error(logStr, e);
            return placeholder.getValue();
        }
    }

    private String replacePlaceholdersInText(Set<Placeholder> placeholders, String text, String logStr) {
        if (placeholders != null && text != null && !text.isEmpty()) {
            for (Placeholder placeholder : placeholders) {
                text = text.replace(placeholder.getKey(), placeholder.getValue());
                text = text.replace(placeholder.getKey().substring(0, placeholder.getKey().length() - 1) + ".urlencode%", encodeValue(placeholder, logStr));
            }
        }
        return text;
    }

    public Email save(
            EmailTemplate emailTemplate,
            String authorGuid,
            String domainName,
            String to,
            int priority,
            String userGuid,
            Set<Placeholder> placeholders,
            String attachmentName,
            byte[] attachmentData
    ) throws Exception {
        if (emailTemplate.isUserOpenStatusOnly() && !userService.isOpenUser(userGuid)) {
            log.warn("Mail template " + emailTemplate.getName() + " supports only open users.");
            throw new MailTemplateUserIsNotOpenException("Mail template " + emailTemplate.getName() + " supports only open users.");
        }
        String logStr = "Email save :: dn: " + domainName + ", tmpl: " + emailTemplate.getName() + ", lang: " + emailTemplate.getLang() + ", to: " + to + ", p: " + priority;
        logStr += ", guid: " + userGuid + ", ph: " + placeholders + ", a: " + attachmentName;
        try {
            if (to == null) {
                logStr += " - 'to' field was null, exiting..";
                log.debug(logStr);
                log.error("'to' field was null, exiting, guid:" + userGuid);
                throw new MailToIsEmptyException("'to' field was null, exiting, guid:" + userGuid);
            }

            emailTemplate = getTemplate(domainName, emailTemplate.getName(), emailTemplate.getLang(), logStr);

            Domain domain = getExternalDomain(domainName);
            String message = "EMail template for domain (" + domainName + ") with template name (" + emailTemplate.getName() + ") and template language (" + emailTemplate.getLang() + ") ";

            String from = domain.getSupportEmail();

            EmailTemplateRevision templateRevision = emailTemplate.getCurrent();

            if (templateRevision != null && templateRevision.getEmailFrom() != null && !templateRevision.getEmailFrom().isEmpty()) {
                log.debug("Field 'from' was changed from value=" + from + " to value=" + templateRevision.getEmailFrom());
                from = templateRevision.getEmailFrom();
            }

            logStr += " from: " + from;
            String subject = replacePlaceholdersInText(placeholders, emailTemplate.getCurrent().getSubject(), logStr);
            logStr += " subj: " + subject;
            String body = replacePlaceholdersInText(placeholders, emailTemplate.getCurrent().getBody(), logStr);

            if (subject == null || body == null) {
                if (body == null) {
                    body = "";
                    message += " body is null.";
                    logStr += " body: null";
                }
                if (subject == null) {
                    subject = "";
                    message += " subject is null.";
                }

                Email email = Email.builder()
                        .createdDate(new Date())
                        .priority(priority)
                        .from(from)
                        .to(to)
                        .subject(subject)
                        .body(body)
                        .user(userGuid != null && !userGuid.isEmpty() ? userService.findOrCreate(userGuid) : null)
                        .domain(domainService.findOrCreate(domainName))
                        .attachmentName(attachmentName)
                        .attachmentData(attachmentData)
                        .failed(true)
                        .errorCount(1)
                        .latestErrorReason(message)
                        .template(emailTemplate)
                        .author(userService.findOrCreate(authorGuid))
                        .build();

                log.debug(logStr); //contains email info
                log.info(message + ", guid:" + userGuid);
                return emailRepository.save(email);
            }

            Email email = Email.builder()
                    .createdDate(new Date())
                    .priority(priority)
                    .from(from)
                    .to(to)
                    .subject(subject)
                    .body(body)
                    .user(userGuid != null && !userGuid.isEmpty() ? userService.findOrCreate(userGuid) : null)
                    .domain(domainService.findOrCreate(domainName))
                    .attachmentName(attachmentName)
                    .attachmentData(attachmentData)
                    .template(emailTemplate)
                    .author(userService.findOrCreate(authorGuid))
                    .build();

            logStr += " - Success.";
            log.debug(logStr);
            return emailRepository.save(email);
        } catch (Exception e) {
            logStr += " - Exception: " + e.getMessage();
            log.debug(logStr);
            log.error("Error saving email for " + userGuid, e);
            throw new Status500InternalServerErrorException(e.getMessage());
        }
    }

    public EmailTemplate getTemplate(String domainName, String emailTemplateName, String emailTemplateLang, String logStr) {
        EmailTemplate emailTemplate = emailTemplateService.findByDomainNameAndNameAndLang(domainName, emailTemplateName, emailTemplateLang);

        if (emailTemplate == null || !emailTemplate.getEnabled()) {
            failedTemplateMessage(logStr, emailTemplate, emailTemplateLang, emailTemplateName, domainName);
            //LSPLAT-4819 Retry template with domain default
            emailTemplateLang = cachingDomainClientService.domainLocale(domainName).substring(0, 2);
            emailTemplate = emailTemplateService.findByDomainNameAndNameAndLang(domainName, emailTemplateName, emailTemplateLang);
        }
        if (emailTemplate == null || !emailTemplate.getEnabled()) {
            failedTemplateMessage(logStr, emailTemplate, emailTemplateLang, emailTemplateName, domainName);
            //LSPLAT-4819 Retry template with english
            emailTemplate = emailTemplateService.findByDomainNameAndNameAndLang(domainName, emailTemplateName, "en");
        }
        if (emailTemplate == null || !emailTemplate.getEnabled()) {
            failedTemplateMessage(logStr, emailTemplate, "en", emailTemplateName, domainName);
            return null;
        }
        return emailTemplate;
    }

    private void failedTemplateMessage(String logStr, EmailTemplate emailTemplate, String language, String emailTemplateName, String domainName) {
        String message = emailTemplateName + " template, on Domain " + domainName + " for locale: " + language + ((emailTemplate == null) ? " was not found!" : " is disabled!");
        logStr += " -" + message;
        log.debug(logStr);
        log.info(message);
    }

    public Page<Email> findByUser(String userGuid, String searchValue, Pageable pageable) {
        Specification<Email> spec = Specification.where(EmailSpecification.user(userService.findOrCreate(userGuid)));
//		if ((searchValue != null) && (searchValue.length() > 0)) {
//			Specifications<Email> s = Specifications.where(EmailSpecification.any(searchValue));
//			spec = (spec == null)? s: spec.and(s);
//		}
        return emailRepository.findAll(spec, pageable);
    }

    public Email findOne(Long id) {
        Email email = emailRepository.findOne(id);
        if (email.getUser() != null) {
            Response<User> fullUser = getUserApiInternalClient().getUser(email.getUser().getGuid());
            if (fullUser.isSuccessful()) email.setFullUser(fullUser.getData());
        }
        return email;
    }

    public Page<Email> findByDomain(String domainNamesCommaSeparated, boolean showSent, boolean showFailed,
                                    String createdDateStartString, String createdDateEndString, Long mailTemplate,
                                    String searchValue, Pageable pageable, LithiumTokenUtil tokenUtil) {
        List<lithium.service.mail.data.entities.Domain> domains = new ArrayList<lithium.service.mail.data.entities.Domain>();
        List<String> tokenPlayerDomains = tokenUtil.playerDomainsWithRoles("MAIL_QUEUE_VIEW", "PLAYER_MAIL_HISTORY_VIEW")
                .stream()
                .map(jwtDomain -> jwtDomain.getName())
                .collect(Collectors.toList());
        for (String domainName : tokenPlayerDomains) {
            domains.add(domainService.findOrCreate(domainName));
        }

        if (StringUtils.hasText(domainNamesCommaSeparated)) {
            String[] domainNames = domainNamesCommaSeparated.split(",");
            Iterator<lithium.service.mail.data.entities.Domain> iterator = domains.iterator();
            while (iterator.hasNext()) {
                lithium.service.mail.data.entities.Domain domain = iterator.next();
                boolean found = false;
                for (String domainName : domainNames) {
                    if (domain.getName().equalsIgnoreCase(domainName)) {
                        found = true;
                        break;
                    }
                }
                if (!found) iterator.remove();
            }

            Specification<Email> spec = Specification.where(EmailSpecification.domainIn(domains));
            if (!showFailed) spec = spec.and(EmailSpecification.failedFalse());
            if (!showSent) spec = spec.and(Specification.where(EmailSpecification.sentDateIsNull()));
            if (createdDateStartString != null && !createdDateStartString.isEmpty()) {
                DateTime createdDateStart = DATE_FORMATTER.parseDateTime(createdDateStartString);
                spec = spec.and(Specification.where(EmailSpecification.createdDateStart(createdDateStart.toDate())));
            }
            if (createdDateEndString != null && !createdDateEndString.isEmpty()) {
                DateTime createdDateEnd = DATE_FORMATTER.parseDateTime(createdDateEndString);
                createdDateEnd = createdDateEnd.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);
                spec = spec.and(Specification.where(EmailSpecification.createdDateEnd(createdDateEnd.toDate())));
            }
//			if ((searchValue != null) && (searchValue.length() > 0)) {
//				Specifications<Email> s = Specifications.where(EmailSpecification.any(searchValue));
//				spec = (spec == null)? s: spec.and(s);
//			}
            if (mailTemplate != null) {
                spec = spec.and(Specification.where(EmailSpecification.emailTemplate(emailTemplateService.findById(mailTemplate))));
            }
            return emailRepository.findAll(spec, pageable);
        }
        return new SimplePageImpl<>(new ArrayList<>(), 0, 1, 0);
    }

    public Email saveSystemEmail(SystemEmailData systemMail) {
        return saveEmail(systemMail, User.SYSTEM_GUID);
    }

    public Email saveEmail(SystemEmailData systemMail, String authorGuid) {

        Domain domain = getExternalDomain(systemMail.getDomainName());

        String from = domain.getSupportEmail();

        Email email = Email.builder()
                .author(userService.findOrCreate(authorGuid))
                .createdDate(new Date())
                .priority(1)
                .from(from)
                .to(systemMail.getTo() == null ? from : systemMail.getTo())
                .subject(systemMail.getSubject())
                .body(systemMail.getBody())
                .user(systemMail.getUserGuid() != null && !systemMail.getUserGuid().isEmpty() ? userService.findOrCreate(systemMail.getUserGuid()) : null)
                .domain(domainService.findOrCreate(systemMail.getDomainName()))
                .attachmentName(systemMail.getAttachmentName())
                .attachmentData(systemMail.getAttachmentData())
                .build();

        return emailRepository.save(email);
    }
}
