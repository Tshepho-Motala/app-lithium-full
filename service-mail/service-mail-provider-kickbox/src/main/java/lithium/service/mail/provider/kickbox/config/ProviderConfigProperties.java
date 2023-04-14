package lithium.service.mail.provider.kickbox.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 *
 */
@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum ProviderConfigProperties {
  API_KEY("api_key"),
  URL("kickbox_url");

  @Getter
  private final String value;

}
