package lithium.service.cashier.processor.flutterwave;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.DoProcessorAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.flutterwave.services.DepositService;
import lithium.service.cashier.processor.flutterwave.services.VerifyService;
import lithium.service.cashier.processor.flutterwave.services.WithdrawService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
@Slf4j
public class ServiceCashierFlutterwaveDoProcessor extends DoProcessorAdapter {
	@Autowired
	VerifyService verifyService;

	@Autowired
	WithdrawService withdrawService;

	@Autowired
	DepositService depositService;

	@Override
	protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
		rest.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return false;
			}
		});
		try {
			if (request.getMethodCode().equals("flutterwaveussd")) {
				return depositService.InitiateUssdDeposit(request, response, context, rest);
			} else {
				return depositService.InitiateWebDeposit(request, response, context, rest);
			}
		} catch (Exception ex) {
			String message = "Deposit stage1 failed (" + request.getTransactionId() + ") due " + ex.getMessage();
			log.error(message, ex);
			response.addRawResponseLog(message + "\r\nFull Stacktrace: \r\n" + ExceptionUtils.getFullStackTrace(ex));
			response.setMessage(ex.getMessage());
			response.setDeclineReason(ex.getMessage());
			return DoProcessorResponseStatus.DECLINED;
		}
	}

	@Override
	protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
		try {
			return verifyService.verify(request, response);
		} catch (Exception ex) {
			String message = "Deposit stage2 failed (" + request.getTransactionId() + ") due " + ex.getMessage();
			log.error(message, ex);
			response.addRawResponseLog(message + "\r\nFull Stacktrace: \r\n" + ExceptionUtils.getFullStackTrace(ex));
			response.setMessage(ex.getMessage());
			return DoProcessorResponseStatus.NOOP;
		}
	}

	@Override
	public DoProcessorResponseStatus withdrawStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
		rest.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return false;
			}
		});
		try {
			return withdrawService.withdraw(request, response, context, rest);
		} catch (Exception ex) {
			String message = "Withdraw stage1 failed (" + request.getTransactionId() + ") due " + ex.getMessage();
			log.error(message, ex);
			response.addRawResponseLog(message + "\r\nFull Stacktrace: \r\n" + ExceptionUtils.getFullStackTrace(ex));
			response.setMessage(ex.getMessage());
			response.setDeclineReason(ex.getMessage());
			return DoProcessorResponseStatus.DECLINED;
		}
	}

	@Override
	protected DoProcessorResponseStatus withdrawStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
		try {
			return verifyService.getTransfer(request, response);
		} catch (Exception ex) {
			String message = "Withdraw stage2 failed (" + request.getTransactionId() + ") due " + ex.getMessage();
			log.error(message, ex);
			response.addRawResponseLog(message + "\r\nFull Stacktrace: \r\n" + ExceptionUtils.getFullStackTrace(ex));
			response.setMessage(ex.getMessage());
			return DoProcessorResponseStatus.NOOP;
		}
	}
}
