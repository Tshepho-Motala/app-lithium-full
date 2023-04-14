package lithium.service.promo.pr.sportsbook.sbt.service;

import lithium.service.accounting.client.stream.event.ICompletedTransactionProcessor;
import lithium.service.accounting.objects.CompleteTransaction;
import lithium.service.promo.client.dto.IActivity;
import lithium.service.promo.client.objects.PromoActivityBasic;
import lithium.service.promo.client.stream.MissionStatsStream;
import lithium.service.promo.pr.sportsbook.sbt.dto.Activity;
import lithium.service.promo.pr.sportsbook.sbt.dto.Category;
import lithium.service.promo.pr.sportsbook.sbt.dto.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionCompletedEventService implements ICompletedTransactionProcessor {

  @Value("${spring.application.name}")
  private String applicationName;

  private final MissionStatsStream missionStatsStream;

  /**
   * Method to be used for processing of completed accounting events by the implementing service
   *
   * @param request
   * @throws Exception
   */
  @Override
  public void processCompletedTransaction(CompleteTransaction request) throws Exception {
    log.info("Received:: " + request);

    IActivity activity;
    Long tranAmountCents;

    String domainName = request.getTransactionEntryList().get(0).getAccount().getDomain().getName();
    String userGuid = request.getTransactionEntryList().get(0).getAccount().getOwner().getGuid();
    String transactionType = request.getTransactionType();

    TransactionType ttype = TransactionType.fromType(transactionType);

    if (ttype == TransactionType.SPORTS_WIN) {
      activity = Activity.WIN;
      tranAmountCents = request.getTransactionEntryList().get(1).getAmountCents();
    } else if (ttype == TransactionType.SPORTS_RESERVE) {
      activity = Activity.BET;
      tranAmountCents = request.getTransactionEntryList().get(0).getAmountCents();
    } else {
      return;
    }

    PromoActivityBasic mab = PromoActivityBasic.builder()
            .category(Category.SPORT)
            .activity(activity)
            .ownerGuid(userGuid)
            .domainName(domainName)
            .value(tranAmountCents)
            .provider(applicationName)
            .build();

    log.info("Sending: "+mab);
    missionStatsStream.registerActivity(mab);
  }
}