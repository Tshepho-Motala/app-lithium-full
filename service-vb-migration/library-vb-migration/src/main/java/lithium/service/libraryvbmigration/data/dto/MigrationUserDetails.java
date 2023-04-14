package lithium.service.libraryvbmigration.data.dto;

import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MigrationUserDetails {
    private @Valid MigrationPlayerBasic playerBasic;
    private MigrationCredential migrationCredential;
}
