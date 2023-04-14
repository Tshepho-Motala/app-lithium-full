package lithium.service.sms.controllers;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.sms.data.entities.SMS;
import lithium.service.sms.objects.PlayerSMSRequest;
import lithium.service.sms.services.SMSService;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.DomainValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/sms")
public class SMSController {
	@Autowired SMSService smsService;

	@PostMapping("/saveForPlayerWithText")
	public Response<Boolean> saveForPlayerWithText(@RequestBody PlayerSMSRequest playerSMSRequest, LithiumTokenUtil tokenUtil) {
		try {
			DomainValidationUtil.validate(playerSMSRequest.getPlayerGuid().split("/")[0], tokenUtil, "PLAYER_VIEW", "PLAYER_EDIT");
			smsService.saveForPlayerWithText(tokenUtil.guid(), playerSMSRequest.getPlayerGuid(), playerSMSRequest.getText());
			return Response.<Boolean>builder().data(true).status(Status.OK).build();
		} catch (Exception e) {
			return Response.<Boolean>builder().status(Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@GetMapping("/findOne/{id}")
	public Response<SMS> findOne(@PathVariable("id") Long id, LithiumTokenUtil tokenUtil) throws LithiumServiceClientFactoryException {
		try {
			SMS sms = smsService.findOne(id);
			DomainValidationUtil.validate(sms.getDomain().getName(), tokenUtil, "SMS_QUEUE_VIEW", "PLAYER_SMS_HISTORY_VIEW");
			return Response.<SMS>builder().data(sms).status(Status.OK).build();
		} catch (Exception e) {
			return Response.<SMS>builder().status(Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@GetMapping("/findByDomain/table")
	private DataTableResponse<SMS> findByDomainTable(@RequestParam(required=false) String domainNamesCommaSeparated,
	                                                 @RequestParam boolean showSent,
	                                                 @RequestParam(required = false, defaultValue = "false") boolean showFailed,
	                                                 @RequestParam String createdDateStart,
	                                                 @RequestParam String createdDateEnd,
	                                                 DataTableRequest request, LithiumTokenUtil tokenUtil) {
		Page<SMS> queue = smsService.findByDomain(domainNamesCommaSeparated, showSent, showFailed, createdDateStart,
				createdDateEnd, request.getSearchValue(), request.getPageRequest(), tokenUtil);
		return new DataTableResponse<>(request, queue);
	}
	
	@GetMapping("/findByUser/table")
	private DataTableResponse<SMS> findByUser(@RequestParam String userGuid, DataTableRequest request, LithiumTokenUtil tokenUtil) throws Status500InternalServerErrorException {
		DomainValidationUtil.validate(userGuid.split("/")[0], tokenUtil, "SMS_QUEUE_VIEW", "PLAYER_SMS_HISTORY_VIEW");
		Page<SMS> userSms = smsService.findByUser(userGuid, request.getSearchValue(), request.getPageRequest());
		return new DataTableResponse<>(request, userSms);
	}
}