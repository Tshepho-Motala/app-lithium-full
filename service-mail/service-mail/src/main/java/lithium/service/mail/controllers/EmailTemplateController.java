package lithium.service.mail.controllers;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.mail.data.entities.EmailTemplate;
import lithium.service.mail.data.entities.EmailTemplateRevision;
import lithium.service.mail.data.entities.User;
import lithium.service.mail.data.repositories.EmailTemplateRepository;
import lithium.service.mail.data.repositories.EmailTemplateRevisionRepository;
import lithium.service.mail.exceptions.MailTemplateUserIsNotOpenException;
import lithium.service.mail.exceptions.MailToIsEmptyException;
import lithium.service.mail.services.MailService;
import lithium.service.mail.services.PlaceholderService;
import lithium.service.mail.services.UserService;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.DomainValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/emailtemplate/{id}")
public class EmailTemplateController {

    @Autowired
    UserService userService;
    @Autowired
    EmailTemplateRepository repository;
    @Autowired
    EmailTemplateRevisionRepository revisionRepository;
    @Autowired
    ChangeLogService changeLogService;
    @Autowired
    MailService mailService;
    @Autowired
    PlaceholderService placeholderService;

    @PostMapping("/test/{recipientEmail:.+}")
    public Response<Boolean> testEmailTemplate(
            @PathVariable("id") EmailTemplate template,
            @PathVariable("recipientEmail") String recipientEmail,
            @RequestParam(name = "transactionId", required = false) Long transactionId,
            LithiumTokenUtil token) throws Exception {
        try {
            DomainValidationUtil.validate(template.getDomain().getName(), "EMAIL_TEMPLATES_VIEW", token);
            mailService.save(
                    template,
                    token.guid(),
                    template.getDomain().getName(),
                    recipientEmail,
                    1,
                    token.guid(),
                    placeholderService.buildPlaceHolders(template, null, transactionId).getData(),
                    null,
                    null);
            return Response.<Boolean>builder().data(true).status(Status.OK).build();
        } catch (MailTemplateUserIsNotOpenException | MailToIsEmptyException e) {
            return Response.<Boolean>builder()
                    .data(false)
                    .status(Status.BAD_REQUEST)
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.<Boolean>builder()
                    .data(false)
                    .status(Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }

    @GetMapping
    public Response<EmailTemplate> get(@PathVariable("id") EmailTemplate t, LithiumTokenUtil tokenUtil) {
        try {
            DomainValidationUtil.validate(t.getDomain().getName(), "EMAIL_TEMPLATES_VIEW", tokenUtil);
            return Response.<EmailTemplate>builder().data(t).status(Status.OK).build();
        } catch (Exception e) {
            return Response.<EmailTemplate>builder().status(Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
        }
    }

    @PostMapping
    public Response<EmailTemplate> save(@RequestBody EmailTemplate t, LithiumTokenUtil tokenUtil) throws Exception {
        try {
            EmailTemplate current = repository.findOne(t.getId());
            DomainValidationUtil.validate(current.getDomain().getName(), "EMAIL_TEMPLATES_VIEW", tokenUtil);
            EmailTemplateRevision currentRevision = revisionRepository.findOne(t.getCurrent().getId());
            List<ChangeLogFieldChange> clfc = changeLogService.copy(t.getEdit(), currentRevision, new String[]{"subject", "body"});
            if (current.getEnabled() != t.getEnabled()) {
                ChangeLogFieldChange fieldEnabledChange =
                        ChangeLogFieldChange.builder()
                                .field("enabled")
                                .fromValue(current.getEnabled().toString())
                                .toValue(t.getEnabled().toString())
                                .build();
                clfc.add(fieldEnabledChange);
            }
            if (current.isUserOpenStatusOnly() != t.isUserOpenStatusOnly()) {
                ChangeLogFieldChange fieldEnabledChange =
                        ChangeLogFieldChange.builder()
                                .field("userOpenStatusOnly")
                                .fromValue(String.valueOf(current.isUserOpenStatusOnly()))
                                .toValue(String.valueOf(t.isUserOpenStatusOnly()))
                                .build();
                clfc.add(fieldEnabledChange);
            }
            if (!current.getName().equals(t.getName())) {
                ChangeLogFieldChange fieldChangeName =
                        ChangeLogFieldChange.builder()
                                .field("name")
                                .fromValue(current.getName())
                                .toValue(t.getName())
                                .build();
                clfc.add(fieldChangeName);
            }
            String regex = "\\<.*?\\>";
            for (ChangeLogFieldChange c : clfc) {
                if (c.getFromValue() != null) {
                    c.setFromValue(c.getFromValue().replaceAll(regex, ""));
                }
                if (c.getToValue() != null) {
                    c.setToValue(c.getToValue().replaceAll(regex, ""));
                }
            }
            t.getEdit().setEmailTemplate(t);
            revisionRepository.save(t.getEdit());
            t.setCurrent(t.getEdit());
            t.setEdit(null);
            t.setEditBy(null);
            t.setEditStartedOn(null);
            t.setUpdatedOn(new Date());
            t = repository.save(t);
            changeLogService.registerChangesWithDomain("emailtemplate", "edit", t.getId(), tokenUtil.guid(), null, null, clfc, Category.SUPPORT, SubCategory.COMMUNICATIONS, 0, current.getDomain().getName());
            return Response.<EmailTemplate>builder().data(t).status(Status.OK).build();
        } catch (Exception e) {
            return Response.<EmailTemplate>builder().status(Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
        }
    }

    @PostMapping("/continueLater")
    public Response<EmailTemplate> continueLater(@RequestBody EmailTemplate t, LithiumTokenUtil tokenUtil) {
        try {
            EmailTemplate current = repository.findOne(t.getId());
            DomainValidationUtil.validate(current.getDomain().getName(), "EMAIL_TEMPLATES_VIEW", tokenUtil);
            t.getEdit().setEmailTemplate(t);
            EmailTemplateRevision edit = revisionRepository.save(t.getEdit());
            current.setEdit(edit);
            current.setEditStartedOn(new Date());
            current.setEditBy(userService.findOrCreate(tokenUtil.guid()));
            current = repository.save(current);
            return Response.<EmailTemplate>builder().data(current).status(Status.OK).build();
        } catch (Exception e) {
            return Response.<EmailTemplate>builder().status(Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
        }
    }

    @PostMapping("/cancelEdit")
    public Response<EmailTemplate> cancelEdit(@RequestBody EmailTemplate t, LithiumTokenUtil tokenUtil) {
        try {
            EmailTemplate current = repository.findOne(t.getId());
            DomainValidationUtil.validate(current.getDomain().getName(), "EMAIL_TEMPLATES_VIEW", tokenUtil);
            current.setEdit(null);
            current.setEditBy(null);
            current.setEditStartedOn(null);
            current = repository.save(current);
            return Response.<EmailTemplate>builder().data(current).status(Status.OK).build();
        } catch (Exception e) {
            return Response.<EmailTemplate>builder().status(Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
        }
    }

    @GetMapping("/edit")
    @Transactional
    public Response<EmailTemplate> edit(@PathVariable("id") EmailTemplate t, LithiumTokenUtil tokenUtil) {
        try {
            DomainValidationUtil.validate(t.getDomain().getName(), "EMAIL_TEMPLATES_VIEW", tokenUtil);
            if (t.getEdit() == null) {
                User user = userService.findOrCreate(tokenUtil.guid());
                EmailTemplateRevision revision = EmailTemplateRevision.builder()
                        .emailTemplate(t)
                        .subject(t.getCurrent().getSubject())
                        .body(t.getCurrent().getBody())
                        .emailFrom(t.getCurrent().getEmailFrom())
                        .build();
                revisionRepository.save(revision);

                t.setEdit(revision);
                t.setEditBy(user);
                t.setEditStartedOn(new Date());
                t = repository.save(t);
            }
            return Response.<EmailTemplate>builder().data(t).status(Status.OK).build();
        } catch (Exception e) {
            return Response.<EmailTemplate>builder().status(Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
        }
    }

    @GetMapping(value = "/changelogs")
    public @ResponseBody Response<ChangeLogs> changeLogs(@PathVariable("id") EmailTemplate t, @RequestParam int p, LithiumTokenUtil tokenUtil) throws Exception {
        DomainValidationUtil.validate(t.getDomain().getName(), "EMAIL_TEMPLATES_VIEW", tokenUtil);
        return changeLogService.list("emailtemplate", t.getId(), p);
    }
}
