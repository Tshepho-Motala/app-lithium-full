package lithium.service.migration.service.user;

import com.hazelcast.core.HazelcastInstance;
import java.util.Optional;
import lithium.exceptions.Status404UserNotFoundException;
import lithium.service.libraryvbmigration.data.entities.MigrationCredential;
import lithium.service.libraryvbmigration.data.entities.MigrationExceptionRecord;
import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lithium.service.migration.repo.MigrationCredentialRepository;
import lithium.service.migration.repo.MigrationExceptionRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MigrationCredentialService {

  private final MigrationCredentialRepository migrationCredentialRepository;
  private final MigrationExceptionRecordRepository migrationExceptionRecordRepository;
  private final ModelMapper modelMapper;

  private final HazelcastInstance hazelcast;

  public lithium.service.libraryvbmigration.data.dto.MigrationCredential findPlayerCredentialByGuid(String playerGuid) {
    return migrationCredentialRepository.findByPlayerGuid(playerGuid).map(migrationCredential -> modelMapper.map(migrationCredential,
        lithium.service.libraryvbmigration.data.dto.MigrationCredential.class)).orElse(null);
  }

  public lithium.service.libraryvbmigration.data.dto.MigrationCredential findPlayerCredentialByCustomerId(String customerId) {
    return migrationCredentialRepository.findByCustomerId(customerId).map(migrationCredential -> modelMapper.map(migrationCredential,
        lithium.service.libraryvbmigration.data.dto.MigrationCredential.class)).orElse(null);
  }

  public lithium.service.libraryvbmigration.data.dto.MigrationCredential findPlayerCredentialByUsername(String username) {
    return migrationCredentialRepository.findByUsername(username).map(migrationCredential -> modelMapper.map(migrationCredential,
        lithium.service.libraryvbmigration.data.dto.MigrationCredential.class)).orElse(null);
  }


  public void saveMigrationException(lithium.service.libraryvbmigration.data.dto.MigrationExceptionRecord exceptionRecord) {
    log.error("VB Migration Que Argument exception: {}", exceptionRecord.getExceptionMessage());
    migrationExceptionRecordRepository.save(
        MigrationExceptionRecord.builder()
            .customerId(exceptionRecord.getCustomerId())
            .migrationType(MigrationType.fromType(exceptionRecord.getMigrationType()))
            .exceptionMessage(exceptionRecord.getExceptionMessage())
            .requestJson(exceptionRecord.getRequestJson())
            .build()
    );
  }


  public void saveMigrationCredential(lithium.service.libraryvbmigration.data.dto.MigrationCredential migrationUserDetails) {
    log.debug("VB Credentials: {}", migrationUserDetails);
    cachePut(migrationUserDetails.getCustomerId(), migrationUserDetails.getPlayerGuid());
    migrationCredentialRepository.save(modelMapper.map(migrationUserDetails, MigrationCredential.class));
  }

  public String cachePut(String customerId, String playerGuid) {
    hazelcast.getMap("lithium.service.migration.credentials").put(customerId, playerGuid);
    log.trace("CustomerID: {} and Player Guid in cache: {}", customerId, playerGuid);
    return playerGuid;
  }

  public String cacheGet(String customerId) {
    String playerGuid = (String) hazelcast.getMap("lithium.service.migration.credentials").get(customerId);

    if (ObjectUtils.isEmpty(playerGuid)) {
      log.trace("No PlayerGuid in cache for CustomerID: {}", customerId);
      return null;
    }

    log.trace("CustomerID: {} and Player Guid in cache: {}", customerId, playerGuid);
    return playerGuid;
  }

  public String getPlayerGuidByCustomerId(String customerId) {

    String playerGuid = cacheGet(customerId);
    if (!ObjectUtils.isEmpty(playerGuid)) {
      return playerGuid;
    }
    Optional<MigrationCredential> credential = migrationCredentialRepository.findByCustomerId(customerId);

    if (credential.isPresent()) {
      return cachePut(customerId, credential.get().getPlayerGuid());
    }

    throw new Status404UserNotFoundException("CustomerID: " + customerId + " was not registered");
  }
}
