package lithium.service.casino.service;

import lithium.exceptions.Status400BadRequestException;
import lithium.service.casino.client.data.MigrationGames;
import lithium.service.casino.data.entities.Currency;
import lithium.service.casino.data.entities.Domain;
import lithium.service.casino.data.entities.Game;
import lithium.service.casino.data.entities.Provider;
import lithium.service.casino.data.repositories.CurrencyRepository;
import lithium.service.casino.data.repositories.DomainRepository;
import lithium.service.casino.data.repositories.GameRepository;
import lithium.service.casino.data.repositories.ProviderRepository;
import lithium.service.casino.stream.MigrationExceptionOutputQueue;
import lithium.service.libraryvbmigration.data.dto.MigrationExceptionRecord;
import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableBinding(MigrationExceptionOutputQueue.class)
public class GameService {

  private final ProviderRepository providerRepository;
  private final CurrencyRepository currencyRepository;
  private final DomainRepository domainRepository;
  private final GameCacheService gameCacheService;
  private final GameRepository gameRepository;
  private final MigrationExceptionOutputQueue migrationExceptionOutputQueue;

  @Retryable(backoff = @Backoff(delay = 500), maxAttempts = 10)
  public void findOrCreate(MigrationGames data) {
    try {
      String gameName = gameCacheService.findGameNameByGuidAndDomain(data.getGameGuid().split("/")[1], data.getDomainName());

      if (ObjectUtils.isEmpty(gameName) && gameName.equals("Freespin Bonus")){
        throw new Status400BadRequestException("Could not find game" + data.getGameGuid());
      }

      Domain domain = domainRepository.findOrCreateByName(data.getDomainName(), Domain::new);
      currencyRepository.findOrCreateByCode(data.getCurrencyCode(), Currency::new);
      providerRepository.findOrCreateByGuid(data.getProviderGuid(),
          () -> Provider.builder().domain(domain)
              .build());
      gameRepository.findOrCreateByGuid(data.getGameGuid(), Game::new);
    } catch (Exception e) {
      log.warn("{} exception: {}", MigrationType.CASINO_GAMES_MIGRATION, e.getMessage(), e);
      migrationExceptionOutputQueue.migrationExceptionOutputQueue()
              .send(MessageBuilder
                      .withPayload(MigrationExceptionRecord.builder()
                              .customerId(data.getGameGuid())
                              .migrationType(MigrationType.CASINO_GAMES_MIGRATION.type())
                              .exceptionMessage(e.getMessage())
                              .requestJson(data.toString())
                              .build())
                      .build());
      throw new Status400BadRequestException(e.getMessage(), e.getStackTrace());
    }
  }
}
