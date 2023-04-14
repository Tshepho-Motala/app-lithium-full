package lithium.service.reward.service;

import java.util.List;
import lithium.service.Response.Status;
import lithium.service.accounting.objects.AdjustmentResponse;
import lithium.service.accounting.objects.AdjustmentTransaction.AdjustmentResponseStatus;
import lithium.service.reward.client.dto.Domain;
import lithium.service.reward.client.dto.RewardRevision;
import lithium.service.reward.client.dto.RewardType;
import lithium.service.reward.enums.RewardTypeFieldName;
import lithium.service.reward.provider.client.dto.ProcessRewardRequest;
import lithium.service.reward.provider.client.dto.ProcessRewardResponse;
import lithium.service.reward.provider.client.dto.ProcessRewardStatus;
import lithium.service.reward.provider.client.dto.ProcessRewardTypeValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CashAwardService {

  @Autowired
  AccountingService accountingService;

  public ProcessRewardResponse awardCash(ProcessRewardRequest request) {
    long amountInCents = Long.parseLong(
        pluckValueFromProcessRewardTypeValue(request.getProcessRewardTypeValues(), RewardTypeFieldName.VALUE_IN_CENTS, "0"));
//    String currencyCode = pluckValueFromProcessRewardTypeValue(request.getProcessRewardTypeValues(), RewardTypeFieldName.CURRENCY_CODE,
//        request.domainCurrency()); //TODO: Currently using domain currency, which is the safer option, but this excludes any other/virtual currencies. Will need to revisit once we want to use any other currency.
    RewardRevision rewardRevision = request.getRewardRevision();
    RewardType rewardType = request.getRewardType();
    Domain domain = request.getDomain();

    AdjustmentResponse adjustmentResponse = accountingService.processCash(rewardType, rewardRevision.getId(), domain.getName(), domain.getCurrency(),
        request.getPlayer().guid(), request.getPlayerRewardTypeHistoryId(), amountInCents);

    ProcessRewardResponse processRewardResponse = ProcessRewardResponse.builder()
            .code(request.getReward().getCurrent().getCode())
            .build();

    if (adjustmentResponse.getAdjustments().get(0).getStatus() == AdjustmentResponseStatus.NEW) {
      processRewardResponse.setStatus(ProcessRewardStatus.SUCCESS);
      processRewardResponse.setCode(Status.OK.id().toString());
      processRewardResponse.setAmountAffected(amountInCents);
      processRewardResponse.setExternalReferenceId(adjustmentResponse.getAdjustments().get(0).getTransactionId() + "");
      processRewardResponse.setValueGiven(amountInCents);
      processRewardResponse.setValueInCents(amountInCents);
      processRewardResponse.setValueUsed(amountInCents);
    } else {
      processRewardResponse.setStatus(ProcessRewardStatus.FAILED);
      //                processRewardResponse.setCode(adjustmentResponse.getAdjustments().get(0).g
      //                processRewardResponse.setErrorCode(); //TODO: fill in something sensible.
    }
    return processRewardResponse;
  }

  public String pluckValueFromProcessRewardTypeValue(List<ProcessRewardTypeValue> processRewardTypeValues, RewardTypeFieldName fieldName,
      String defaultValue)
  {
    return processRewardTypeValues.stream()
        .filter(v -> v.getRewardTypeFieldName().equalsIgnoreCase(fieldName.rewardTypeFieldName()))
        .map(ProcessRewardTypeValue::getRewardRevisionTypeValue)
        .findFirst()
        .orElse(defaultValue);
  }
}
