package lithium.service.document;

import lithium.service.document.client.objects.DocumentPurpose;
import lithium.service.document.client.objects.enums.DocumentReviewReason;
import lithium.service.document.client.objects.enums.DocumentReviewStatus;
import lithium.service.document.data.entities.Domain;
import lithium.service.document.data.entities.ReviewStatus;
import lithium.service.document.data.repositories.DocumentTypeRepository;
import lithium.service.document.data.repositories.DomainRepository;
import lithium.service.document.data.repositories.ReviewReasonRepository;
import lithium.service.document.data.repositories.ReviewStatusRepository;
import lithium.service.domain.client.CachingDomainClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


@Configuration
@Slf4j
public class ServiceDocumentInit {
	public static final String DOCUMENT_TYPE_NAME_OTHER = "Other";
	public static final String DOCUMENT_TYPE_NAME_PASSPORT = "Passport";
	public static final String DOCUMENT_TYPE_NAME_DRIVERS_LICENSE = "Drivers License";
	public static final String DOCUMENT_TYPE_NAME_BANK_STATEMENT = "Bank Statement";
	public static final String DOCUMENT_TYPE_NAME_UTILITY_BILL = "Utility Bill";
	public static final String DOCUMENT_TYPE_NAME_SELFIE_ID = "Selfie ID";

	@Autowired
	private ReviewStatusRepository reviewStatusRepository;
	@Autowired
	private ReviewReasonRepository reviewReasonRepository;
	@Autowired
	private DocumentTypeRepository documentTypeRepository;
	@Autowired
	private DomainRepository domainRepository;
	@Autowired
	private CachingDomainClientService cachingDomainClientService;

	public void init() throws Exception {
		for (DocumentReviewStatus status : DocumentReviewStatus.values()) {
			reviewStatusRepository.findOrCreateByName(status.getName(), ReviewStatus::new);
		}

		for (lithium.service.domain.client.objects.Domain playerDomain : cachingDomainClientService.getDomainClient().findAllPlayerDomains().getData()) {
			Domain domain = domainRepository.findOrCreateByName(playerDomain.getName(), Domain::new);
			for (DocumentReviewReason reviewReason : DocumentReviewReason.values()) {
				reviewReasonRepository.findOrCreate(domain, reviewReason.getName());
			}
			documentTypeRepository.findOrCreate(domain, DocumentPurpose.INTERNAL, DOCUMENT_TYPE_NAME_OTHER);
			documentTypeRepository.findOrCreate(domain, DocumentPurpose.INTERNAL, DOCUMENT_TYPE_NAME_PASSPORT);
			documentTypeRepository.findOrCreate(domain, DocumentPurpose.INTERNAL, DOCUMENT_TYPE_NAME_DRIVERS_LICENSE);
			documentTypeRepository.findOrCreate(domain, DocumentPurpose.INTERNAL, DOCUMENT_TYPE_NAME_BANK_STATEMENT);
			documentTypeRepository.findOrCreate(domain, DocumentPurpose.INTERNAL, DOCUMENT_TYPE_NAME_UTILITY_BILL);
			documentTypeRepository.findOrCreate(domain, DocumentPurpose.INTERNAL, DOCUMENT_TYPE_NAME_SELFIE_ID);
		}
	}
}
