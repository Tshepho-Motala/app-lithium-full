package lithium.service.document.controllers;

import lithium.client.changelog.objects.ChangeLogs;
import lithium.service.Response;
import lithium.service.document.client.objects.DocumentType;
import lithium.service.document.services.DocumentTypeService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/backoffice/document-type/{domain}")
public class BackofficeDocumentTypeController {

	@Autowired
	private DocumentTypeService documentTypeService;

	@GetMapping("/list")
	public Response<List<DocumentType>> list(@PathVariable("domain") String domain, LithiumTokenUtil tokenUtil)  {
		return Response.<List<DocumentType>>builder().data(documentTypeService.listPerDomain(domain)).build();
	}

	@PutMapping("/save")
	public Response<DocumentType> save(@PathVariable("domain") String domain,
									   @RequestBody DocumentType documentType,
									   LithiumTokenUtil tokenUtil) {
		log.info("Got request: " + documentType);
		return Response.<DocumentType>builder().data(documentTypeService.save(domain, documentType, tokenUtil)).build();
	}

	@GetMapping(value = "/changelogs")
	public @ResponseBody Response<ChangeLogs> changeLogs(@PathVariable("domain") String domainName, @RequestParam int p) throws Exception {
		return documentTypeService.changelog(domainName, p);
	}

}
