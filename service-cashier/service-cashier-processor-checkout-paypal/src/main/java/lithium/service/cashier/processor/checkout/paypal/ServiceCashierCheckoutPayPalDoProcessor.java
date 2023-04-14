package lithium.service.cashier.processor.checkout.paypal;

import com.checkout.payments.AlternativePaymentSource;
import com.checkout.payments.CustomerRequest;
import com.checkout.payments.PaymentRequest;
import com.checkout.payments.PaymentResponse;
import com.checkout.payments.GetPaymentResponse;
import com.checkout.payments.RequestSource;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.processor.DoProcessorAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.checkout.paypal.data.DeclineReasonErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.checkout.CheckoutApi;
import com.checkout.CheckoutApiImpl;

import static lithium.service.cashier.processor.checkout.paypal.data.DeclineReasonErrorType.CHECKOUT_PAYPAL_DEPOSIT_STAGE_1_DECLINED;
import static lithium.service.cashier.processor.checkout.paypal.data.DeclineReasonErrorType.CHECKOUT_PAYPAL_DEPOSIT_STAGE_2_DECLINED;
import static lithium.service.cashier.processor.checkout.paypal.data.DeclineReasonErrorType.getError;
import static lithium.service.cashier.processor.checkout.paypal.data.DeclineReasonErrorType.getInitiatingTransactionPaymentStage1DecliningMessage;
import static lithium.service.cashier.processor.checkout.paypal.data.DeclineReasonErrorType.getInitiatingTransactionPaymentStage2DecliningMessage;

@Slf4j
@Service
public class ServiceCashierCheckoutPayPalDoProcessor extends DoProcessorAdapter {

	@Override
	protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
		try {
			String secretKey = request.getProperty("secret_key");
			String publicKey = request.getProperty("public_key");
			boolean useSandbox = Boolean.parseBoolean(request.getProperty("use_sendbox"));

			CheckoutApi checkoutApi = CheckoutApiImpl.create(secretKey, useSandbox, publicKey);

			AlternativePaymentSource alternativePaymentSource =
					new AlternativePaymentSource("paypal");

			alternativePaymentSource.put("invoice_number", request.getTransactionId().toString());
			alternativePaymentSource.put("logo_url", request.getProperty("logo_url"));

			CustomerRequest customer = new CustomerRequest();
			customer.setEmail(request.getUser().getEmail());
			customer.setName(request.getUser().getFullName());

			PaymentRequest<RequestSource> paymentRequest =
					PaymentRequest.fromSource(alternativePaymentSource, request.getUser().getCurrency(), request.inputAmountCents()); //cents

			paymentRequest.setCustomer(customer);

			paymentRequest.setReference(request.getTransactionId().toString());
			String redirectUrl = request.getProperty("redirect_url");
			paymentRequest.setSuccessUrl(redirectUrl + "/" + request.getTransactionId() + "/success");
			paymentRequest.setFailureUrl(redirectUrl + "/" + request.getTransactionId() + "/failed");

			PaymentResponse apiResponse = checkoutApi.paymentsClient().requestAsync(paymentRequest).get();
			response.setRawRequestLog(paymentRequest.toString());
			response.setRawResponseLog(apiResponse.toString());

			if (apiResponse.isPending()) {
				log.debug("Payment is in the pending state, user will be redirected to paypal page.");
				response.setOutputData(1, "paymentStatus", apiResponse.getPending().getStatus());
				response.setProcessorReference(apiResponse.getPending().getId());
				response.setIframeUrl(apiResponse.getPending().getRedirectLink().getHref());
				response.setIframeMethod("GET");
				return DoProcessorResponseStatus.IFRAMEPOST;
			} else {
				response.setOutputData(1, "paymentStatus", apiResponse.getPayment().getStatus());
				response.setProcessorReference(apiResponse.getPayment().getId());
				response.setOutputData(1, "responseSummary", apiResponse.getPayment().getResponseSummary());
				response.setDeclineReason(DeclineReasonErrorType.getError(CHECKOUT_PAYPAL_DEPOSIT_STAGE_1_DECLINED));
				return DoProcessorResponseStatus.DECLINED;
			}
		} catch (Exception e) {
			log.error("Unable to get payment  details for the paymentToken for tran with id " + request.getTransactionId() + ". " + e.getMessage(), e);
			buildRawResponseLog(response,e);
			response.setDeclineReason(getInitiatingTransactionPaymentStage1DecliningMessage(request.getTransactionId(), e.getMessage()));
			return DoProcessorResponseStatus.DECLINED;
		}
	}

	@Override
	protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) {
		try {
			String secretKey = request.getProperty("secret_key");
			String publicKey = request.getProperty("public_key");
			boolean useSandbox = Boolean.parseBoolean(request.getProperty("use_sendbox"));

			CheckoutApi api = CheckoutApiImpl.create(secretKey, useSandbox, publicKey);
			String sessionId = request.stageOutputData(1, "session_id");
			GetPaymentResponse payment = api.paymentsClient().getAsync(sessionId != null && !sessionId.isEmpty() ? sessionId : request.getProcessorReference()).get();

			response.setRawResponseLog(payment.toString());

			if (payment.isApproved()) {
				response.setOutputData(2, "paymentStatus", payment.getStatus());
				return DoProcessorResponseStatus.SUCCESS;
			}
			response.setDeclineReason(getError(CHECKOUT_PAYPAL_DEPOSIT_STAGE_2_DECLINED));
			return DoProcessorResponseStatus.DECLINED;
		} catch (Exception e) {
			log.error("Unable to get payment  details for the paymentToken for tran with id " + request.getTransactionId() + ". " + e.getMessage(), e);
			buildRawResponseLog(response,e);
			response.setDeclineReason(getInitiatingTransactionPaymentStage2DecliningMessage(request.getTransactionId(), e.getMessage()));
			return DoProcessorResponseStatus.DECLINED;
		}
	}
}
