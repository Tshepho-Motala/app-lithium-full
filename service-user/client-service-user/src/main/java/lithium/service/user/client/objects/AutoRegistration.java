package lithium.service.user.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoRegistration {
    Long userId;
    String ipAddress;
    String userAgent;
    String deviceId;
    String password;
    boolean channelOptOut;
}
