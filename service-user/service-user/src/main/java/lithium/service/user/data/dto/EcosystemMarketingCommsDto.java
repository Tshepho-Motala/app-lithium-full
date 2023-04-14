package lithium.service.user.data.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EcosystemMarketingCommsDto {
  private String domainName;
  private String ecosystemRelationshipType;
  private boolean emailOptOut;
  private boolean postOptOut;
  private boolean smsOptOut;
  private boolean callOptOut;
  private boolean pushOptOut;
  private boolean leaderboardOptOut;
}
