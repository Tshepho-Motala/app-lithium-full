package lithium.service.cashier.controllers.internal;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.cashier.client.objects.ProcessedProcessorProperty;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.services.InternalMethodsService;

//@Slf4j
@RestController
@RequestMapping("/internal/methods")
public class InternalMethodsController {

//	@Autowired
//	private LithiumServiceClientFactory services;
//	@Autowired
//	private TokenStore tokenStore;
//	@Autowired
//	private CashierFrontendService cashierFrontendService;
	@Autowired
	private InternalMethodsService internalMethodsService;
	
//	@GetMapping("/{domainMethodId}")
//	public Response<?> processor(
//		@PathVariable("domainMethodId") DomainMethod domainMethod,
//		@RequestParam("userGuid") String userGuid
//	) {
//		return Response.<ProcessedMethod>builder().data(internalMethodsService.findProcessedProcessor(domainMethod, userGuid)).status(Status.OK).build();
//	}
	
	@GetMapping("/{domainMethodProcessorId}/properties")
	public Response<List<ProcessedProcessorProperty>> processorProperties(
		@PathVariable("domainMethodProcessorId") DomainMethodProcessor domainMethodProcessor
	) {
		return Response.<List<ProcessedProcessorProperty>>builder().data(internalMethodsService.findProcessedProcessorProperties(domainMethodProcessor)).status(Status.OK).build();
	}
	
//	@GetMapping("/deposit")
//	@JsonView(Views.ProcessedProcessor.class)
//	public Response<?> methodsDeposit(
//		@RequestParam("userGuid") String userGuid
//	) {
//		log.info("Lookup Deposit Methods for userName:"+userGuid.split("/")[1]+" domainName:"+userGuid.split("/")[0]);
//		
//		List<?> domainMethods = cashierFrontendService.methodsDeposit(userGuid.split("/")[1], userGuid.split("/")[0]);
//		
//		return Response.<List<?>>builder().data(domainMethods).status(Status.OK).build();
//	}
//	@GetMapping("/deposit/image")
//	@JsonView(Views.Image.class)
//	public Response<?> methodsDepositWithImage(
//		@RequestParam("userGuid") String userGuid
//	) {
//		return methodsDeposit(userGuid);
//	}
//	@GetMapping("/withdraw")
//	@JsonView(Views.ProcessedProcessor.class)
//	public Response<?> methodsWithdraw(
//		@RequestParam("userGuid") String userGuid
//	) {
//		log.info("Lookup Withdraw Methods for userName:"+userGuid.split("/")[1]+" domainName:"+userGuid.split("/")[0]);
//		
//		List<?> domainMethods = cashierFrontendService.methodsWithdraw(userGuid.split("/")[1], userGuid.split("/")[0]);
//		
//		return Response.<List<?>>builder().data(domainMethods).status(Status.OK).build();
//	}
//	@GetMapping("/withdraw/image")
//	@JsonView(Views.Image.class)
//	public Response<?> methodsWithdrawWithImage(
//		@RequestParam("userGuid") String userGuid
//	) {
//		return methodsWithdraw(userGuid);
//	}
}