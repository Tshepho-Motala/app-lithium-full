package lithium.service.user.data.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserJobRequest {

  @Min(5)
  private int pageSize;


  @NotNull
  @NotBlank
  private String domain;

  @Min(5)
  private int phoneLength;
}
