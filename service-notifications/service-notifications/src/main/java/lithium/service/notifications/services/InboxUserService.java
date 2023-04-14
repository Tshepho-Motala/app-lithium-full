package lithium.service.notifications.services;

import lithium.service.notifications.data.entities.Inbox;
import lithium.service.notifications.data.entities.InboxUser;
import lithium.service.notifications.data.entities.User;
import lithium.service.notifications.data.repositories.InboxRepository;
import lithium.service.notifications.data.repositories.InboxUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Slf4j
public class InboxUserService {
    @Autowired private InboxUserRepository inboxUserRepository;


    public InboxUser findByUserLock(User user) {
        return inboxUserRepository.findByUserAlwaysLock(user);
    }

    @Cacheable(value = "lithium.service.notifications.services.inbox-user-service.find-by-user", key = "#user.guid")
    public InboxUser findByUser(User user) {
        return inboxUserRepository.findByUser(user);
    }

    @Transactional
    public void updateSummaryFromInbox(Inbox inbox, boolean read) {

        log.debug("Updating inbox summary for user " + inbox.getUser().getGuid(), inbox);

        InboxUser inboxUser = findByUserLock(inbox.getUser());

        if(Objects.isNull(inboxUser)) {
            inboxUser = InboxUser.builder()
                    .user(inbox.getUser())
                    .ctaCount(0)
                    .readCount(0)
                    .unreadCount(0)
                    .build();
        }

        log.debug("Inbox Summary before update, inboxUser:%s", inboxUser, inboxUser);

        int readCount = inboxUser.getReadCount();
        int ctaCount = inboxUser.getCtaCount();
        int unreadCount = inboxUser.getUnreadCount();

        if(read) {
            readCount = Math.max(0, readCount + 1);
            unreadCount = Math.max(0, unreadCount - 1);

            if(inbox.getCta()) {
                ctaCount = Math.max(0, ctaCount - 1);
            }
        }
        else {
            unreadCount = Math.max(0, unreadCount + 1);

            if(inbox.getCta()) {
                ctaCount = Math.max(0, ctaCount + 1);
            }
        }

        inboxUser.setCtaCount(ctaCount);
        inboxUser.setReadCount(readCount);
        inboxUser.setUnreadCount(unreadCount);

        log.debug("Inbox Summary after update, inboxUser:%s", inboxUser, inboxUser);

        inboxUserRepository.save(inboxUser);
    }
}
