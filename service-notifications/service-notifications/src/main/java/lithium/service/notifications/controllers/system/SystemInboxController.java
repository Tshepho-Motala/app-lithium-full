package lithium.service.notifications.controllers.system;

import lithium.service.notifications.client.objects.InboxSummary;
import lithium.service.notifications.data.entities.InboxUser;
import lithium.service.notifications.data.entities.User;
import lithium.service.notifications.data.repositories.UserRepository;
import lithium.service.notifications.services.InboxUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping(value = "/system/inbox")
public class SystemInboxController {

    @Autowired
    InboxUserService inboxUserService;

    @Autowired
    UserRepository userRepository;

    @Transactional
    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    public InboxSummary getInboxSummary(@RequestParam("userGuid") String userGuid) {
        User user = userRepository.findByGuid(userGuid);

        InboxSummary inboxSummary =  InboxSummary.builder()
                .ctaCount(0)
                .readCount(0)
                .unreadCount(0)
                .build();

        if(!Objects.isNull(user)) {
            InboxUser saveInboxUser = inboxUserService.findByUser(user);

            if(!Objects.isNull(saveInboxUser)) {
                inboxSummary.setUnreadCount(saveInboxUser.getUnreadCount());
                inboxSummary.setCtaCount(saveInboxUser.getCtaCount());
                inboxSummary.setReadCount(saveInboxUser.getReadCount());
            }
        }

        return inboxSummary;

    }
}
