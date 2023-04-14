package lithium.service.limit.services;

import lithium.service.limit.client.objects.ExclusionSource;
import lithium.service.limit.client.objects.SelfExclusionCoolOffPreferenceRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@Slf4j
public class SelfExclusionCoolOffPreferenceIngestionService {
    private final CoolOffService coolOffService;
    private final ExclusionService exclusionService;


    @Autowired
    public SelfExclusionCoolOffPreferenceIngestionService(CoolOffService coolOffService,
            ExclusionService exclusionService) {
        this.coolOffService = coolOffService;
        this.exclusionService = exclusionService;

    }

    public void ingest(SelfExclusionCoolOffPreferenceRequest request) throws Exception {
        log.trace("Received a request to migrate self exclusion or cool off player preference | {}", request);

            String domainName = request.getLithiumUserGuid().split("/")[0];
            Date createdDate = getCreatedDate(request.getStartDate(), request.getRequestedDate());
            Date expiryDate = getExpiryDate(createdDate, request.getPeriodInDays());

            if (request.isSelfExclusionRequest()) {
                exclusionService.setMinimal(domainName, request.getLithiumUserGuid(), createdDate, expiryDate,
                        null, ExclusionSource.INTERNAL);
            } else if (request.isCoolOffRequest()) {
                coolOffService.setMinimal(request.getLithiumUserGuid(), createdDate, expiryDate);
            }

    }

    private Date getCreatedDate(Date startDate, Date requestedDate) {
        if (startDate == null) {
            return requestedDate;
        }
        return startDate;
    }

    private Date getExpiryDate(Date startDate, Long periodInDays) {
        if ((periodInDays == null) || (periodInDays.compareTo(-1L) == 0)) {
            return null;
        }
        LocalDateTime expiryDate = LocalDateTime
                .ofInstant(startDate.toInstant(), ZoneId.systemDefault())
                .plusDays(periodInDays);
        return Date.from(expiryDate
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }
}
