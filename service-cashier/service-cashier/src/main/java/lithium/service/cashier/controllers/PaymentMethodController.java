package lithium.service.cashier.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.data.views.Views;
import lithium.service.cashier.services.ProcessorAccountService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/cashier/pmc")
public class PaymentMethodController {

	@Autowired
	private ProcessorAccountService processorAccountService;

	@GetMapping("/withdraw-processor-accounts-per-user-method")
	@JsonView(Views.Public.class)
	public Response<List<ProcessorAccount>> getWithdrawProcessorAccounts(
			@RequestParam String methodCode,
			@RequestParam String userGuid
			) {
		try {
			log.info("Fetching methodCode={}, userGuid={}", methodCode, userGuid);
			List<ProcessorAccount> processorAccounts = processorAccountService.getProcessorAccounts(userGuid, false)
					.stream()
					.filter(processorAccount -> processorAccount.getMethodCode().equals(methodCode))
					.collect(Collectors.toList());

			for (ProcessorAccount processorAccount : processorAccounts) {
				processorAccount.setDescriptor(processorAccount.getDescriptor() + " ( "+ processorAccount.getStatus().getName() + " ) ");
			}

			return Response.<List<ProcessorAccount>>builder().data(processorAccounts).status(Status.OK).build();
		} catch (Exception e) {
			log.error("Error while getting user cards, user=" + userGuid + ", " + e.getMessage(), e);
			return Response.<List<ProcessorAccount>>builder().status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
}
