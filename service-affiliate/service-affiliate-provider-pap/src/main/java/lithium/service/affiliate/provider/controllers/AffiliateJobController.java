package lithium.service.affiliate.provider.controllers;

import java.text.DateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.accounting.objects.Period;
import lithium.service.affiliate.provider.service.DomainService;
import lithium.service.affiliate.provider.service.JobService;
import lithium.service.affiliate.provider.service.PapProcessingService;
import lithium.service.affiliate.provider.stream.objects.PapTransactionStreamData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class AffiliateJobController {
	@Autowired private DomainService domainService;
	@Autowired private JobService jobService;
	@Autowired private PapProcessingService pps;
	
//	@Scheduled(cron="${lithium.service.affiliate.provider.data.export.cron}")
	@Scheduled(fixedDelay=60000)
	public void runDataExport() {
		log.info("executing scheduled method for batch processing");
		domainService.getPapAffiliateDomains().forEach((provider) -> { jobService.runDataExportForDomain(provider); } );
	}
	
	@PostMapping("/affiliate/provider/data/export")
	public void exportDataToAffiliateSystem(@RequestParam("domainName") String domainName, @RequestParam("dateStart") String dateStart, @RequestParam("dateEnd") String dateEnd) throws Exception {
		log.info("Maunal execution for affiliate data export on domain: "+ domainName + " dateStart: " + dateStart + " dateEnd: " + dateEnd);
		Date startDate = DateTime.parse(dateStart).toDate();
//		startDate.setMonth(6);
//		startDate.setDate(26);
		Date endDate =DateTime.parse(dateEnd).toDate();
//		endDate.setMonth(6);
//		endDate.setDate(29);
		jobService.runExport(domainName, Period.GRANULARITY_DAY, domainService.retrieveDomainFromDomainService(domainName).getCurrency(), startDate, endDate);
	}
	
	@RequestMapping("/affiliate/provider/test")
	public void auth(@RequestParam("merchantUsername") String merchantUsername, @RequestParam("merchantPassword") String merchantPassword) throws Exception {
		String sessionId = pps.authenticate(merchantUsername, merchantPassword, "http://isp55.com/scripts/server.php");
		PapTransactionStreamData ptsd = PapTransactionStreamData.builder()
				.affiliateGuid("testaff")
				.amount("500.00")
				.bannerGuid("b79227d3")
				.campaignGuid("11111111")
				.ownerGuid("luckybetz/testuser")
				.transactionDate(new Date())
				.transactionType("CASINO_BET")
				.build();
		pps.registerTransaction(sessionId, ptsd);
	}
}
