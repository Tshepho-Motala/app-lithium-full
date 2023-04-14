package lithium.service.user.services;

import lithium.exceptions.Status415InvalidDocumentTypeException;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.document.client.DocumentClient;
import lithium.service.document.client.objects.Document;
import lithium.service.document.client.objects.DocumentFile;
import lithium.systemauth.SystemAuthService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class DocumentService {
	
	public static final String DOCUMENT_AUTHOR_INTERNAL = "user-document-internal";
	public static final String DOCUMENT_AUTHOR_EXTERNAL = "user-document-external";
	
	@Autowired
	private LithiumServiceClientFactory services;
	
	@LoadBalanced
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired private TokenStore tokenStore;
	
	@Autowired
	SystemAuthService systemAuthService;
	
	public Response<List<Document>> listUserDocumentInternal(String ownerGuid, Principal principal) {
		try {
			return getDocumentClient().findDocumentByOwnerAndAuthorService(ownerGuid, DOCUMENT_AUTHOR_INTERNAL, getPrincipalGuid(principal));
		} catch (Exception e) {
			log.error("Error retrieving internal documents for user: " + ownerGuid, e);
			return Response.<List<Document>>builder().status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	public Response<List<Document>> listUserDocumentExternal(String ownerGuid, Principal principal) {
		try {
			Response<List<Document>> docListResponse = getDocumentClient().findDocumentByOwnerAndAuthorService(ownerGuid, DOCUMENT_AUTHOR_EXTERNAL, getPrincipalGuid(principal));
			
			//Documents that always need to be created for a player
			if ((docListResponse.getStatus() == Status.OK || docListResponse.getStatus() == Status.NOT_FOUND) && (docListResponse.getData() == null || docListResponse.getData().isEmpty())) {
				createDocument("Identification Document", "New", "Personal Identification",ownerGuid, true,principal);
				createDocument("Proof Of Residence", "New", "Residential Locale", ownerGuid, true, principal);
				docListResponse = getDocumentClient().findDocumentByOwnerAndAuthorService(ownerGuid, DOCUMENT_AUTHOR_EXTERNAL, getPrincipalGuid(principal));
			}
			
			return docListResponse;
			
		} catch (Exception e) {
			log.error("Error retrieving external documents for user: " + ownerGuid, e);
			return Response.<List<Document>>builder().status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	
	private DocumentClient getDocumentClient() throws LithiumServiceClientFactoryException {
		return services.target(DocumentClient.class,"service-document", true);
	}

	public Response<DocumentFile> getDocumentFile(String documentUuid, Principal principal) {
		try {
			return getDocumentClient().findFileByDocumentUuid(documentUuid, getPrincipalGuid(principal));
		} catch (Exception e) {
			log.error("Error retrieving file with document uuid: " + documentUuid, e);
			return Response.<DocumentFile>builder().status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	public Response<DocumentFile> saveFilePlayer(String documentUuid, String documentStatus, final MultipartFile file, Principal principal) throws Status415InvalidDocumentTypeException {
		try {
			for (Document d : listUserDocumentExternal(principal).getData()) {
				if (d.getUuid().equals(documentUuid)) {
					Response<DocumentFile> response = saveFile(documentUuid, documentStatus, file, principal);
					return response;
				}
			}
			return Response.<DocumentFile>builder().status(Status.FORBIDDEN).message("The document is not associated with the authenticated user").build();
		} catch(Status415InvalidDocumentTypeException exception) {
			throw exception;
		}
	}
	
	public Response<DocumentFile> saveFileAdmin(String documentUuid, final MultipartFile file, Principal principal) throws Status415InvalidDocumentTypeException  {
		try {
			Response<DocumentFile> response = saveFile(documentUuid, null, file, principal);
			return response;
		} catch (Status415InvalidDocumentTypeException exception) {
			throw exception;
		}
	}
	
	private Response<DocumentFile> saveFile(String documentUuid, String documentStatus, final MultipartFile file, Principal principal) throws Status415InvalidDocumentTypeException {
		//Check for file type
		List<String> fileTypes = Arrays.asList("image/jpg", "image/jpeg", "image/png", "application/pdf");
		if (!isValidFileType(file, fileTypes)) {
			String [] suffix = {"jpg", "jpeg", "png", "pdf"};
			throw new Status415InvalidDocumentTypeException("Invalid Document Type, supported types are " + Arrays.toString(suffix));
		}

		try {
			String token = systemAuthService.getTokenValue();

			final MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
			parts.add("file", new MultipartFileResource(file.getInputStream(), file.getOriginalFilename()));
			parts.add("authorGuid", getPrincipalGuid(principal));
			parts.add("filename", file.getOriginalFilename());
			parts.add("documentStatus", documentStatus);

			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			headers.set("Authorization", "bearer " + token);

			final HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(
					parts, headers);
			
			ResponseEntity<DocumentFile> e = restTemplate.exchange("http://service-document/document/saveFile/{documentUuid}", HttpMethod.POST, requestEntity, DocumentFile.class, documentUuid);
			
			if (e.getStatusCode() != HttpStatus.OK) throw new Exception(e.getStatusCode().getReasonPhrase());
			
			Response<DocumentFile> response = Response.<DocumentFile>builder().data(e.getBody()).status(Status.OK).build();
			
			return response;
			
		} catch (Exception e) {
			log.error("Error saving documentFile: " + documentUuid + " file data: " + file, e);
			return Response.<DocumentFile>builder().status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	class MultipartFileResource extends InputStreamResource {
		private String filename;
		

		
		public MultipartFileResource(final InputStream inputStream, final String filename) {
			super(inputStream);
			this.filename = filename;
		}
		
		@Override
		public String getFilename() {
			return filename;
		}
		
		@Override
		public long contentLength() throws IOException {
			return -1; // due to the custom resource converter, we pass a negative number for content length
			// this allows the input stream to be read only once.
		}
	}

	public Response<DocumentFile> downloadFile(String documentUuid, Integer page) throws Exception {
		return getDocumentClient().downloadFile(documentUuid, page);
	}

	public Response<Document> createDocument(String name, String statusString, String documentFunction,
			String ownerGuid, boolean external, Principal principal) {
		
		try {
			if (external) {
				return getDocumentClient().createDocument(name, statusString, documentFunction, ownerGuid, DOCUMENT_AUTHOR_EXTERNAL, getPrincipalGuid(principal));
			} else {
				return getDocumentClient().createDocument(name, statusString, documentFunction, ownerGuid, DOCUMENT_AUTHOR_INTERNAL, getPrincipalGuid(principal));
			}
		} catch (Exception e) {
			log.error("Error creating document: " + name +" "+ ownerGuid +" "+ documentFunction +" "+ statusString, e);
			return Response.<Document>builder().status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
	}

	public Response<Document> updateDocument(String documentUuid, String name, String statusString,
			String documentFunction, boolean archive, boolean delete, Principal principal) {
		
		try {
			return getDocumentClient().updateDocument(documentUuid, name, statusString, documentFunction, archive, delete, getPrincipalGuid(principal));
		} catch (Exception e) {
			log.error("Error updating document: " + documentUuid +" "+ name  +" "+ documentFunction +" "+ statusString, e);
			return Response.<Document>builder().status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
	}

	public Response<List<Document>> listUserDocumentExternal(Principal principal) {
		
		String userGuid = getPrincipalGuid(principal);
		Response<List<Document>> docListResponse = listUserDocumentExternal(userGuid, principal);
		
		if (docListResponse.getStatus() == Status.OK && !docListResponse.getData().isEmpty()) {
			if (! checkOwner(docListResponse.getData().get(0).getOwnerGuid(), principal)) {
				docListResponse.setData(null);
				docListResponse.setStatus(Status.FORBIDDEN);
			}
		}
		return docListResponse;
	}
	
	private String getPrincipalGuid(Principal principal) {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, principal).build();
		return util.getJwtUser().getDomainName()+"/"+util.getJwtUser().getUsername();
	}

//	public Response<List<Document>> listUserDocumentInternal(Principal principal) {
//		String userGuid = getPrincipalGuid(principal);
//		return listUserDocumentInternal(userGuid, principal);
//	}

//	public Response<Document> createDocument(String name, String statusString, String documentFunction,
//			Principal principal) {
//		String userGuid = getPrincipalGuid(principal);
//		return createDocument(name, statusString, documentFunction, userGuid, principal);
//	}
	
	private boolean checkOwner(String responseOwnerGuid, Principal principal) {
		if (responseOwnerGuid.equalsIgnoreCase(getPrincipalGuid(principal))) {
			return true;
		}
		return false;
	}

	/**
	 *
	 * @param multipartFile
	 * @param fileTypes
	 * @return
	 */
	private boolean isValidFileType(MultipartFile multipartFile, List<String> fileTypes) {
		if (multipartFile == null) {
			return false;
		}
		for (String fileType: fileTypes) {
			if(fileType.equalsIgnoreCase(multipartFile.getContentType())) {
				return true;
			}
		}
		return false;
	}
}
