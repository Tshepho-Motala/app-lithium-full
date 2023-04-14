package lithium.service.accounting.provider.internal.controllers;

import lithium.service.accounting.objects.NetDeposit;
import lithium.service.accounting.objects.frontend.FrontendSummary;
import lithium.service.accounting.objects.frontend.FrontendTransaction;
import lithium.service.accounting.provider.internal.services.PlayerService;
import lithium.service.client.datatable.DataTableResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/system")
public class FrontendController {
	@Autowired TokenStore tokenStore;
	@Autowired Environment environment;
	@Autowired PlayerService playerService;

	@RequestMapping("/summary")
	public FrontendSummary summary(
		@RequestParam("dateStart") @DateTimeFormat(pattern="yyyy-MM-dd") String dateStart,
		@RequestParam("dateEnd") @DateTimeFormat(pattern="yyyy-MM-dd") String dateEnd,
		@RequestParam("currencyCode") String currencyCode,
		@RequestParam("guid") String guid
	) throws Exception { //TODO: Needs to be replaced with ReturnCodeException.
		String logMsg = ("player account summary request : g: "+guid+", ds: "+dateStart+", de: "+dateEnd+", c: "+currencyCode);
		FrontendSummary result = playerService.summary(guid, dateStart, dateEnd, currencyCode);
		logMsg += ", result : "+result;
		log.debug(logMsg);
		return result;
	}

	@RequestMapping("/transactions")
	public DataTableResponse<FrontendTransaction> transactions(
		@RequestParam("dateStart") @DateTimeFormat(pattern="yyyy-MM-dd") String dateStart,
		@RequestParam("dateEnd") @DateTimeFormat(pattern="yyyy-MM-dd") String dateEnd,
		@RequestParam("currencyCode") String currencyCode,
		@RequestParam("pageSize") int pageSize,
		@RequestParam("page") int page,
		@RequestParam("guid") String guid,
		Locale locale
	) throws Exception { //TODO: Needs to be replaced with ReturnCodeException.
		String logMsg = ("player account transactions request : g: "+guid+", ds: "+dateStart+", de: "+dateEnd+", c: "+currencyCode+", p: "+page+", locale: "+locale);
		DataTableResponse<FrontendTransaction> result = playerService.transactions(guid, dateStart, dateEnd, currencyCode, pageSize, page, locale);
		logMsg += ", result : "+result;
		log.debug(logMsg);
		return result;
	}

	@RequestMapping("/netdeposit")
	public NetDeposit transactions(
			@RequestParam("guid") String guid
	) throws Exception {
		log.debug("player netDeposit request for guid:" + guid);
		NetDeposit result = playerService.calculateNetDeposit(guid);
		log.debug("Netdeposit result:{" + result + "}for userGuid:" + guid);
		return result;
	}

}
