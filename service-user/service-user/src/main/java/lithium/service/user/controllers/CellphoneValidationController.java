package lithium.service.user.controllers;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.user.services.EmailValidationService;
import lithium.service.user.services.SMSValidationService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/{domainName}/{emailOrUsernameOrPhoneNumber}/cellphonevalidation")
@Slf4j
public class CellphoneValidationController {
	@Autowired SMSValidationService smsValidationService;
	
	@PostMapping("/step1")
	public Response<String> validateCellphone(@PathVariable String domainName, @PathVariable String emailOrUsernameOrPhoneNumber) {
		try {
			smsValidationService.sendCellphoneValidationTokenSms(domainName, emailOrUsernameOrPhoneNumber, false, false);
			return Response.<String>builder().data(emailOrUsernameOrPhoneNumber).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<String>builder().data(emailOrUsernameOrPhoneNumber).status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@PostMapping("/step2")
	public Response<String> validateTokenAndSetCellphoneValidated(@PathVariable String domainName, @PathVariable String emailOrUsernameOrPhoneNumber, @RequestParam(required=false) Boolean resend, @RequestParam String token) {
		Status status = Status.INTERNAL_SERVER_ERROR;
		
		try {
			status = smsValidationService.validateTokenAndSetCellphoneValidated(domainName, emailOrUsernameOrPhoneNumber, resend, token);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return Response.<String>builder().data(emailOrUsernameOrPhoneNumber).status(status).build();
	}
}