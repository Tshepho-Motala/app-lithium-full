package lithium.service.migration.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataRequest {

  private long fromRow;
  private long toRow;
  private long numberOfRows;
  private String query;

}
