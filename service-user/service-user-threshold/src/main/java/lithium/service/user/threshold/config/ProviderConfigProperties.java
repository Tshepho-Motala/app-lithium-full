package lithium.service.user.threshold.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum ProviderConfigProperties {
  ENABLED("enabled"),
  NAME("name"),
  EXTREME_PUSH_API_URL("extremePushApiUrl"),
  EXTREME_PUSH_APP_TOKEN("extremePushAppToken");


  @Getter
  private final String value;
}
