package lithium.service.user.client.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum BiometricsStatus {
  NOT_REQUIRED(0, "not_required"),
  REQUIRED_PHOTO(1, "required_photo"),
  REQUIRED_VIDEO(2, "required_video"),
  PENDING(3, "pending"),
  PASSED(4, "passed"),
  FAILED(5, "failed");

  @Getter
  @Accessors(fluent = true)
  private final int code;

  @Accessors(fluent = true)
  private final String value;

  @JsonValue
  public String getValue() {
      return value;
  }

  @JsonCreator
  public static BiometricsStatus fromValue(String value) throws IllegalArgumentException {
    for (BiometricsStatus bs : BiometricsStatus.values()) {
      if (bs.value.equalsIgnoreCase(value)) {
        return bs;
      }
    }
    throw new IllegalArgumentException("Can't resolve Biometrics Status from value: " + value);
  }

  public static BiometricsStatus fromCode(int code) throws IllegalArgumentException {
    for (BiometricsStatus bs : BiometricsStatus.values()) {
      if (bs.code == code) {
        return bs;
      }
    }
    throw new IllegalArgumentException("Can't resolve Biometrics Status from code: " + code);
  }
}
