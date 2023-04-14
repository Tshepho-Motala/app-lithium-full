package lithium.service.user.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AuthRequest {
  private String domain;
  private String username;
  private String password;
  private String ipAddress;
  private String userAgent;
  private Map<String, String> extraParameters;
  private String locale;
}
