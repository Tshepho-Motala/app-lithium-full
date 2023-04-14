package lithium.service.domain.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Ecosystem relationship types
 *
 * @version 1.0
 * @since 2020-09-30
 */

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum EcosystemRelationshipTypes {
  ECOSYSTEM_ROOT("ECOSYSTEM_ROOT"),
  ECOSYSTEM_MUTUALLY_EXCLUSIVE("ECOSYSTEM_MUTUALLY_EXCLUSIVE"),
  ECOSYSTEM_MEMBER("ECOSYSTEM_MEMBER");

  @Getter
  @Setter
  @Accessors(fluent = true)
  private String key;
//
//	@Getter
//	@Setter
//	@Accessors(fluent = true)
//	private String defaultValue;

  @JsonCreator
  public static EcosystemRelationshipTypes fromKey(String key) {
    for (EcosystemRelationshipTypes ds : EcosystemRelationshipTypes.values()) {
      if (ds.key.equalsIgnoreCase(key)) {
        return ds;
      }
    }
    return null;
  }
}
