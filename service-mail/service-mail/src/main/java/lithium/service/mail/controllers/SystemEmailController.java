package lithium.service.mail.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.mail.client.SystemMailClient;
import lithium.service.mail.client.objects.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.mail.client.objects.SystemEmailData;
import lithium.service.mail.services.MailService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/mail/system")
@Slf4j
public class SystemEmailController implements SystemMailClient {

	@Autowired
	private MailService mailService;

	@Autowired
	private ObjectMapper objectMapper;
	
	
	@PostMapping("/send")
	public Response<Email> save(@RequestBody SystemEmailData systemMail) throws Exception {
		lithium.service.mail.data.entities.Email email = mailService.saveSystemEmail(systemMail);
		
		if (email.getId() <= 0L) {
			log.error("Problem saving system email for : " + email.getUser().getGuid());
			log.debug("Problem saving system email: " + email);
			return Response.<Email>builder().data(convertToDto(email)).status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return Response.<Email>builder().data(convertToDto(email)).status(Status.OK).build();
	}

	@PostMapping("/find")
	public DataTableResponse<Email> find(String guid, int page, int size) {

		PageRequest pageRequest = PageRequest.of(page, size);
		Page<lithium.service.mail.data.entities.Email> emails = mailService.findByUser(guid, null, pageRequest);
		Page<Email> emailsDto = emails.map(this::convertToDto);
		return new DataTableResponse<>(
				new DataTableRequest(pageRequest, null, null),
				emailsDto,
				emails.getTotalElements(),
				emails.getPageable().getPageNumber(),
				emails.getTotalPages()
		);
	}

	private Email convertToDto (lithium.service.mail.data.entities.Email email) {
		return objectMapper.convertValue(email, Email.class);
	}
}
