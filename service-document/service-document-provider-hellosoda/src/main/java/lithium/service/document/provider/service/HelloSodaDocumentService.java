package lithium.service.document.provider.service;

import lithium.service.document.provider.api.exceptions.Status413FailToGetSessionTokenFromHelloSodaServiceException;
import lithium.service.document.provider.api.exceptions.Status411FailAuthInHelloSodaServiceException;
import lithium.service.document.provider.api.exceptions.Status412FailToGetJobDetailsException;
import lithium.service.document.provider.api.exceptions.Status414FailFromHelloSodaServiceException;
import lithium.service.document.provider.api.exceptions.Status540ProviderNotConfiguredException;
import lithium.service.document.provider.api.schema.JobData;
import lithium.service.document.provider.api.schema.ReportResponse;

import java.io.IOException;

public interface HelloSodaDocumentService {
//    String createJob(JobDetailsRequest detailsRequest, String notifyUrl, String domainName, lithium.service.user.client.objects.User user) throws UserNotFoundException, UserClientServiceFactoryException, Status500ProviderNotConfiguredException, Status203FailToCreateJobFromHelloSodaException;

    String attachUserInformationToJob(lithium.service.user.client.objects.User user, String domainName, String notifyUrl,
                                      String sessionId, String jobId, String clientId) throws Status540ProviderNotConfiguredException, Status414FailFromHelloSodaServiceException;

    JobData getJobDetailsById(String jobId, String domainName) throws Status540ProviderNotConfiguredException, Status412FailToGetJobDetailsException;

    ReportResponse getReportByJobId(String jobId, String domainName) throws Status540ProviderNotConfiguredException, Status412FailToGetJobDetailsException;

    byte[] getFrontSideImage(String sessionToken, String domainName) throws Status540ProviderNotConfiguredException, IOException, Status411FailAuthInHelloSodaServiceException;

    byte[] getBackSideImage(String sessionToken, String domainName) throws Status540ProviderNotConfiguredException, Status411FailAuthInHelloSodaServiceException;

    String getSessionToken(String domainName, String jobId, String helloSodaApiToken) throws Status540ProviderNotConfiguredException, Status413FailToGetSessionTokenFromHelloSodaServiceException;

    String getApiToken(String domainName) throws Status540ProviderNotConfiguredException, Status411FailAuthInHelloSodaServiceException;

    String commitJob(String domainName, String jobId, String sessionId) throws Status540ProviderNotConfiguredException, Status414FailFromHelloSodaServiceException;
}
