package lithium.service.cashier.controllers.internal;

import lithium.service.Response;
import lithium.service.cashier.client.CashierProcessorAccountVerifyInternalClient;
import lithium.service.cashier.client.internal.VerifyProcessorAccountRequest;
import lithium.service.cashier.client.internal.VerifyProcessorAccountResponse;
import lithium.service.cashier.services.ProcessorAccountVerificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/internal")
public class ProcessorAccountVerificationController implements CashierProcessorAccountVerifyInternalClient {
    @Autowired
    ProcessorAccountVerificationService processorAccountVerificationService;

	@RequestMapping(path="/processor-account/verify", method=RequestMethod.POST)
	public Response<VerifyProcessorAccountResponse> verifyAccount(@RequestBody VerifyProcessorAccountRequest request) {
		try {
			return Response.<VerifyProcessorAccountResponse>builder()
				.data(processorAccountVerificationService.verify(request))
				.status(Response.Status.OK)
				.build();
		} catch (Exception e) {
			log.error("Failed to perform user processor account " + request.getVerifications().stream().map(v -> v.toString()).collect(Collectors.joining(", ")) + "varification. User guid: "+ request.getUserGuid() + "Exception: " + e.getMessage(), e);
			return Response.<VerifyProcessorAccountResponse>builder()
				.status(Response.Status.INTERNAL_SERVER_ERROR)
				.message(e.getMessage())
				.build();
		}
	}
}
