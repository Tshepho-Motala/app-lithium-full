package lithium.service.user.provider.threshold.services;

import java.math.BigDecimal;
import lithium.service.accounting.objects.CompleteSummaryAccountTransactionTypeDetail;
import lithium.service.user.provider.threshold.data.entities.ThresholdRevision;
import lithium.service.user.provider.threshold.data.entities.User;

public interface LimitService {
   BigDecimal getLimitAmount(ThresholdRevision thresholdRevision, User user);

   void processLossLimitEvent(String domainName, User user, CompleteSummaryAccountTransactionTypeDetail completeSummaryAccountTransactionTypeDetail)
       throws Exception;
}
