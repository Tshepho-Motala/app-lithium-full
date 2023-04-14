package lithium.service.document.provider.service;

import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.document.provider.api.exceptions.Status412FailToGetJobDetailsException;
import lithium.service.document.provider.api.exceptions.Status414FailFromHelloSodaServiceException;
import lithium.service.document.provider.api.exceptions.Status540ProviderNotConfiguredException;
import lithium.service.document.provider.api.schema.JobResponse;
import lithium.service.kyc.client.exceptions.Status459VerificationResultNotFountException;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import org.springframework.http.ResponseEntity;

public interface HelloSodaProfileFKService {

    JobResponse submitFKCheck(String fkToken, String guid, String applicationId, Long tokenUtilId) throws Status540ProviderNotConfiguredException, UserClientServiceFactoryException, UserNotFoundException, Status414FailFromHelloSodaServiceException, LithiumServiceClientFactoryException;

    ResponseEntity<String> processFacebookHSReport(String jobId) throws Status540ProviderNotConfiguredException, Status412FailToGetJobDetailsException, UserClientServiceFactoryException, UserNotFoundException, LithiumServiceClientFactoryException, Status459VerificationResultNotFountException;
}
