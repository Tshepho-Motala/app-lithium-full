package lithium.service.casino.client;

import lithium.service.casino.client.data.MigrationGames;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name="service-casino", path="/system/migration")
public interface CasinoMigrationClient {

  @RequestMapping("/add-games")
  void addGameToCasino(@RequestBody MigrationGames data) throws Status500UnhandledCasinoClientException;
}
