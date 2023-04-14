package lithium.service.user.client.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * User link types
 *
 * @version 1.0
 * @since 2020-10-10
 */

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum UserLinkTypes {
  CROSS_DOMAIN_LINK("CROSS_DOMAIN_LINK", true, "Users are directionally linked within an ecosystem"),
  DELETED_ROOT_DOMAIN_LINK("DELETED_ROOT_DOMAIN_LINK", false, "Users who have deleted their root ecosystem domain user"),
  SIMILAR_DETAILS("SIMILAR_DETAILS", true, "Users have similar details"),
  SUSPECTED_FRAUD("SUSPECTED_FRAUD", false, "Users are suspected of being fraudulent"), //TODO: Is this relevant?
  DUPLICATE_ACCOUNT("DUPLICATE_ACCOUNT", true, "User has multiple accounts"); //TODO: Is this relevant?

  @Getter
  @Setter
  @Accessors(fluent = true)
  private String code;

  @Getter
  @Setter
  @Accessors(fluent = true)
  private Boolean linkDirectionSensitive;

  @Getter
  @Setter
  @Accessors(fluent = true)
  private String description;

  @JsonCreator
  public static UserLinkTypes fromCode(String code) {
    for (UserLinkTypes ult : UserLinkTypes.values()) {
      if (ult.code.equalsIgnoreCase(code)) {
        return ult;
      }
    }
    return null;
  }
}
