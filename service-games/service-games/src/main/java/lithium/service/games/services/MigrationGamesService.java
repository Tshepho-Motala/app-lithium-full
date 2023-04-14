package lithium.service.games.services;

import lithium.service.casino.client.CasinoMigrationClient;
import lithium.service.casino.client.data.MigrationGames;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.games.data.entities.Game;
import lithium.service.libraryvbmigration.data.dto.GameMigrationDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MigrationGamesService {

  private final GameService gameService;
  private final LithiumServiceClientFactory services;

  public void initiateProviderService(GameMigrationDetails details) throws Exception {
      Game game = gameService.findOrCreateGame(
          details.getDomainName(),
          details.getProviderGuid(),
          details.getGameName(),
          details.getCommercialName(),
          details.getProviderGameId(),
          details.getDescription(),
          null,
          details.getRtp(),
          details.getDateNow(),
          details.getDateNow(),
          details.getDateNow(),
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          null,
          null,
          null,
          false,
          false,
          false,
          null,
          null
      );

      getCasinoMigrationClient().addGameToCasino(MigrationGames.builder()
          .currencyCode(details.getCurrencyCode())
          .domainName(details.getDomainName())
          .providerGuid(details.getDomainName() + "/" + game.getProviderGuid())
          .gameGuid(details.getDomainName() + "/" + game.getGuid())
          .build());
  }

  private CasinoMigrationClient getCasinoMigrationClient()
      throws Status500UnhandledCasinoClientException {
    try {
      return services.target(CasinoMigrationClient.class, "service-casino", true);
    } catch (Exception e) {
      log.error("Problem getting Casino Migration service", e);
      throw new Status500UnhandledCasinoClientException(
          "Unable to retrieve casino client proxy: " + e.getMessage());
    }
  }
}
