package lithium.service.notifications.controllers.frontend;

import java.util.Map;
import lithium.service.notifications.client.exceptions.Status404InboxItemNotFoundException;
import lithium.service.notifications.client.objects.AcknowledgementRequest;
import lithium.service.notifications.client.objects.FEInbox;
import lithium.service.notifications.data.entities.Inbox;
import lithium.service.notifications.data.entities.InboxLabelValue;
import lithium.service.notifications.data.entities.NotificationType;
import lithium.service.notifications.data.objects.UserInboxQueryParams;
import lithium.service.notifications.services.InboxService;
import lithium.tokens.LithiumTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/frontend/inbox")
public class FrontendInboxController {
    @Autowired
    private InboxService inboxService;

    @RequestMapping(value = "/read", method = RequestMethod.GET)
    public List<FEInbox> read(
            @RequestParam(name = "channels", defaultValue = "pull", required = false) String channels,
            @RequestParam(name = "locale", defaultValue = "en", required = false) String locale,
            @RequestParam(name = "cta", required = false) Boolean cta,
            @RequestParam(name = "type", required = false) String type,
            LithiumTokenUtil util ) {

        UserInboxQueryParams queryParams = UserInboxQueryParams.builder()
                .cta(cta)
                .type(type)
                .channels(channels)
                .locale(locale)
                .userGuid(util.guid())
                .read(true)
                .build();

        return  inboxService.findUserInbox(queryParams)
                .stream()
                .map(this::covertToFEInbox)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/unread", method = RequestMethod.GET)
    public List<FEInbox> unread(
            @RequestParam(name = "channels", defaultValue = "pull", required = false) String channels,
            @RequestParam(name = "locale", defaultValue = "en", required = false) String locale,
            @RequestParam(name = "cta", required = false) Boolean cta,
            @RequestParam(name = "type", required = false) String type,
            LithiumTokenUtil util) {

        UserInboxQueryParams queryParams = UserInboxQueryParams.builder()
                .cta(cta)
                .type(type)
                .channels(channels)
                .locale(locale)
                .userGuid(util.guid())
                .read(false)
                .build();

        return  inboxService.findUserInbox(queryParams)
                .stream()
                .map(this::covertToFEInbox)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/{inboxId}/markAsRead", method = RequestMethod.POST)
    public FEInbox markAsRead(@RequestBody AcknowledgementRequest acknowledgementRequest,
                              @PathVariable("inboxId") Long inboxId,
                              @RequestParam(name = "locale", defaultValue = "en", required = false) String locale,
                              LithiumTokenUtil util ) throws Status404InboxItemNotFoundException {
        return covertToFEInbox(inboxService.read(inboxId, acknowledgementRequest.getReplyMessage(), locale, util));
    }

    private FEInbox covertToFEInbox(Inbox inbox) {
        String defaultType = lithium.service.notifications.enums.NotificationType.DEFAULT.getType();
        return FEInbox.builder()
                .inboxId(inbox.getId())
                .message(inbox.getMessage())
                .createdDate(inbox.getCreatedDate())
                .lastReadDate(inbox.getLastReadDate())
                .read(inbox.getRead())
                .readDate(inbox.getReadDate())
                .sentDate(inbox.getSentDate())
                .metaData((inbox.getMetaData()!=null&&!inbox.getMetaData().isEmpty())?inbox.getMetaData().stream().collect(Collectors.toMap(ilv -> ilv.getLabel().getName(), ilv -> ilv.getLabelValue().getValue())):null)
                .type((Optional.ofNullable(inbox.getNotification().getNotificationType()).orElse(NotificationType.builder().name(defaultType).build())).getName().toLowerCase())
                .build();
    }
}
