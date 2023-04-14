package lithium.service.user.controllers;

import static lithium.service.Response.Status.CONFLICT;
import static lithium.service.Response.Status.OK;

import java.security.Principal;
import javax.servlet.http.HttpServletResponse;
import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.exceptions.Status493ExcessiveFailedPasswordResetBlockException;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.user.client.objects.UserPasswordReset;
import lithium.service.user.enums.TokenType;
import lithium.service.user.enums.TokenTypeConverter;
import lithium.service.user.enums.Type;
import lithium.service.user.enums.TypeConverter;
import lithium.service.user.exceptions.Status100InvalidInputDataException;
import lithium.service.user.exceptions.Status422InvalidDateOfBirthException;
import lithium.service.user.exceptions.Status424InvalidResetTokenException;
import lithium.service.user.exceptions.Status999GeneralFailureException;
import lithium.service.user.services.PasswordResetService;
import lithium.service.user.services.UserValidationBaseService;
import lithium.tokens.LithiumTokenUtil;
import lithium.tokens.LithiumTokenUtilService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/passwordreset/")
@Slf4j
public class PasswordResetCombinedController extends UserValidationBaseService {
	@Autowired PasswordResetService passwordResetService;
  @Autowired LithiumTokenUtilService tokenService;
	@Autowired @Setter
  CachingDomainClientService cachingDomainClientService;

	@ExceptionHandler(ErrorCodeException.class)
	public Response handleErrorCodeException(ErrorCodeException ex, HttpServletResponse response) {
		log.trace("PasswordResetCombinedController:ErrorCodeException " + ex.getCode() + " " + ex.getMessage());
		return Response.<String>builder().message(ex.getMessage()).status(Response.Status.fromId(ex.getCode())).build();
	}

	@PostMapping("/1")
	public Response<String> step1(
		@RequestParam String domainName,
		@RequestParam(required = false) String email,
		@RequestParam(required = false) String username,
		@RequestParam(required = false) String mobile,
		@RequestParam(defaultValue = "email") Type type,
		@RequestParam(name = "token", defaultValue = "n") TokenType tokenType,
		@RequestParam(defaultValue = "5") Integer tokenlength,
		@RequestParam(required = false) String dateOfBirth //@DateTimeFormat(pattern="dd/MM/yyyy")
	) throws
			Status100InvalidInputDataException,
			Status405UserDisabledException,
			Status422InvalidDateOfBirthException,
			Status490SoftSelfExclusionException,
			Status491PermanentSelfExclusionException,
			Status493ExcessiveFailedPasswordResetBlockException,
			Status496PlayerCoolingOffException,
			Status999GeneralFailureException
	{
		if (type == null) type = Type.EMAIL;
		if (tokenType == null) tokenType = TokenType.NUMERIC;
		String parameterStr = "dn: "+domainName+", e: "+email+", u: "+username+", m: "+mobile+", t: "+type.name()
				+", tokenType: "+tokenType.name()+", l: "+tokenlength+", dob: "+dateOfBirth;
		log.debug("PasswordReset : Step 1 :: " + parameterStr);
		passwordResetService.step1(domainName, email, username, mobile, type, tokenType, tokenlength, dateOfBirth, null);

		return Response.<String>builder().status(OK).build();
	}

	@PostMapping("/1/backoffice")
	public Response<String> step1BackOffice(
			@RequestParam String domainName,
			@RequestParam(required = false) String email,
			@RequestParam(required = false) String username,
			@RequestParam(required = false) String mobile,
			@RequestParam(defaultValue = "email") Type type,
			@RequestParam(name = "token", defaultValue = "n") TokenType tokenType,
			@RequestParam(defaultValue = "5") Integer tokenlength,
			@RequestParam(required = false) String dateOfBirth, //@DateTimeFormat(pattern="dd/MM/yyyy")
      Principal principal
	) throws
			Status100InvalidInputDataException,
			Status405UserDisabledException,
			Status422InvalidDateOfBirthException,
			Status490SoftSelfExclusionException,
			Status491PermanentSelfExclusionException,
			Status493ExcessiveFailedPasswordResetBlockException,
			Status496PlayerCoolingOffException,
			Status999GeneralFailureException, LithiumServiceClientFactoryException {
		if (type == null) type = Type.EMAIL;
		if (tokenType == null) tokenType = TokenType.NUMERIC;
		String parameterStr = "dn: "+domainName+", e: "+email+", u: "+username+", m: "+mobile+", t: "+type.name()
				+", tokenType: "+tokenType.name()+", l: "+tokenlength+", dob: "+dateOfBirth;
		log.debug("PasswordReset : Step 1 :: " + parameterStr);
		Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
		if(domain.getPlayers()){
			return Response.<String>builder().status(CONFLICT).build();
		}
    	LithiumTokenUtil wantedPrincipal = null;
		if (principal != null) {
      wantedPrincipal = tokenService.getUtil(principal);
		};
    	passwordResetService.step1(domainName, email, username, mobile, type, tokenType, tokenlength, dateOfBirth, wantedPrincipal);
		return Response.<String>builder().status(OK).build();
	}


	@PostMapping("/2")
	public Response<?> step2(
		@RequestParam String domainName,
		@RequestParam(required = false) String email,
		@RequestParam(required = false) String username,
		@RequestParam(required = false) String mobile,
		@RequestBody UserPasswordReset userPasswordReset
	) throws
		Status100InvalidInputDataException,
		Status424InvalidResetTokenException,
		Status493ExcessiveFailedPasswordResetBlockException,
		Status999GeneralFailureException
	{
		String parameterStr = "dn: "+domainName+", e: "+email+", u: "+username+", m: "+mobile+", t: "+userPasswordReset;
		log.debug("PasswordReset : Step 2 :: "+parameterStr);
		passwordResetService.step2(domainName, email, username, mobile, userPasswordReset, null);
		return Response.<String>builder().status(OK).build();
	}

	@InitBinder
	public void initBinder(final WebDataBinder webdataBinder) {
		webdataBinder.registerCustomEditor(Type.class, new TypeConverter());
		webdataBinder.registerCustomEditor(TokenType.class, new TokenTypeConverter());
	}
}
