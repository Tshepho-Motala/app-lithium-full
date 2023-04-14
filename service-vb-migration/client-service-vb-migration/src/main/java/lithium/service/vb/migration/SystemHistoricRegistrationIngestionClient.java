package lithium.service.vb.migration;

import lithium.service.Response;

import lithium.service.libraryvbmigration.data.dto.MigrationCredential;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "service-vb-migration")
public interface SystemHistoricRegistrationIngestionClient {

  @PostMapping(value = "/system/historic-registration-ingestion/find-credential-by-guid/{playerGuid}")
  Response<MigrationCredential> findPlayerByPlayerGuid(
      @PathVariable("playerGuid") String playerGuid);

  @PostMapping(value = "/system/historic-registration-ingestion/find-credential-by-customer-id/{customerId}")
  Response<MigrationCredential> findPlayerByCustomerId(
      @PathVariable("customerId") String customerId);

  @PostMapping(value = "/system/historic-registration-ingestion/find-credential-by-username/{username}")
  Response<MigrationCredential> findPlayerByUsername(@PathVariable("username") String username);
}
