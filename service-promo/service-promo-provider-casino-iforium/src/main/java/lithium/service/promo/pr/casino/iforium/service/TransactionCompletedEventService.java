package lithium.service.promo.pr.casino.iforium.service;

import java.text.MessageFormat;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.casino.CasinoTransactionLabels;
import lithium.service.accounting.client.stream.event.ICompletedTransactionProcessor;
import lithium.service.accounting.objects.CompleteTransaction;
import lithium.service.accounting.objects.TransactionLabelBasic;
import lithium.service.games.client.objects.Game;
import lithium.service.promo.client.dto.IActivity;
import lithium.service.promo.client.objects.Label;
import lithium.service.promo.client.objects.PromoActivityBasic;
import lithium.service.promo.client.stream.MissionStatsStream;
import lithium.service.promo.pr.casino.iforium.dto.Activity;
import lithium.service.promo.pr.casino.iforium.dto.Category;
import lithium.service.promo.pr.casino.iforium.dto.ExtraFieldType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransactionCompletedEventService implements ICompletedTransactionProcessor {

  @Value("${spring.application.name}")
  private String applicationName;

  @Autowired
  MissionStatsStream missionStatsStream;

  /**
   * Method to be used for processing of completed accounting events by the implementing service
   *
   * @param request
   * @throws Exception
   */
  @Override
  public void processCompletedTransaction(CompleteTransaction request) throws Exception {
    log.debug("Received:: " + request);

    if (!shouldProcessTransaction(request)) {
      log.debug("The request did not originate from service-casino-provider-iforium, skipping..., request {}", request);
      return;
    }

    String domainName = request.getTransactionEntryList().get(0).getAccount().getDomain().getName();
    String userGuid = request.getTransactionEntryList().get(0).getAccount().getOwner().getGuid();
    String transactionType = request.getTransactionType();
    IActivity activity;
    Long tranAmountCents = 0L;
    if ("CASINO_WIN".equalsIgnoreCase(transactionType)) {
      activity = Activity.WIN;
      tranAmountCents = request.getTransactionEntryList().get(1).getAmountCents(); // tran contra entry
    } else if ("CASINO_BET".equalsIgnoreCase(transactionType)) {
      activity = Activity.WAGER;
      tranAmountCents = request.getTransactionEntryList().get(0).getAmountCents(); // tran entry
    } else {
      log.debug(MessageFormat.format("Transaction Type {0} is not supported at this time", transactionType));
      return;
    }

    String gameGuid = getLabel(request.getTransactionLabelList(), CasinoTransactionLabels.GAME_GUID_LABEL, null);
    String gameProviderId = getLabel(request.getTransactionLabelList(), CasinoTransactionLabels.GAME_PROVIDER_ID, "");

    // strip off domain from the game guid
    if (gameGuid != null && gameGuid.contains("/")) {
      gameGuid = gameGuid.substring(gameGuid.indexOf("/") + 1);
    }

    String gameType = "";

    Optional<TransactionLabelBasic> lv = request.getTransactionLabelList().stream().filter(tlv -> tlv.getLabelName().equalsIgnoreCase("game-data")).findFirst();
    if (lv.isPresent()) {
      byte[] objectAsJson = Base64.getDecoder().decode(lv.get().getLabelValue());
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      Game game = objectMapper.readValue(objectAsJson, Game.class);
      log.debug("Game: "+game);

      if(game.getPrimaryGameType() != null) {
        gameType = game.getPrimaryGameType().getName();
      }

    }

    PromoActivityBasic mab = PromoActivityBasic.builder()
        .category(Category.CASINO)
        .activity(activity)
        .ownerGuid(userGuid)
        .domainName(domainName)
        .provider(applicationName)
        .labelValues(
            Stream.of(new String[][] {
                    { ExtraFieldType.GAME.getType(), gameGuid },
                    { ExtraFieldType.GAME_TYPE.getType(), gameType },
                    { ExtraFieldType.GAME_PROVIDER.getType(), gameProviderId },
            }).collect(Collectors.toMap(data -> data[0], data -> data[1]))
        )
        .value(tranAmountCents)
        .build();

    log.debug("Sending: "+mab);
    missionStatsStream.registerActivity(mab);
  }

  private String getLabel(List<TransactionLabelBasic> labels, String name, String defaultValue) {
    return labels.stream().filter(tlv -> tlv.getLabelName().equalsIgnoreCase(name) && !"null".equalsIgnoreCase(tlv.getLabelValue()))
            .map(TransactionLabelBasic::getLabelValue)
            .findFirst()
            .orElse(defaultValue);
  }

  //TODO: need to come back and find a better way to this, for now this works
  private boolean shouldProcessTransaction(CompleteTransaction request) {
    String provider = getLabel(request.getTransactionLabelList(), CasinoTransactionLabels.PROVIDER_GUID_LABEL, null);

    if (provider != null && provider.contains("/")) {
      provider = provider.substring(provider.indexOf("/") + 1);
    }

    return "service-casino-provider-iforium".equalsIgnoreCase(provider);
  }
}