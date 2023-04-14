package lithium.service.document.controllers;

import lithium.service.document.data.entities.DocumentType;
import lithium.service.document.services.DocumentTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import static java.util.Objects.nonNull;

@Slf4j
@RestController
@RequestMapping("/public/files/")
public class PublicDocumentController {

	@Autowired
	private DocumentTypeService documentTypeService;

	@RequestMapping(value = "/typeIcon/{id}", method = RequestMethod.GET)
	@ResponseBody
	public void downloadIconFile(@PathVariable("id") Long id, HttpServletResponse response) throws Exception {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setCacheControl(CacheControl.noStore().getHeaderValue());

			DocumentType documentType = documentTypeService.getDocumentType(id);

			if (nonNull(documentType)) {
				response.setHeader(HttpHeaders.CACHE_CONTROL, CacheControl.noStore().getHeaderValue());
				response.setContentType(documentType.getIconType());
				response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"" + documentType.getIconName() +"\""));
				if (nonNull(documentType.getIconBase64())) {
					response.getOutputStream().write(documentType.getIconBase64());
				}
			}

		} finally {
			response.flushBuffer();
		}
	}

}
