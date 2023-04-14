package lithium.service.libraryvbmigration.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import lithium.service.user.client.objects.PlayerBasic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MigrationPlayerBasic extends PlayerBasic {
  private LocalDateTime createdDate;
  private boolean residentialAddressVerified;
  private boolean ageVerified;
  private boolean testUser;
  private int passwordHashAlgorithm;
  private String passwordSalt;
}
