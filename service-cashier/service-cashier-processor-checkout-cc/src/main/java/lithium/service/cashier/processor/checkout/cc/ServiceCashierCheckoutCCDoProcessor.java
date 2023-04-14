package lithium.service.cashier.processor.checkout.cc;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.DoProcessorAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class ServiceCashierCheckoutCCDoProcessor extends DoProcessorAdapter {
	public static final String CVV_FIELD = "cvv";

	@Autowired
	CashierInternalClientService cashierService;

	@Autowired
	LithiumConfigurationProperties lithiumProperties;

	@Autowired
	CheckoutApiService checkoutApiService;

	@Autowired
	MessageSource messageSource;

	protected DoProcessorResponseStatus validateDepositStage1(DoProcessorRequest request, DoProcessorResponse response) {
		if (request.getProcessorAccount() != null) {
			if (!CheckoutCCFieldValidator.validateCVV(CVV_FIELD, 1, request, response)) {
				log.error("Invalid input data cvv. TransactionId: " + request.getTransactionId());
				return DoProcessorResponseStatus.INPUTERROR;
			}
		} else if (!StringUtil.isEmpty(request.stageInputData(1).get("paymentToken"))) {
			if (StringUtil.isEmpty(request.stageInputData(1).get("nameoncard"))) {
				log.error("Invalid input data nameoncard. TransactionId: " + request.getTransactionId());
				return DoProcessorResponseStatus.INPUTERROR;
			}
		} else {
			log.error("Invalid input data paymentToken/cardReference. TransactionId: " + request.getTransactionId());
			return DoProcessorResponseStatus.INPUTERROR;
		}
		return DoProcessorResponseStatus.SUCCESS;
	}

	@Override
	protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		DoProcessorResponseStatus status =  checkoutApiService.deposit(request, response, request.stageInputData(1).get("paymentToken"), Boolean.parseBoolean(request.getProperty("use_3DSecure")));
		if (status == DoProcessorResponseStatus.DECLINED && request.stageInputData(1).get("paymentToken2") != null && response.stageOutputData(1).containsKey("soft_decline")) {
			status = checkoutApiService.deposit(request, response, request.stageInputData(1).get("paymentToken2"), true);

		}
		return status;
	}

	@Override
	protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		return verifyPayment(request, response);
	}

	@Override
	public DoProcessorResponseStatus withdrawStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
		return checkoutApiService.payout(request, response);
	}

	@Override
	protected DoProcessorResponseStatus withdrawStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
		return verifyPayment(request, response);
	}

	private DoProcessorResponseStatus verifyPayment(DoProcessorRequest request, DoProcessorResponse response) {
		try {
			return checkoutApiService.verifyPayment(request, response, null);
		} catch (Exception e) {
			return DoProcessorResponseStatus.NOOP;
		}
	}
}
