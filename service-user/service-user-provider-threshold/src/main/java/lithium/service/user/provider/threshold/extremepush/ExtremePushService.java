package lithium.service.user.provider.threshold.extremepush;

import lithium.service.user.provider.threshold.extremepush.dto.ThresholdMessage;
import org.springframework.http.ResponseEntity;

public interface ExtremePushService {

  ResponseEntity<?> sendMessage(ThresholdMessage message);




}
