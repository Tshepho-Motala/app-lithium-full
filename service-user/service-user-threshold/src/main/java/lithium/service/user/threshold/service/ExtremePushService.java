package lithium.service.user.threshold.service;

import lithium.service.user.threshold.data.dto.ExternalServiceMessage;
import org.springframework.http.ResponseEntity;

public interface ExtremePushService {

  ResponseEntity<?> sendMessage(ExternalServiceMessage message);
}
