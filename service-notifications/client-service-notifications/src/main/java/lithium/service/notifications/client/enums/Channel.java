package lithium.service.notifications.client.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum Channel {

  SMS("SMS"),
  EMAIL("EMAIL"),
  POPUP("POPUP"),
  PUSH("PUSH"),
  PULL("PULL");

  @Getter
  @Accessors(fluent = true)
  private String channelName;
}