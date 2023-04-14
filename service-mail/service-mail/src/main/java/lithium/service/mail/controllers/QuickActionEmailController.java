package lithium.service.mail.controllers;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.service.Response;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.mail.client.objects.SystemEmailData;
import lithium.service.mail.data.entities.EmailTemplate;
import lithium.service.mail.exceptions.MailTemplateUserIsNotOpenException;
import lithium.service.mail.exceptions.MailToIsEmptyException;
import lithium.service.mail.services.MailService;
import lithium.service.mail.services.PlaceholderService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/quick-action-email")
public class QuickActionEmailController {
    @Autowired
    ChangeLogService changeLogService;
    @Autowired
    MailService mailService;
    @Autowired
    PlaceholderService placeholderService;

    @GetMapping("/{id}/get-placeholders")
    public Response<Set<Placeholder>> getPlaceHolders(
            @PathVariable("id") EmailTemplate template,
            @RequestParam(name = "recipientGuid", required = false) String recipientGuid,
            @RequestParam(name = "transactionId", required = false) Long transactionId
    ) {
        return placeholderService.buildPlaceHolders(template, recipientGuid, transactionId);
    }

    @PostMapping("/{id}/send-template")
    public Response<Boolean> sendEmailTemplate(
            @PathVariable("id") EmailTemplate template,
            @RequestParam(name = "recipientGuid") String recipientGuid,
            @RequestParam(name = "recipientId") Long recipientId,
            @RequestParam(name = "recipientEmail") String recipientEmail,
            @RequestParam(name = "transactionId", required = false) Long transactionId,
            LithiumTokenUtil token) {
        try {
            mailService.save(
                    template,
                    token.guid(),
                    template.getDomain().getName(),
                    recipientEmail,
                    1,
                    recipientGuid,
                    placeholderService.buildPlaceHolders(template, recipientGuid, transactionId).getData(),
                    null, null);

            String comment = "Email Template:" + template.getName() + " with lang: " + template.getLang() + " and version:" + template.getVersion() + " was sent";
            log.debug(comment);
            changeLogService.registerChangesWithDomain("user.send_template", "create", recipientId, token.guid(), comment, null, null, Category.SUPPORT, SubCategory.COMMUNICATIONS, 0, token.domainName());

            return Response.<Boolean>builder()
                    .data(true)
                    .status(Response.Status.OK)
                    .build();
        } catch (MailTemplateUserIsNotOpenException | MailToIsEmptyException e) {
            return Response.<Boolean>builder()
                    .data(false)
                    .status(Response.Status.BAD_REQUEST)
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.<Boolean>builder()
                    .data(false)
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }

    @PostMapping("/{id}/send-email/{user-id}")
    public Response<Boolean> sendEmail(
            @PathVariable("id") EmailTemplate template,
            @RequestBody SystemEmailData systemEmailData,
            @PathVariable("user-id") Long userId,
            LithiumTokenUtil token) {
        try {
            log.debug("Quick send email SystemEmailData retrieved:" + systemEmailData.toString());
            mailService.saveEmail(systemEmailData, token.guid());

            String comment = "Email Template:" + template.getName() + " with lang: " + template.getLang() + " and version:" + template.getVersion() + " was sent after manual editing";


            log.debug(comment);
            changeLogService.registerChangesWithDomain("user.send_template", "create", userId, token.guid(), comment, null, null, Category.SUPPORT, SubCategory.COMMUNICATIONS, 0, token.domainName());

            return Response.<Boolean>builder().data(true).build();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.<Boolean>builder()
                    .data(false)
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }
}
