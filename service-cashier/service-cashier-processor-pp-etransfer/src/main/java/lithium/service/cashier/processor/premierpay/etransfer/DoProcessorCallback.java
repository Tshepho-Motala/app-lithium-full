package lithium.service.cashier.processor.premierpay.etransfer;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.Response;
import lithium.service.cashier.client.external.DoProcessorCallbackRequest;
import lithium.service.cashier.client.external.DoProcessorCallbackResponse;
import lithium.service.cashier.client.frontend.DoErrorException;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.method.premierpay.enums.Action;
import lithium.service.cashier.method.premierpay.enums.ErrorCode;
import lithium.service.cashier.method.premierpay.util.SignatureCalculator;
import lithium.service.cashier.method.premierpay.enums.Status;
import lithium.service.cashier.processor.callback.DoProcessorCallbackAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class DoProcessorCallback extends DoProcessorCallbackAdapter {
	@Autowired
	private LithiumConfigurationProperties configurationProperties;
	
	@Override
	public DoProcessorCallbackResponse callback(DoProcessorCallbackRequest request, DoProcessorCallbackResponse response) throws DoErrorException, Exception {
		log.debug("PremierPay :: ProcessorCallback");
		log.debug("Req: "+request);
		log.debug("Res: "+response);

		if (log.isTraceEnabled()) {
			request.getParameterMap().forEach((k, v) -> {
				log.trace("k: "+k+" v:"+v);
			});
		}

		String callback = request.getParameter("callback");
		log.debug("callback :: "+callback);
		Long receivedTransactionId = (request.getParameter("udf1")!=null)?Long.parseLong(request.getParameter("udf1")):-1L;
		if (receivedTransactionId == -1L) {
            throw new Exception("Invalid TransactionId received.");
		}

		String payby = request.getParameter("udf3");
		String extTransactionId = request.getParameter("txid"); // The transaction id of the current request.
		String parentTranId = request.getParameter("parent_txid"); // The transaction id of the initial transaction of the initial transaction request.

		response.setProcessorUserId(request.getParameter("customer_id"));
		response.setTransactionId(receivedTransactionId);
		response.setProcessorReference(extTransactionId);
		Action tranAction = Action.fromCode(request.getParameter("tx_action")); // The type of action performed (eg. PAYMENT, REFUND, PAYOUT, etc)
		Action parentAction = Action.fromCode(request.getParameter("parent_txaction")); // The type of action that was initially processed (eg. PAYMENT, PAYOUT, etc)

		Response<DoProcessorRequest> doProcessorRequest = null;
		// When it is a withdrawal callback we don't check for oob parent transactions to tie back to since we initiated the withdrawal.
		if (tranAction != Action.PAYOUT && tranAction != Action.PENDING_PAYOUT) {
			doProcessorRequest = getDoProcessorRequest(receivedTransactionId, extTransactionId, request.getProcessorCode(), true);
		} else {
			doProcessorRequest = getDoProcessorRequest(receivedTransactionId, extTransactionId, request.getProcessorCode(), false);
		}

		if (!doProcessorRequest.isSuccessful()) {
			log.error("Could not retrieve previous processor request. TranId: "+receivedTransactionId+" ExtTranId: "+extTransactionId);
			response.setMessage(doProcessorRequest.getMessage());
			response.setStatus(DoProcessorResponseStatus.DECLINED);
			return response;
		}
		Long transactionId = doProcessorRequest.getData().getTransactionId();
		response.setTransactionId(transactionId);

		String userId = request.getParameter("udf2");

		Status status = Status.fromCode(request.getParameter("status")); // The status of the processed action (eg. APPROVED, DECLINED, ERROR, REFUNDED, etc)
		String descriptor = request.getParameter("descriptor"); // What should show on the customer's bank statement.

		String sid = doProcessorRequest.getData().getProperty("sid");
		String rcode = doProcessorRequest.getData().getProperty("rcode");
		String signature = request.getParameter("signature");

		if (!signatureValid(signature, rcode, sid, extTransactionId, status, descriptor)) {
			log.error("Invalid signature received - Decline. TranId: "+receivedTransactionId+" ExtTranId: "+extTransactionId);
			response.setMessage("Invalid signature received.");
			response.setStatus(DoProcessorResponseStatus.DECLINED);
			return response;
		}

		/**
		 * Jamie, 10:27 AM - PremierPay Integration - Senior Developer
		 * i see. so, it's the symfony intl currency bundle. it looks like it is techincally sending back the format as {symbol} {amount} {currency} - it's just that it's "symbol" for canadian dollars is "CA$"
		 */
		int amountCents = 0;
		if ("interac".equalsIgnoreCase(payby)) {
			amountCents = doProcessorRequest.getData().inputAmount().movePointRight(2).intValue();
		} else if ("etransfer".equalsIgnoreCase(payby)) {
			String amount = request.getParameter("amount"); // The transaction amount, formatted with currency, ready for display
			amount = amount.substring(amount.indexOf(" "), amount.lastIndexOf(" ")).trim();
			amountCents = new BigDecimal(amount).movePointRight(2).intValue();
		}

		String message = request.getParameter("message");
		String code = request.getParameter("code");
		ErrorCode ec = ErrorCode.fromCode(code);
		String comment = request.getParameter("comment"); // Notes assigned to the current transaction

		if (tranAction == Action.PENDING || tranAction == Action.PENDING_PAYOUT) {
			response.setStatus(DoProcessorResponseStatus.NOOP);
		} else if (tranAction == Action.PAYMENT || tranAction == Action.PAYOUT) {
			switch (status) {
				case APPROVED:
					response.setMessage(descriptor + " - " + comment);
					response.setStatus(DoProcessorResponseStatus.SUCCESS);
					break;
				case DECLINED:
					response.setMessage(descriptor + " - " + comment);
					response.setStatus(DoProcessorResponseStatus.DECLINED);
					break;
				case REFUNDED:
					response.setMessage(descriptor + " - " + comment);
					response.setStatus(DoProcessorResponseStatus.REVERSAL_NEXTSTAGE);
					break;
				case ERROR:
				default:
					response.setMessage(message + ((ec != null) ? " - " + ec.description() : ""));
					response.setStatus(DoProcessorResponseStatus.FATALERROR);
					break;
			}
		} else {
			if ("return".equalsIgnoreCase(callback)) {
				switch (status) {
					case APPROVED:
						response.setMessage(descriptor);
						response.setStatus(DoProcessorResponseStatus.SUCCESS);
						break;
					case DECLINED:
						response.setMessage(descriptor + " - "+ message);
						response.setStatus(DoProcessorResponseStatus.DECLINED);
						break;
					default:
						response.setMessage(message);
						response.setStatus(DoProcessorResponseStatus.FATALERROR);
						break;
				}
			} else response.setStatus(DoProcessorResponseStatus.FATALERROR);
		}

		if (tranAction!=null) response.setOutputData(1, "tx_action", tranAction.code());
		if (parentAction!=null) response.setOutputData(1, "parent_txaction", parentAction.code());

		response.setAmountCentsReceived(amountCents);
		response.setProcessorRequest(request.getParameterMap());

		response.setOutputData(1, "parent_txid", parentTranId);
		response.setOutputData(1, "account_info", request.getParameter("customer_id"));
		response.setOutputData(1, "descriptor", descriptor);
		response.setBillingDescriptor(descriptor);
		
		response.setCallbackResponse("OK");
		response.setRedirect(configurationProperties.getGatewayPublicUrl()+"/service-cashier/frontend/loadingrefresh");
//		response.setRedirect("http://196.22.242.140:29001/service-cashier/frontend/loadingrefresh");
		return response;
	}
	
	protected boolean signatureValid(String signature, String rcode, String sid, String extTransactionId, Status status, String descriptor) {
		SignatureCalculator sc = SignatureCalculator.builder().build();
		String sig = sc.signature(rcode, sid, extTransactionId, status.code(), descriptor);

		if (!sig.equalsIgnoreCase(signature)) {
			log.error("Signature Received : "+signature+" <> "+sig);
			return false;
		} else {
			log.info("Signature Received : "+signature+" === "+sig);
			return true;
		}
	}
}
