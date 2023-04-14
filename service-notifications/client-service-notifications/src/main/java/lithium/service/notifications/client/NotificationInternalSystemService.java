package lithium.service.notifications.client;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.notifications.client.exceptions.Status500NotificationsInternalSystemClientException;
import lithium.service.notifications.client.objects.InboxSummary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationInternalSystemService {
    @Autowired
    private LithiumServiceClientFactory lithiumServiceClientFactory;

    public InboxSummary getInboxSummaryByUserGuid(String userGuid) {
        InboxSummary summary = InboxSummary.builder().build();

       try {
           summary =  getClient().getInboxSummary(userGuid);
       }
       catch (Exception e) {
           log.debug("Status500NotificationsInternalSystemClientException: message = " + e.getMessage());
       }

       return summary;
    }

    private NotificationsClient getClient() throws Status500NotificationsInternalSystemClientException {
        try {
            return lithiumServiceClientFactory.target(NotificationsClient.class, true);
        }catch (LithiumServiceClientFactoryException e){
            throw new Status500NotificationsInternalSystemClientException("NotificationInternalSystemService#getClient failed, reason: "+ e.getMessage());
        }
    }
}
