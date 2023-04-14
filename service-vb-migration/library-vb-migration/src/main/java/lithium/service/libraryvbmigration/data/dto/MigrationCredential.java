package lithium.service.libraryvbmigration.data.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MigrationCredential {
  private long id;
  private String salt;
  private String hashedPassword;
  private short hashingAlgorithm;
  private String username;
  private String securityQuestion;
  private String securityQuestionAnswer;
  private String playerGuid;
  private String customerId;
}
