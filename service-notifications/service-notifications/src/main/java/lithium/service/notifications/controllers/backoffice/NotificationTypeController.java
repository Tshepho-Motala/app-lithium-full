package lithium.service.notifications.controllers.backoffice;

import lithium.service.notifications.data.entities.NotificationType;
import lithium.service.notifications.data.repositories.NotificationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/backoffice/notification-types")
@RequiredArgsConstructor
public class NotificationTypeController {

    private final NotificationTypeRepository notificationTypeRepository;

    @GetMapping
    public List<NotificationType> getTypes() {
        return notificationTypeRepository.findAll();
    }
}
