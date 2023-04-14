package lithium.service.document.controllers;

import lithium.exceptions.Status405UnsupportedDocumentTypeException;
import lithium.exceptions.Status406OverLimitFileSizeException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.document.data.entities.DocumentFile;
import lithium.service.document.exceptions.Status400BadRequestException;
import lithium.service.document.services.FrontendDocumentService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/frontend/document")
public class FrontendDocumentController {

	@Autowired
	private FrontendDocumentService frontendDocumentService;

	@RequestMapping("/upload")
	public Response<DocumentFile> upload(@RequestPart("documentType") String documentType, @RequestPart("file") MultipartFile multipartFile, LithiumTokenUtil tokenUtil) throws Status500InternalServerErrorException, Status400BadRequestException, Status550ServiceDomainClientException, Status405UnsupportedDocumentTypeException, Status406OverLimitFileSizeException, IOException {
		return frontendDocumentService.uploadVerificationDocument(documentType, multipartFile, tokenUtil);
	}
}
