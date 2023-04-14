package lithium.service.accounting.client;

import lithium.service.accounting.objects.NetDeposit;
import lithium.service.accounting.objects.frontend.FrontendSummary;
import lithium.service.accounting.objects.frontend.FrontendTransaction;
import lithium.service.client.datatable.DataTableResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="service-accounting", path="/system")
public interface AccountingFrontendClient {

	@RequestMapping("/summary")
	FrontendSummary summary(
		@RequestParam("dateStart") @DateTimeFormat(pattern="yyyy-MM-dd") String dateStart,
		@RequestParam("dateEnd") @DateTimeFormat(pattern="yyyy-MM-dd") String dateEnd,
		@RequestParam("currencyCode") String currencyCode,
		@RequestParam("guid") String guid
	) throws Exception;

	@RequestMapping("/transactions")
	DataTableResponse<FrontendTransaction> transactions(
		@RequestParam("dateStart") @DateTimeFormat(pattern="yyyy-MM-dd") String dateStart,
		@RequestParam("dateEnd") @DateTimeFormat(pattern="yyyy-MM-dd") String dateEnd,
		@RequestParam("currencyCode") String currencyCode,
		@RequestParam("pageSize") int pageSize,
		@RequestParam("page") int page,
		@RequestParam("guid") String guid,
		@RequestParam("locale") String locale
	) throws Exception;

	@RequestMapping("/netdeposit")
	NetDeposit getNetDeposit(
			@RequestParam("guid")String guid
	) throws Exception;
}
