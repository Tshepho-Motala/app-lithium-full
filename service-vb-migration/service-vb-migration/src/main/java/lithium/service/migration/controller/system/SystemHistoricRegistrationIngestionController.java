package lithium.service.migration.controller.system;

import lithium.service.Response;
import lithium.service.migration.service.user.MigrationCredentialService;
import lithium.service.vb.migration.SystemHistoricRegistrationIngestionClient;
import lithium.service.libraryvbmigration.data.dto.MigrationCredential;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/historic-registration-ingestion")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SystemHistoricRegistrationIngestionController implements SystemHistoricRegistrationIngestionClient {

  private final MigrationCredentialService migrationCredentialService;

  @PostMapping("/find-credential-by-username/{username}")
  public Response<MigrationCredential> findPlayerByUsername(@PathVariable("username") String username) {
    return Response.<MigrationCredential>builder().data(migrationCredentialService.findPlayerCredentialByUsername(username)).build();
  }

  @PostMapping("/find-credential-by-guid/{playerGuid}")
  public Response<MigrationCredential> findPlayerByPlayerGuid(@PathVariable("playerGuid") String playerGuid) {
    return Response.<MigrationCredential>builder().data(migrationCredentialService.findPlayerCredentialByGuid(playerGuid)).build();
  }

  @PostMapping("/find-credential-by-customer-id/{customerId}")
  public Response<MigrationCredential> findPlayerByCustomerId(@PathVariable("customerId") String customerId) {
    return Response.<MigrationCredential>builder().data(migrationCredentialService.findPlayerCredentialByCustomerId(customerId)).build();
  }
}
