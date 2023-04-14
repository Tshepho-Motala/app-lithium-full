package lithium.service.access.controllers.external.schemas;

import lithium.service.user.client.objects.AddressBasic;
import lithium.service.user.client.objects.PlayerBasic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.Map;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ExternalValidationRequest {
  private String firstName;
  private String lastNamePrefix;
  private String lastName;
  private String placeOfBirth;
  private Integer dobYear;
  private Integer dobMonth;
  private Integer dobDay;
  private String email;
  private Map<String, String> additionalData;
  private String sha256;
}
