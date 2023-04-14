package lithium.service.user.mock.vipps.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import lithium.service.user.mock.vipps.domain.LoginForm;
import lithium.service.user.mock.vipps.service.MockService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping(value="/login/v1")
public class LoginWebController {
	@Autowired
	private MockService mockService;

	@GetMapping("/")
	public String login(
		@RequestParam("callbackPrefix") String callbackPrefix,
		@RequestParam("requestId") String requestId,
		@RequestParam("fallBack") String fallBack,
		@RequestParam("consentRemovalPrefix") String consentRemovalPrefix,
		WebRequest request,
		Model model
	) {
		LoginForm loginForm = LoginForm.builder()
		.requestId(requestId)
		.fallBack(fallBack)
		.callbackPrefix(callbackPrefix)
		.consentRemovalPrefix(consentRemovalPrefix)
		.build();
		model.addAttribute("loginForm", loginForm);
		log.info("GET loginRequests : "+loginForm);
		
		return "login";
	}
	
	@PostMapping("/")
	public String loginPost(
		@ModelAttribute("loginForm") LoginForm loginForm,
		Model model,
		WebRequest request,
		HttpServletResponse response
	) throws IOException {
		model.addAttribute("loginForm", loginForm);
		log.info("POST loginRequests : "+request);
		log.info("Model : "+model);
		
		if (request.getParameter("allow") != null) {
			log.info("ALLOW");
			mockService.doCallbackRequestAllow(loginForm);
			response.sendRedirect(loginForm.getFallBack());
		} else if (request.getParameter("deny") != null) {
			log.info("DENY");
			mockService.doCallbackRequestDeny(loginForm);
			response.sendRedirect(loginForm.getFallBack());
		} else if (request.getParameter("revoke") != null) {
			log.info("REVOKE");
			mockService.doCallbackRequestRevoke(loginForm);
		}
		
		return "login";
	}
}