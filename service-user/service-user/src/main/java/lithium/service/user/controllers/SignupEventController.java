package lithium.service.user.controllers;

import static lithium.service.Response.Status.NOT_FOUND;
import static lithium.service.Response.Status.OK;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.DomainValidationUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.user.data.entities.SignupEvent;

@RestController
@RequestMapping("/signupevent/{id}")
public class SignupEventController {
	@GetMapping
	public Response<SignupEvent> get(@PathVariable("id") SignupEvent signupEvent, LithiumTokenUtil tokenUtil)
      throws Status500InternalServerErrorException {
		if (signupEvent == null) {
			return Response.<SignupEvent>builder().status(NOT_FOUND).build();
		}
    DomainValidationUtil.validate(signupEvent.getDomain().getName(), "SIGNUPEVENTS_VIEW", tokenUtil);
		return Response.<SignupEvent>builder().data(signupEvent).status(OK).build();
	}
}
