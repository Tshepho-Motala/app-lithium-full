package lithium.service.user.threshold.service;

import java.math.BigDecimal;
import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.user.threshold.client.dto.ThresholdDto;
import lithium.service.user.threshold.client.dto.ThresholdRevisionDto;
import lithium.service.user.threshold.client.enums.EType;
import lithium.service.user.threshold.data.entities.Threshold;
import lithium.service.user.threshold.data.entities.ThresholdRevision;
import lithium.tokens.LithiumTokenUtil;
import org.modelmapper.ModelMapper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface ThresholdService {

  Threshold findByAgeOrDefault(String domainName, EType eType, Integer granularity, Integer age);

  Threshold find(String domainName, EType eType, Integer granularity, Integer ageMin, Integer ageMax, LithiumTokenUtil lithiumTokenUtil);

  @Retryable( maxAttempts = 5, backoff = @Backoff( random = true, delay = 50, maxDelay = 1000 ), exclude = {
      NotRetryableErrorCodeException.class}, include = Exception.class )
  @Transactional( propagation = Propagation.REQUIRED, rollbackFor = Exception.class )
  Threshold save(String domainName, Threshold threshold, BigDecimal percentage, BigDecimal amount, EType eType, Integer granularity, Integer minAge,
      Integer maxAge, LithiumTokenUtil lithiumTokenUtil)
  throws Status500InternalServerErrorException;

  Threshold disable(String domainName, Threshold threshold, EType eType, LithiumTokenUtil lithiumTokenUtil);

  default ThresholdDto mapDto(ModelMapper modelMapper, Threshold threshold) {
    ThresholdRevision thresholdRevision = threshold.getCurrent();
    ThresholdDto dto = modelMapper.map(threshold, ThresholdDto.class);
    dto.setCurrent(modelMapper.map(thresholdRevision, ThresholdRevisionDto.class));
    return dto;
  }
}
