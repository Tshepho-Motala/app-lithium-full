package lithium.service.casino.api.system;

import lithium.service.casino.client.CasinoMigrationClient;
import lithium.service.casino.client.data.MigrationGames;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/system")
@RequiredArgsConstructor
public class SystemGameController implements CasinoMigrationClient {

  private final GameService service;
  @Override
  @PostMapping("/migration/add-games")
  public void addGameToCasino(MigrationGames data) throws Status500UnhandledCasinoClientException {
    service.findOrCreate(data);
  }
}
