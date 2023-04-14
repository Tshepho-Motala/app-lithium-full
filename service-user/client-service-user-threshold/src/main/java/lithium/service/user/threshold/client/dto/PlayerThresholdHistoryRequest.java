package lithium.service.user.threshold.client.dto;

import java.io.Serializable;
import java.util.Date;
import lithium.service.client.datatable.DataTableRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerThresholdHistoryRequest implements Serializable {

  private String playerGuid;
  private String domainName;
  private String[] typeName;
  private Integer granularity;
  private Date dateStart;
  private Date dateEnd;
  private DataTableRequest tableRequest;
}
