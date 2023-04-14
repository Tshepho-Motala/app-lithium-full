package lithium.service.user.controllers;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

import lithium.service.domain.client.util.LocaleContextProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.user.services.EmailValidationService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/{domainName}/{emailOrUsername}/emailvalidation")
@Slf4j
public class EmailValidationController {
	@Autowired EmailValidationService emailValidationService;
  @Autowired LocaleContextProcessor localeContextProcessor;

	@PostMapping("/step1")
	public Response<String> validateEmail(
	    @PathVariable String domainName,
      @PathVariable String emailOrUsername,
      @RequestParam(value = "locale", required = false) String locale) {
		try {
      localeContextProcessor.setLocaleContextHolder(locale, domainName);
			emailValidationService.sendEmailValidationTokenEmail(domainName, emailOrUsername, false, false, false);
			return Response.<String>builder().data(emailOrUsername).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<String>builder().data(emailOrUsername).status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@PostMapping("/step2")
	public Response<String> validateTokenAndSetEmailValidated(
	    @PathVariable String domainName, @PathVariable String emailOrUsername,
      @RequestParam(required=false) Boolean resend, @RequestParam String token,
      @RequestParam(value = "locale", required = false) String locale) {
		Status status = Status.INTERNAL_SERVER_ERROR;
		
		try {
      localeContextProcessor.setLocaleContextHolder(locale, domainName);
      status = emailValidationService.validateTokenAndSetEmailValidated(domainName, emailOrUsername, resend, token);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return Response.<String>builder().data(emailOrUsername).status(status).build();
	}
	
	@PostMapping("/v2/step1")
	public Response<String> validateEmailNumericToken(
	    @PathVariable String domainName,
      @PathVariable String emailOrUsername,
      @RequestParam(value = "locale", required = false) String locale) {
		try {
      localeContextProcessor.setLocaleContextHolder(locale, domainName);
      emailValidationService.sendEmailValidationTokenEmailNumericToken(domainName, emailOrUsername, false, false, false);
			return Response.<String>builder().data(emailOrUsername).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<String>builder().data(emailOrUsername).status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@PostMapping("/v2/step2")
	public Response<String> validateTokenAndSetEmailValidatedNumericToken(
	    @PathVariable String domainName, @PathVariable String emailOrUsername,
      @RequestParam(required=false) Boolean resend, @RequestParam String token,
      @RequestParam(value = "locale", required = false) String locale) {
		return validateTokenAndSetEmailValidated(domainName, emailOrUsername, resend, token, locale);
	}
}
