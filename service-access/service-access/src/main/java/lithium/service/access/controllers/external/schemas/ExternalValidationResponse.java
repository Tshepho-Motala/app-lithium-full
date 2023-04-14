package lithium.service.access.controllers.external.schemas;

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
public class ExternalValidationResponse {
  private Boolean result;
  private Map<String, String> data;
  private String message;
  private String rejectReason;
}
