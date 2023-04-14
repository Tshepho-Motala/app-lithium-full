package lithium.service.games.stream;

import lithium.service.accounting.client.stream.event.ICompletedTransactionProcessor;
import lithium.service.accounting.objects.CompleteTransaction;
import lithium.service.accounting.objects.TransactionLabelBasic;
import lithium.service.casino.CasinoTranType;
import lithium.service.casino.CasinoTransactionLabels;
import lithium.service.casino.client.objects.FreeGamePayload;
import lithium.service.games.data.entities.Game;
import lithium.service.games.data.entities.User;
import lithium.service.games.services.FreeGameService;
import lithium.service.games.services.GameService;
import lithium.service.games.services.RecentlyPlayedService;
import lithium.service.games.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CompletedTransactionProcessor implements ICompletedTransactionProcessor {

  @Autowired
  RecentlyPlayedService recentlyPlayedService;
  @Autowired
  GameService gameService;
  @Autowired
  UserService userService;
  @Autowired
  private FreeGameService freeGameService;
  @Value("${lithium.queue.free-game-metadata-enabled}")
  private boolean freeGameMetaDataEnabled;

  @Override
  public void processCompletedTransaction(CompleteTransaction request) throws Exception {
    log.trace("CompletedTransactionProcessor.processCompletedTransaction [request=" + request + "]");
    if (request.getTransactionType().contentEquals(CasinoTranType.CASINO_BET.toString()) || request.getTransactionType()
        .contentEquals(CasinoTranType.CASINO_BET_FREESPIN.toString()) || request.getTransactionType()
        .contentEquals(CasinoTranType.CASINO_WIN_FREESPIN.toString())) {
      String userGuid = request.getTransactionEntryList().get(0).getAccount().getOwner().getGuid();
      String gameGuid = request.getTransactionLabelList().stream()
          .filter(label -> label.getLabelName().contentEquals(CasinoTransactionLabels.GAME_GUID_LABEL))
          .map(TransactionLabelBasic::getLabelValue)
          .findFirst()
          .orElse(null);
      User user = userService.findOrCreate(userGuid);
      Game game = gameService.findByGameAndDomainName(gameGuid.split("/")[1] ,user.domainName());
      if (game == null) {
        log.warn("Game is not configured. Could not add entry to recently played games history." +
                " [userGuid="+userGuid+", gameGuid="+gameGuid+"]");
        return;
      }
      if (freeGameMetaDataEnabled) {
        FreeGamePayload freeGamePayload = new FreeGamePayload(game.getFreeGame(), game.getProviderGameId(), user.getGuid());
        freeGameService.checkBetsOnFreeGame(freeGamePayload);
      }
      recentlyPlayedService.add(game, user.getId());
    }
  }

}
