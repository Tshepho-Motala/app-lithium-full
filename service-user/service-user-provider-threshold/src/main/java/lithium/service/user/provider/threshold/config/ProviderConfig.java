package lithium.service.user.provider.threshold.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderConfig {
  private boolean enabled;
  private String name;
  private String extremePushApiUrl;
  private String extremePushAppToken;
}
