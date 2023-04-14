package lithium.service.cashier.controllers.external;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.WebRequest;
import lithium.service.domain.client.util.LocaleContextProcessor;
import lithium.service.cashier.client.external.DoProcessorCallbackClient;
import lithium.service.cashier.client.external.DoProcessorCallbackRequest;
import lithium.service.cashier.client.external.DoProcessorCallbackResponse;
import lithium.service.cashier.client.frontend.DoResponse;
import lithium.service.cashier.data.entities.Processor;
import lithium.service.cashier.data.repositories.ProcessorRepository;
import lithium.service.cashier.machine.DoMachine;
import lithium.service.client.LithiumServiceClientFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/external")
public class DoProcessorExternalCallbackController {
	@Autowired
	private LithiumServiceClientFactory serviceFactory;
	@Autowired
	private ProcessorRepository processorRepository;
	@Autowired
	private WebApplicationContext beanContext;
	@Autowired
	LocaleContextProcessor localeContextProcessor;

	@RequestMapping
	public String test() {
		return "test";
	}
	
	private void checkHash(String url, String hash) throws Exception {
		String sha1hex = DigestUtils.sha1Hex(url);
		log.debug("Processor Callback Hash Check :: Received : "+hash+" Computed : "+sha1hex);
		if (!hash.equals(sha1hex)) {
			log.warn("Processor Callback Hash Check FAILED :: Received : "+hash+" Computed : "+sha1hex);
			throw new Exception("Hash Check Failed");
		}
	}
	
	private DoProcessorCallbackClient getClient(String processorCode, String hash) throws Exception {
		DoProcessorCallbackClient client = null;
		Processor p = processorRepository.findByCode(processorCode);
		if (p == null) throw new Exception("Callback received from unknown processor : "+processorCode);
		checkHash(p.getUrl(), hash);
		client = serviceFactory.target(DoProcessorCallbackClient.class, p.getUrl(), true);
		return client;
	}
	
	private DoProcessorCallbackRequest buildRequest(WebRequest request, String processorCode, String hash) {
		return buildRequest(request, null, processorCode, hash);
	}
	
	private DoProcessorCallbackRequest buildRequest(WebRequest request, Object requestBody, String processorCode, String hash) {
		Map<String, String[]> headers = new HashMap<>();
		Map<String, String> parameters = new HashMap<>();
		
		request.getHeaderNames().forEachRemaining(hn -> headers.put(hn.toLowerCase(), request.getHeaderValues(hn)));
		request.getParameterNames().forEachRemaining(pn -> parameters.put(pn, request.getParameter(pn)));
		
		return DoProcessorCallbackRequest.builder()
		.requestBody(requestBody)
		.contextPath(request.getContextPath())
		.locale(request.getLocale())
		.parameterMap(parameters)
		.headerMap(headers)
		.processorCode(processorCode)
		.hash(hash)
		.build();
	}
	
	private DoResponse doMachineProcess(DoProcessorCallbackResponse doProcessorCallbackResponse, boolean isSafe) throws Exception {
		DoMachine machine = beanContext.getBean(DoMachine.class);
		DoResponse doResponse = machine.processorCallback(doProcessorCallbackResponse.doProcessorResponse(), doProcessorCallbackResponse.getProcessorRequest(), isSafe);
		return doResponse;
	}
	
	private String callback(WebRequest webRequest, HttpServletResponse response, String processorCode, String hash, boolean isSafe) {
		return callback(webRequest, response, null, processorCode, hash, isSafe);
	}

	private String callback(WebRequest webRequest, HttpServletResponse response, Object requestBody, String processorCode, String hash, boolean isSafe) {
		try {
			DoProcessorCallbackResponse doProcessorCallbackResponse = null;
			if (requestBody != null) {
				doProcessorCallbackResponse = getClient(processorCode, hash).doCallback(buildRequest(webRequest, requestBody, processorCode, hash));
			} else {
				doProcessorCallbackResponse = getClient(processorCode, hash).doCallback(buildRequest(webRequest, processorCode, hash));
			}
			DoResponse doResponse = doMachineProcess(doProcessorCallbackResponse, isSafe);
			if (doResponse.getError() != null && doResponse.getError()) return "nok";
			if ((doProcessorCallbackResponse.getRedirect()!=null) && (!doProcessorCallbackResponse.getRedirect().isEmpty())) {
				response.sendRedirect(doProcessorCallbackResponse.getRedirect());
			}
			return doProcessorCallbackResponse.getCallbackResponse();
		} catch (Exception e) {
			log.error("callbackGet error", e);
		}
		return "nok";
	}

	@Deprecated()
	@GetMapping("/callback/do/{processorCode}/{hash}")
	public String callbackGet(
		@PathVariable("processorCode") String processorCode,
		@PathVariable("hash") String hash,
		WebRequest webRequest,
		HttpServletResponse response,
		@RequestParam(value = "locale", required = false) String locale
	) {
		localeContextProcessor.setLocaleContextHolder(locale);
		return callback(webRequest, response, processorCode, hash, false);
	}

	@Deprecated()
	@PostMapping("/callback/do/{processorCode}/{hash}")
	public String callbackPost(
		@PathVariable("processorCode") String processorCode,
		@PathVariable("hash") String hash,
		WebRequest webRequest,
		HttpServletResponse response,
		@RequestParam(value = "locale", required = false) String locale
	) {
		localeContextProcessor.setLocaleContextHolder(locale);
		return callback(webRequest, response, processorCode, hash, false);
	}

	@GetMapping("/safecallback/do/{processorCode}/{hash}")
	public String safeCallbackGet(
			@PathVariable("processorCode") String processorCode,
			@PathVariable("hash") String hash,
			WebRequest webRequest,
			HttpServletResponse response,
			@RequestParam(value = "locale", required = false) String locale
	) {
		localeContextProcessor.setLocaleContextHolder(locale);
		return callback(webRequest, response, processorCode, hash,true);
	}

	@PostMapping("/safecallback/do/{processorCode}/{hash}")
	public String sefeCallbackPost(
			@PathVariable("processorCode") String processorCode,
			@PathVariable("hash") String hash,
			WebRequest webRequest,
			HttpServletResponse response,
			@RequestParam(value = "locale", required = false) String locale
	) {
		localeContextProcessor.setLocaleContextHolder(locale);
		return callback(webRequest, response, processorCode, hash, true);
	}
}
