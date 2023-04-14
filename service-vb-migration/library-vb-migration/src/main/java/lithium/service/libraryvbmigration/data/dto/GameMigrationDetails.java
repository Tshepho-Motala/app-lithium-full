package lithium.service.libraryvbmigration.data.dto;

import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameMigrationDetails {
  private String domainName;
  private String customerId;
  private String providerGuid;
  private String gameName;
  private String commercialName;
  private String providerGameId;
  private String description;
  private BigDecimal rtp;
  private Date dateNow;
  private String currencyCode;
}
