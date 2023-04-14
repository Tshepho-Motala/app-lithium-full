package lithium.service.user.controllers;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.user.client.objects.UserPasswordReset;
import lithium.service.user.services.PasswordResetService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/{domainName}/{emailOrUsername}/passwordreset/")
@Slf4j
public class PasswordResetController {
	@Autowired
	PasswordResetService passwordResetService;

	@PostMapping("/step1")
	public Response<String> resetPasswordEmail(@PathVariable String domainName, @PathVariable String emailOrUsername) {
		try {
			passwordResetService.sendPasswordResetTokenEmail(domainName, emailOrUsername, false);
			return Response.<String>builder().data(emailOrUsername).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<String>builder().data(emailOrUsername).status(INTERNAL_SERVER_ERROR)
					.message(e.getMessage()).build();
		}
	}

	@PostMapping("/step1/decoded")
	public Response<String> numericCodeResetPasswordEmail(@PathVariable String domainName,
														  @PathVariable String emailOrUsername) {
		try {
			passwordResetService.sendPasswordResetTokenEmail(domainName, emailOrUsername, true);
			return Response.<String>builder().data(emailOrUsername).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<String>builder().data(emailOrUsername).status(INTERNAL_SERVER_ERROR)
					.message(e.getMessage()).build();
		}
	}

	@PostMapping("/step2/decoded")
	public Response<?> validateDecodedTokenAndResetPassword(@PathVariable String domainName,
															@PathVariable String emailOrUsername, @RequestBody UserPasswordReset userPasswordReset) {
		byte[] newToken = userPasswordReset.getToken().getBytes();
		Response<?> response;
		try {
			userPasswordReset.setToken(new String(java.util.Base64.getEncoder().encode(newToken), "UTF-8"));
			response = this.validateTokenAndResetPassword(domainName, emailOrUsername, userPasswordReset);
			if (log.isDebugEnabled()) log.debug(response.toString());
			return response;

		} catch (UnsupportedEncodingException e) {
			log.error("an error occurred , could not decode tokens");

			return Response.<String>builder().data(emailOrUsername).status(INTERNAL_SERVER_ERROR)
					.message(e.getMessage()).build();
		}
	}

	@PostMapping("/step2")
	public Response<?> validateTokenAndResetPassword(@PathVariable String domainName,
													 @PathVariable String emailOrUsername, @RequestBody UserPasswordReset userPasswordReset) {
		boolean success = false;
		try {
			success = passwordResetService.validateTokenAndResetPassword(domainName, emailOrUsername,
					userPasswordReset.getToken(), userPasswordReset.getPassword());
		} catch (Exception e) {
			success = false;
			log.error(e.getMessage(), e);
		}

		if (success) {
			return Response.<String>builder().data(emailOrUsername).status(OK).build();
		} else {
			return Response.<String>builder().data(emailOrUsername).status(INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/validate")
	public Response<?> validateToken(@PathVariable String domainName, @PathVariable String emailOrUsername,
									 @RequestBody UserPasswordReset userPasswordReset) {
		boolean success;
		try {
			success = passwordResetService.validateTokenOnly(domainName, emailOrUsername, userPasswordReset.getToken());
		} catch (Exception e) {
			success = false;
			log.error(e.getMessage(), e);
		}
		return Response.<String>builder().data(emailOrUsername).status(success ? OK : INTERNAL_SERVER_ERROR).build();
	}

	@PostMapping("/sms/step1")
	public Response<String> resetPasswordMobile(@PathVariable String domainName, @PathVariable String emailOrUsername) {
		try {
			passwordResetService.sendPasswordResetTokenSms(domainName, emailOrUsername);
			return Response.<String>builder().data(emailOrUsername).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<String>builder().data(emailOrUsername).status(INTERNAL_SERVER_ERROR)
					.message(e.getMessage()).build();
		}
	}

	@PostMapping("/sms/step2")
	public Response<?> validateTokenAndResetPasswordSms(@PathVariable String domainName,
														@PathVariable String emailOrUsername, @RequestBody UserPasswordReset userPasswordReset) {
		return validateTokenAndResetPassword(domainName, emailOrUsername, userPasswordReset);
	}
}
