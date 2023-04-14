package lithium.service.user.mock.vipps.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import lithium.service.user.mock.vipps.service.MockService;
import lithium.service.user.provider.vipps.domain.Address;
import lithium.service.user.provider.vipps.domain.CallbackRequest;
import lithium.service.user.provider.vipps.domain.CallbackRequest.Status;
import lithium.service.user.provider.vipps.domain.SignupOrLoginRequest;
import lithium.service.user.provider.vipps.domain.SignupOrLoginResponse;
import lithium.service.user.provider.vipps.domain.UserDetails;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping(value="/signup/v1")
public class LoginRequestController {
	@Autowired
	private MockService mockService;
	
	@GetMapping("/loginRequests/{xRequestId}")
	@ResponseBody
	public CallbackRequest get(
		@PathVariable("xRequestId") String xRequestId
	) {
		log.info("@GetMapping(\"/loginRequests/"+xRequestId+"\")");
		CallbackRequest cr = CallbackRequest.builder()
		.requestId(xRequestId)
		.status(Status.SUCCESS)
		.userDetails(
			UserDetails.builder()
			.userId("0825623882")
			.firstName("Riaan")
			.lastName("Schoeman")
			.email("vipps@riaan.playsafesa.com")
			.mobileNumber("0825623882")
			.ssn("")
			.address(
				Address.builder()
				.addressLine1("addressLine1")
				.addressLine2("addressLine2")
				.build()
			)
			.build()
		)
		.build();
		log.info("CallbackRequest : "+cr);
		return cr;
	}
	
	@PostMapping("/loginRequests")
	@ResponseBody
	public SignupOrLoginResponse post(
		@RequestBody SignupOrLoginRequest loginRequest,
		HttpServletRequest httpServletRequest,
		WebRequest request
	) throws UnsupportedEncodingException {
		log.info("@PostMapping(\"/loginRequests\") : "+loginRequest);
		String authorization = request.getHeader("Authorization");
		String xRequestId = request.getHeader("X-Request-Id");
		String xTimeStamp = request.getHeader("X-TimeStamp");
		String contentType = request.getHeader("Content-Type");
		String subscriptionKey = request.getHeader("Ocp-Apim-Subscription-Key");
		
		if (mockService.validLoginRequestHeaders(authorization, xRequestId, xTimeStamp, contentType, subscriptionKey)) {
			SignupOrLoginResponse loginResponse = SignupOrLoginResponse.builder()
			.requestId(xRequestId)
			.url(mockService.getUrl()+"/login/v1/?requestId="+xRequestId+"&fallBack="+URLEncoder.encode(loginRequest.getMerchantInfo().getFallBack(), "UTF-8")+"&callbackPrefix="+URLEncoder.encode(loginRequest.getMerchantInfo().getCallbackPrefix(), "UTF-8")+"&consentRemovalPrefix="+URLEncoder.encode(loginRequest.getMerchantInfo().getConsentRemovalPrefix(), "UTF-8"))
			.build();
			log.info("SignupOrLoginResponse : "+loginResponse);
			return loginResponse;
		} else {
			SignupOrLoginResponse loginResponse = SignupOrLoginResponse.builder().build();
			loginResponse.addErrorDetail("unauthorized_client", "Wrong headers supplied.");
			log.warn("SignupOrLoginResponse : "+loginResponse);
			return loginResponse;
		}
	}
}