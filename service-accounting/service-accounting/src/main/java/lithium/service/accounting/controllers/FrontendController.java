package lithium.service.accounting.controllers;

import lithium.service.accounting.objects.NetDeposit;
import lithium.service.accounting.objects.frontend.FrontendSummary;
import lithium.service.accounting.objects.frontend.FrontendTransaction;
import lithium.service.accounting.service.AccountingService;
import lithium.service.client.datatable.DataTableResponse;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/frontend")
public class FrontendController {
	@Autowired AccountingService accountingService;

	@RequestMapping("/summary")
	public FrontendSummary summary(
		@RequestParam("dateStart") @DateTimeFormat(pattern="yyyy-MM-dd") String dateStart,
		@RequestParam("dateEnd") @DateTimeFormat(pattern="yyyy-MM-dd") String dateEnd,
		@RequestParam("currencyCode") String currencyCode,
		LithiumTokenUtil tokenUtil
	) throws Exception {
		String logMsg = ("player account summary request : g: "+tokenUtil.guid()+", un: "+tokenUtil.username()+", ds: "+dateStart+", de: "+dateEnd+", c: "+currencyCode);
		log.debug(logMsg);
		return accountingService.accountingFrontendClient().summary(
			dateStart, dateEnd, currencyCode, tokenUtil.guid()
		);
	}

	@RequestMapping("/transactions")
	public DataTableResponse<FrontendTransaction> transactions(
		@RequestParam("dateStart") @DateTimeFormat(pattern="yyyy-MM-dd") String dateStart,
		@RequestParam("dateEnd") @DateTimeFormat(pattern="yyyy-MM-dd") String dateEnd,
		@RequestParam("currencyCode") String currencyCode,
		@RequestParam("pageSize") int pageSize,
		@RequestParam("page") int page,
		@RequestParam(name="locale", required=false, defaultValue="en_US") String locale,
		LithiumTokenUtil tokenUtil
	) throws Exception {
		String logMsg = ("player account transactions request : g: "+tokenUtil.guid()+", un: "+tokenUtil.username()+", ds: "+dateStart+", de: "+dateEnd+", c: "+currencyCode+", p: "+page+", ps: "+pageSize);
		log.debug(logMsg);
		return accountingService.accountingFrontendClient().transactions(
			dateStart, dateEnd, currencyCode, pageSize, page, tokenUtil.guid(), locale
		);
	}

	@RequestMapping("/netdeposit")
	public NetDeposit getNetDeposit (LithiumTokenUtil tokenUtil) throws Exception {
		return accountingService.accountingFrontendClient().getNetDeposit(tokenUtil.guid());
	}
}
