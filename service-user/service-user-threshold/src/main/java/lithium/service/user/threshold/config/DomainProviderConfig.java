package lithium.service.user.threshold.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainProviderConfig {

  private boolean enabled;
  private String name;
  private String extremePushApiUrl;
  private String extremePushAppToken;
}
