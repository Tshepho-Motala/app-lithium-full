package lithium.service.promo.client.objects;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ActivityBO implements Serializable {

  @Serial
  private static final long serialVersionUID = 1778683707333976263L;
  private long id;

  @NotEmpty(message = "name is a required field")
  private String name;
}