package lithium.service.accountinghistory.service;

import java.util.Map;
import lithium.service.accounting.objects.TransactionEntryBO;
import lithium.service.casino.SystemBonusClientService;
import lithium.service.casino.client.data.PlayerBonusHistory;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.games.client.objects.Game;
import lithium.service.reward.client.QueryRewardClientService;
import lithium.service.reward.client.dto.PlayerRewardTypeHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RewardsService {

  @Autowired
  QueryRewardClientService queryRewardClientService;
  @Autowired
  SystemBonusClientService systemBonusClientService;


  public void enrichRewardInformation(Map<String, Game> domainGameMap, DataTableResponse<TransactionEntryBO> result) {
    result.getData().stream().forEach(transactionEntryBO -> {
      log.trace("transactionEntryBO: " + transactionEntryBO);
      if (null != transactionEntryBO.getDetails().getPlayerRewardTypeHistoryId()) {
        queryServiceReward(transactionEntryBO);
      } else if (null != transactionEntryBO.getDetails().getPlayerBonusHistoryId() && !Long.valueOf(-1L)
          .equals(transactionEntryBO.getDetails().getPlayerBonusHistoryId())) {
        queryServiceCasino(transactionEntryBO);
      }
    });
  }

  private void queryServiceReward(TransactionEntryBO transactionEntryBO) {
    try {
      PlayerRewardTypeHistory prth = queryRewardClientService.findById(transactionEntryBO.getDetails().getPlayerRewardTypeHistoryId());
      if ((prth != null) && (prth.getRewardRevisionType() != null) && (prth.getRewardRevisionType().getRewardRevision() != null)) {
        transactionEntryBO.getDetails().setBonusCode(prth.getRewardRevisionType().getRewardRevision().getCode());
        transactionEntryBO.getDetails().setBonusName(prth.getRewardRevisionType().getRewardRevision().getName());
        transactionEntryBO.getDetails().setBonusRevisionId(prth.getRewardRevisionType().getRewardRevision().getId());
      } else if ((prth != null) && (prth.getPlayerRewardHistory() != null) && (prth.getPlayerRewardHistory().getRewardRevision() != null)) {
        transactionEntryBO.getDetails().setBonusCode(prth.getPlayerRewardHistory().getRewardRevision().getCode());
        transactionEntryBO.getDetails().setBonusName(prth.getPlayerRewardHistory().getRewardRevision().getName());
        transactionEntryBO.getDetails().setBonusRevisionId(prth.getPlayerRewardHistory().getRewardRevision().getId());
      }
    } catch (Exception e) {
      log.warn("queryServiceReward error occurred", e);
    }
  }

  private void queryServiceCasino(TransactionEntryBO transactionEntryBO) {
    try {
      PlayerBonusHistory pbh = systemBonusClientService.findPlayerBonusHistoryById(transactionEntryBO.getDetails().getPlayerBonusHistoryId());
      if (pbh != null) {
        transactionEntryBO.getDetails().setBonusCode(pbh.getBonusRevision().getBonusCode()+" (Legacy)");
        transactionEntryBO.getDetails().setBonusName(pbh.getBonusRevision().getBonusName());
        transactionEntryBO.getDetails().setBonusRevisionId(pbh.getBonusRevision().getId());
      }
    } catch (Exception e) {
      log.warn("queryServiceCasino error occurred", e);
    }
  }
}
