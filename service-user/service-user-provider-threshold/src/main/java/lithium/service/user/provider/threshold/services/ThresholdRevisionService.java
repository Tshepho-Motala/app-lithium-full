package lithium.service.user.provider.threshold.services;

import java.util.Optional;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.accounting.objects.CompleteSummaryAccountTransactionTypeDetail;
import lithium.service.user.provider.threshold.data.dto.ThresholdRevisionDto;
import lithium.service.user.provider.threshold.data.entities.ThresholdRevision;
import lithium.service.user.provider.threshold.data.entities.User;
import lithium.service.user.provider.threshold.data.enums.Type;
import lithium.tokens.LithiumTokenUtil;

public interface ThresholdRevisionService extends AbstractService<ThresholdRevision> {

   boolean playerReachedThresholdLimit(ThresholdRevision thresholdRevision, CompleteSummaryAccountTransactionTypeDetail
      completeSummaryAccountTransactionTypeDetail, User user);

  void deleteThresholdRevision(String domainName, int granularity, Type type, LithiumTokenUtil tokenUtil) throws Exception;

  ThresholdRevision saveThresholdRevision(ThresholdRevisionDto thresholdRevisionDto, LithiumTokenUtil lithiumTokenUtil) throws Exception;

  Optional<ThresholdRevision> findByDomainAndGranularity(String domainName, int granularity);


  Optional<ThresholdRevision> findAgeBasedRevisionByDomainAndGranularity(String domainName, int granularity,int minAge, int maxAge);

  ThresholdRevision saveAgeBasedThresholdRevision(ThresholdRevisionDto thresholdRevisionDto,int  minAge, int maxAge, LithiumTokenUtil lithiumTokenUtil)
  throws Status500InternalServerErrorException;

}
