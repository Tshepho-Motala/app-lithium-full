package lithium.service.user.controllers;

import java.security.Principal;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import lithium.client.changelog.ChangeLogService;
import lithium.exceptions.Status415InvalidDocumentTypeException;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.document.client.objects.Document;
import lithium.service.document.client.objects.DocumentFile;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.repositories.GroupRepository;
import lithium.service.user.data.repositories.LoginEventRepository;
import lithium.service.user.services.DocumentService;
import lithium.service.user.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("{domainName}/users/documents")
public class UserDocumentController {
	@Autowired UserService userService;
	@Autowired GroupRepository groupRepository;
	@Autowired ChangeLogService changeLogService;
	@Autowired ModelMapper modelMapper;
	@Autowired LoginEventRepository loginEventRepository;
	@Autowired DocumentService documentService;

	@GetMapping(path="/admin/listUserDocumentsInternal")
	public Response<List<Document>> listUserDocumentsInternal(@PathVariable("domainName") String domainName, @RequestParam(name="userId") User user, Principal principal) {
		//return documentService.listUserDocumentInternal(user.getDomain().getName()+"/"+user.getUsername(), principal);
		return documentService.listUserDocumentInternal(user.guid(), principal);
	}

	@GetMapping(path="/player/listUserDocumentsExternal")
	public Response<List<Document>> listUserDocumentsExternal(@PathVariable("domainName") String domainName, Principal principal) {
		return documentService.listUserDocumentExternal(principal);
	}

	@GetMapping(path="/admin/listUserDocumentsExternal")
	public Response<List<Document>> listUserDocumentsExternal(@PathVariable("domainName") String domainName, @RequestParam("userId") User user, Principal principal) {
		//return documentService.listUserDocumentExternal(user.getDomain().getName()+"/"+user.getUsername(), principal);
		return documentService.listUserDocumentExternal(user.guid(), principal);
	}

//	@GetMapping(path="/getDocumentFile")
//	public Response<DocumentFile> getDocumentFile(String documentUuid, Principal principal) {
//		return documentService.getDocumentFile(documentUuid, principal);
//	}

	@PostMapping(path="/admin/saveFile")
	public Response<DocumentFile> saveFileAdmin(@RequestParam("documentUuid") String documentUuid, @RequestPart("image") final MultipartFile file, Principal principal) {
		log.debug("Received a multipart file" + principal);
		try {
			Response<DocumentFile> response = documentService.saveFileAdmin(documentUuid.split(",")[0], file, principal);
			return response;
		} catch (Status415InvalidDocumentTypeException exception) {
			log.error(exception.getMessage(), exception);
			return Response.<DocumentFile>builder().status(Status.INVALID_DOCUMENT_TYPE).message(exception.getMessage()).build();
		} catch (Exception e) {
			log.error("Unable to read file bytes", e);
			return Response.<DocumentFile>builder().status(Status.NOT_FOUND).message("Unable to read file bytes").build();
		}
	}

	@PostMapping(path="/player/saveFile")
	public Response<DocumentFile> saveFilePlayer(@RequestParam("documentUuid") String documentUuid, @RequestParam("documentStatus") String documentStatus, @RequestPart("image") final MultipartFile file, Principal principal) {
		log.info("Received a multipart file"+ principal);
		try {
			return documentService.saveFilePlayer(documentUuid.split(",")[0], documentStatus.split(",")[0], file, principal);
		} catch (Exception e) {
			log.error("Unable to read file bytes", e);
		}

		return null;
	}

	@RequestMapping(value = "/downloadFile", method = RequestMethod.GET)
	@ResponseBody public void downloadFile(@RequestParam("documentUuid") String documentUuid, @RequestParam("page") Integer page, HttpServletResponse response) throws Exception {
		try {
		HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.noStore().getHeaderValue());

		Response<DocumentFile> responseDoc = documentService.downloadFile(documentUuid, page);

		if (responseDoc.getStatus() == Status.OK) {
			response.setHeader(HttpHeaders.CACHE_CONTROL, CacheControl.noStore().getHeaderValue());
			response.setContentType(responseDoc.getData().getFile().getMimeType());
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"" + responseDoc.getData().getFile().getName() +"\""));
			response.getOutputStream().write(responseDoc.getData().getFile().getData());
		}

		} finally {
			response.flushBuffer();
		}
	}

	@RequestMapping(value="/admin/createDocument")
	public Response<Document> createDocument(@RequestParam("name") String name,
											@RequestParam("statusString") String statusString,
											@RequestParam("documentFunction") String documentFunction,
											@RequestParam("userId") User user,
											@RequestParam("external") boolean external,
											Principal principal) throws Exception {

		return documentService.createDocument(name, statusString, documentFunction, user.getGuid(), external, principal);
	}

	@RequestMapping(value="/admin/updateDocument", method=RequestMethod.POST)
	public Response<Document> updateDocument(@RequestBody Document document, Principal principal) throws Exception {

		return documentService.updateDocument(document.getUuid(), document.getName(), document.getStatusName(), document.getFunctionName(), document.isArchived(), document.isDeleted(), principal);

	}
}
