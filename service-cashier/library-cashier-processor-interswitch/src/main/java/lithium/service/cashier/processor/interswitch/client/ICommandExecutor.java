package lithium.service.cashier.processor.interswitch.client;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.user.client.objects.User;

import java.util.List;

public interface ICommandExecutor {
	Object executeCommand(String processorCode) throws Status500InternalServerErrorException, Status550ServiceDomainClientException, Status500LimitInternalSystemClientException;
	User getAllowedUser(String domainName);
	Object buildErrorMessage(String message);
	List<String> resolveProcessorCodes(String request);
}
