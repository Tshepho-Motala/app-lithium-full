package lithium.service.mail.provider.kickbox.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KickboxResponse {
  private String result;
  private String reason;
  private boolean success;
  private String email;
  private String message;
  private boolean disposable;
}
