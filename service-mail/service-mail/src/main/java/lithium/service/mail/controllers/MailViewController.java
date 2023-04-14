package lithium.service.mail.controllers;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

import java.security.Principal;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.DomainValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.mail.data.entities.Email;
import lithium.service.mail.services.MailService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/mail")
public class MailViewController {
	@Autowired MailService mailService;
	
	@GetMapping("/findByUser/table")
	private DataTableResponse<Email> findByUser(@RequestParam String userGuid, DataTableRequest request, LithiumTokenUtil tokenUtil) throws Status500InternalServerErrorException {
		DomainValidationUtil.validate(userGuid.split("/")[0], tokenUtil, "MAIL_QUEUE_VIEW", "PLAYER_MAIL_HISTORY_VIEW");
		Page<Email> userMail = mailService.findByUser(userGuid, request.getSearchValue(), request.getPageRequest());
		return new DataTableResponse<>(request, userMail);
	}
	
	@GetMapping("/findOne/{id}")
	private Response<Email> findOne(@PathVariable Long id, LithiumTokenUtil tokenUtil) {
		try {
			Email email = mailService.findOne(id);
			DomainValidationUtil.validate(email.getDomain().getName(), tokenUtil, "MAIL_QUEUE_VIEW", "PLAYER_MAIL_HISTORY_VIEW");
			return Response.<Email>builder().data(email).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Email>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@GetMapping("/findByDomain/table")
	private DataTableResponse<Email> findByDomainTable(@RequestParam(required=false) String domainNamesCommaSeparated,
	                                                   @RequestParam boolean showSent,
	                                                   @RequestParam(required = false, defaultValue = "false") boolean showFailed,
	                                                   @RequestParam String createdDateStart, @RequestParam String createdDateEnd,
													   @RequestParam(required = false) Long mailTemplate,
	                                                   DataTableRequest request, LithiumTokenUtil tokenUtil) {
		Page<Email> queue = mailService.findByDomain(domainNamesCommaSeparated, showSent, showFailed, createdDateStart, createdDateEnd, mailTemplate, request.getSearchValue(), request.getPageRequest(), tokenUtil);
		return new DataTableResponse<>(request, queue);
	}
}