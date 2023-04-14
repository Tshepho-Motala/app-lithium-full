package lithium.service.cashier.controllers.migration;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.cashier.jobs.migration.PopulateTransactionsPaymentMethodsJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/populate-transactions-payment-methods-job")
public class PopulateTransactionsPaymentMethodsJobController {

	@Autowired
	private PopulateTransactionsPaymentMethodsJob job;

	@RequestMapping(method = RequestMethod.GET, path = "/start")
	public Response<Void> startJob(
			@RequestParam(defaultValue = "true") boolean dryRun,
			@RequestParam(defaultValue = "true") boolean onePagePopulationFlag,
			@RequestParam(defaultValue = "10") int pageSize,
			@RequestParam(defaultValue = "1000") Long delay
	) throws Exception {
		log.info("dryRun={}, onePagePopulationFlag={}, pageSize={}, delay={}", dryRun, onePagePopulationFlag, pageSize, delay);
		job.executePopulationTransactionJob(dryRun, onePagePopulationFlag, pageSize, delay);
		return Response.<Void>builder().status(Status.OK).message("Transactions Population Job has been started...").build();
	}

	@RequestMapping(method = RequestMethod.GET)
	public Response<Void> populateTransactionsPaymentMethodsJob(
			@RequestParam(defaultValue = "true") boolean dryRun,
			@RequestParam(defaultValue = "true") boolean onePagePopulationFlag,
			@RequestParam(defaultValue = "10") int pageSize,
			@RequestParam(defaultValue = "1000") Long delay
	) throws InterruptedException {
		job.populateTransactionsPaymentMethods(dryRun, onePagePopulationFlag, pageSize, delay);
		log.info(":: executePopulationTransactionJob finished.");
		return Response.<Void>builder().status(Status.OK).message("OK").build();
	}

	@RequestMapping(method = RequestMethod.GET, path = "/get-unpopulated-transactions-number")
	public Response<Void> getUnpopulatedTransactionsNumber() {
		return Response.<Void>builder().status(Status.OK).message(job.getTransactionsNumberWithoutPaymentMethods()).build();
	}

	@RequestMapping(method = RequestMethod.GET, path = "/force-disable-job")
	public Response<Void> forceDisableJob() {
		job.forceDisableJob();
		return Response.<Void>builder().status(Status.OK).message("Job disabled...").build();
	}
}
