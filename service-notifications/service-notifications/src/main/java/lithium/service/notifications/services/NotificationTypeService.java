package lithium.service.notifications.services;

import lithium.service.notifications.data.entities.NotificationType;
import lithium.service.notifications.data.repositories.NotificationTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationTypeService {
    private final NotificationTypeRepository notificationTypeRepository;

    public void register(String type) {
        try {
            Optional<NotificationType> result = notificationTypeRepository.findFirstByName(type);

            if (!result.isPresent()) {
                notificationTypeRepository.save(NotificationType.builder()
                        .name(type.toLowerCase())
                        .build());
            } else {
                log.debug(String.format("NotificationType %s has already been added, Skipping", type));
            }
        }
        catch (Exception e) {
            log.error(String.format("An error occurred while adding notification type %s", type), e);
        }

    }
}
