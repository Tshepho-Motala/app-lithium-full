package lithium.service.promo.client.objects;

import java.io.Serial;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
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
public class UserCategoryBO implements Serializable {

  @Serial
  private static final long serialVersionUID = 2079962856333634140L;
  private Long id;
  private Long userCategoryId;

  @NotNull
  private String type;
}
