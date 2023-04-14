package lithium.service.promo.client.objects;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DomainBO implements Serializable {

  @Serial
  private static final long serialVersionUID = 5609442627952764448L;

  @NotEmpty(message = "A domain name is required")
  private String name;

  private String timezone;
}
