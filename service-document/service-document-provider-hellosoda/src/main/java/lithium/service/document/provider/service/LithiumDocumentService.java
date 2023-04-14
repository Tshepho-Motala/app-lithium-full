package lithium.service.document.provider.service;

import lithium.service.document.client.objects.DocumentInfo;
import lithium.service.document.provider.api.schema.ReportResponse;
import lithium.service.document.provider.entity.HelloSodaStatus;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LithiumDocumentService {
    ResponseEntity updateDocument(String jobId) throws Exception, UserClientServiceFactoryException, UserNotFoundException;

    List<DocumentInfo> downloadAndSaveUserImages(ReportResponse.IdCheck.Metrics backMetrics, String sessionId, String jobId, HelloSodaStatus status, String domainName, String userGuid) throws Exception;
}
