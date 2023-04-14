package lithium.service.libraryvbmigration.data.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MigrationExceptionRecord {
  private long id;
  private String requestJson;
  private String exceptionMessage;
  private String customerId;
  private String migrationType;
}
