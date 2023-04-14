package lithium.service.user.mass.action.objects;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserValidation {

  private Long fileUploadId;
  private Long uploadedPlayerId;
  private String uploadedDomainName;
  private Long rowNumber;
  private Double amount;
  private boolean duplicate;
}
