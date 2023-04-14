package lithium.service.user.provider.threshold.services;

import lithium.service.accounting.objects.CompleteSummaryAccountTransactionType;
import lithium.service.user.provider.threshold.data.entities.Domain;

public interface DomainService extends AbstractService<Domain> {

  Domain findOrCreate(String domainName);
}
