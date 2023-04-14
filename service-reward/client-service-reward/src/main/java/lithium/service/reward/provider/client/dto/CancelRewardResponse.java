package lithium.service.reward.provider.client.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import lithium.service.Response.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CancelRewardResponse implements Serializable {

  @Serial
  private static final long serialVersionUID = 7844778905914125287L;

  private String code;
  private String result;
  private String description;
  private Integer errorCode;

  public boolean isSuccess() {
    return Objects.equals(getCode(), Status.OK.id() + "");
  }
}
