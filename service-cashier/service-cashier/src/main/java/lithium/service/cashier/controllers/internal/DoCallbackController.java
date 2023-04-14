package lithium.service.cashier.controllers.internal;

import lithium.service.cashier.client.frontend.DoResponse;
import lithium.service.domain.client.util.LocaleContextProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.machine.DoMachine;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/internal")
public class DoCallbackController {

	@Autowired WebApplicationContext beanContext;
	@Autowired LocaleContextProcessor localeContextProcessor;

	@RequestMapping(method = RequestMethod.POST, path="/docallback")
	public Response<String> doCallback(
			@RequestBody DoProcessorResponse response,
			@RequestParam(value = "locale", required = false) String locale) throws Exception {
		try {
			localeContextProcessor.setLocaleContextHolder(locale);
			DoMachine machine = beanContext.getBean(DoMachine.class);
			DoResponse doResponse = machine.processorCallback(response, false);
			log.debug("internal docallback " + response + " " + doResponse);
		} catch (Exception e) {
			log.error("internal docallback exception " + e.getMessage() + " " + response, e);
			return Response.<String>builder()
					.data(e.getMessage())
					.status(Status.INTERNAL_SERVER_ERROR)
					.message(e.getMessage())
					.build();
		}
		return Response.<String>builder()
				.data("OK")
				.status(Status.OK)
				.message("OK")
				.build();
	}

	@RequestMapping(method = RequestMethod.POST, path="/dosafecallback")
	public Response<DoResponse> doSafeCallback(
			@RequestBody DoProcessorResponse response,
			@RequestParam(value = "locale", required = false) String locale) throws Exception {
		localeContextProcessor.setLocaleContextHolder(locale);
		DoResponse doResponse = null;
		try {
			DoMachine machine = beanContext.getBean(DoMachine.class);
			doResponse = machine.processorCallback(response, true);
			log.debug("internal docallback " + response + " " + doResponse);
		} catch (Exception e) {
			log.error("internal docallback exception " + e.getMessage() + " " + response, e);
			return Response.<DoResponse>builder()
					.data(null)
					.status(Status.INTERNAL_SERVER_ERROR)
					.message(e.getMessage())
					.build();
		}
		return Response.<DoResponse>builder()
				.data(doResponse)
				.status(Status.OK)
				.message("OK")
				.build();
	}
	
	@RequestMapping(method = RequestMethod.POST, path="/docallbackgettransaction")
	public Response<DoProcessorRequest> doCallbackGetTransaction(
		@RequestParam("transactionId") long transactionId,
		@RequestParam("processorCode") String processorCode
	) {
		try {
			DoMachine machine = beanContext.getBean(DoMachine.class);
			DoProcessorRequest request = machine.processorCallbackGetTransaction(transactionId, processorCode);
			return Response.<DoProcessorRequest>builder()
//					.data(checkOutOfBandTransaction(machine, request))
					.data(request)
					.status(Status.OK)
					.message("OK")
					.build();
		} catch (Exception e) {
			log.error("/docallbackgettransaction :: "+e.getMessage(), e);
			return Response.<DoProcessorRequest>builder()
					.status(Status.INTERNAL_SERVER_ERROR)
					.message(e.getMessage())
					.build();
		}
	}
//
//	private DoProcessorRequest checkOutOfBandTransaction(DoMachine machine, DoProcessorRequest request) {
//		Boolean oob = null;
//		try {
//			oob = Boolean.parseBoolean(String.valueOf(request.getProperty("oob")));
//			if (oob) {
//				log.info("Processing possible Out Of Band Transfer..");
//				return machine.createOutOfBandTransaction(request);
//			}
//		} catch (Exception e) {
//			log.error("Error checking out of band transfer status.", e);
//		}
//		return request;
//	}

	@RequestMapping(method = RequestMethod.POST, path="/docallbackgettransactionfromreferenceoob")
	public Response<DoProcessorRequest> doCallbackGetTransaction(
			@RequestParam("transactionId") long transactionId,
			@RequestParam("processorReference") String processorReference,
			@RequestParam("processorCode") String processorCode,
			@RequestParam(required = false, name = "checkOOB", defaultValue = "false") Boolean checkOOB
	) {
		try {
			log.info("docallbackgettransactionfromreferenceoob :: tranId:"+transactionId+" ref:"+processorReference+" processorCode:"+processorCode+" checkOOB:"+checkOOB);
			DoMachine machine = beanContext.getBean(DoMachine.class);
			DoProcessorRequest request = machine.processorCallbackGetTransaction(transactionId, processorReference.trim(), processorCode.trim(), checkOOB);
			return Response.<DoProcessorRequest>builder()
				.data(request)
				.status(Status.OK)
				.message("OK")
				.build();
		} catch (Exception e) {
			log.error("/docallbackgettransactionfromreferenceoob :: "+e.getMessage(), e);
			return Response.<DoProcessorRequest>builder()
				.status(Status.INTERNAL_SERVER_ERROR)
				.message(e.getMessage())
				.build();
		}
	}

	@RequestMapping(method = RequestMethod.POST, path="/docallbackgettransactionfromreference")
	public Response<DoProcessorRequest> doCallbackGetTransaction(
		@RequestParam("processorReference") String processorReference,
		@RequestParam("processorCode") String processorCode
	) {
		try {
			DoMachine machine = beanContext.getBean(DoMachine.class);
			DoProcessorRequest request = machine.processorCallbackGetTransaction(processorReference, processorCode);
			return Response.<DoProcessorRequest>builder()
					.data(request)
					.status(Status.OK)
					.message("OK")
					.build();
		} catch (Exception e) {
			log.error("/docallbackgettransactionfromreference :: "+e.getMessage(), e);
			return Response.<DoProcessorRequest>builder()
					.status(Status.INTERNAL_SERVER_ERROR)
					.message(e.getMessage())
					.build();
		}
	}

	@RequestMapping(method = RequestMethod.POST, path="/do-callback-get-transaction-from-additional-reference")
	public Response<DoProcessorRequest> doCallbackGetTransactionByAdditionalReference(
		@RequestParam("additionalReference") String additionalReference,
		@RequestParam("processorCode") String processorCode
	) {
		try {
			DoMachine machine = beanContext.getBean(DoMachine.class);
			DoProcessorRequest request = machine.processorCallbackGetTransactionByAdditionalReference(additionalReference, processorCode);
			return Response.<DoProcessorRequest>builder()
					.data(request)
					.status(Status.OK)
					.message("OK")
					.build();
		} catch (Exception e) {
			log.error("/do-callback-get-transaction-from-additional-reference :: "+e.getMessage(), e);
			return Response.<DoProcessorRequest>builder()
					.status(Status.INTERNAL_SERVER_ERROR)
					.message(e.getMessage())
					.build();
		}
	}
}
