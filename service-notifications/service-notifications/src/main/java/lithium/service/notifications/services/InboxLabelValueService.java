package lithium.service.notifications.services;

import java.util.HashMap;
import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.exceptions.Status400BadRequestException;
import lithium.service.notifications.data.entities.Inbox;
import lithium.service.notifications.data.entities.InboxLabelValue;
import lithium.service.notifications.data.entities.LabelValue;
import lithium.service.notifications.data.repositories.DomainRepository;
import lithium.service.notifications.data.repositories.InboxLabelValueRepository;
import lithium.service.notifications.data.repositories.InboxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.util.ObjectUtils;

@Service
public class InboxLabelValueService {

  @Autowired
  LabelValueService labelValueService;
  @Autowired
  InboxLabelValueRepository inboxLabelValueRepository;
  @Autowired
  InboxRepository inboxRepository;
  @Autowired
  DomainRepository domainRepository;

  @Transactional( rollbackOn = Exception.class )
  @Retryable( backoff = @Backoff( delay = 500 ), maxAttempts = 10, exclude = {NotRetryableErrorCodeException.class}, include = Exception.class )
  public Inbox save(Inbox inbox, Map<String, String> metaData)
  throws Status400BadRequestException
  {
    List<InboxLabelValue> lvList = new ArrayList<>();
    metaData = ObjectUtils.isEmpty(metaData) ? new HashMap<>() : metaData;

    for(Map.Entry<String, String> data: metaData.entrySet()) {
      LabelValue lv = labelValueService.findOrCreate(data.getKey(), data.getValue());

      InboxLabelValue inboxLabelValue = InboxLabelValue.builder().
              labelValue(lv).
              label(lv.getLabel()).
              inbox(inbox)
              .build();
      lvList.add(inboxLabelValueRepository.save(inboxLabelValue));
    }

    inbox.setMetaData(lvList);
    // Just making absolutely sure that the cache is evicted once all settings have been written. There could be
    // inconsistencies on the cached object if the cache has already been set, but all settings haven't persisted
    // yet.
    inbox = inboxRepository.save(inbox);
    return inbox;
  }
}

