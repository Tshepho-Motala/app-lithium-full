package lithium.service.casino.client.data;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
public class MigrationGames implements Serializable {
  private static final long serialVersionUID = 1111752512452023028L;
  private String currencyCode;
  private String domainName;
  private String providerGuid;
  private String gameGuid;
}
