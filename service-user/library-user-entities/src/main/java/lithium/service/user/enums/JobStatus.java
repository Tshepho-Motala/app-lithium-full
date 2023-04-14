package lithium.service.user.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum JobStatus {
  CREATED(0), PROCESSING(1), COMPLETE(2), FAILED(3);

  @Setter
  @Accessors(fluent = true)
  private int status;


  @JsonValue
  public Integer status() {
    return status;
  }

  @JsonCreator
  public static JobStatus fromStatus(int status) {
    for (JobStatus js : JobStatus.values()) {
      if (js.status == status) {
        return js;
      }
    }
    return null;
  }
}
