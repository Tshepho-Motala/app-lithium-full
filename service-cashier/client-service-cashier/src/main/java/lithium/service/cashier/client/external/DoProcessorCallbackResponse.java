package lithium.service.cashier.client.external;

import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorNotificationData;
import lithium.service.cashier.client.objects.TransactionRemarkData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
public class DoProcessorCallbackResponse extends DoProcessorResponse {
	private String callbackResponse;
	private Object processorRequest;
	private String redirect;
	
	@Builder(builderMethodName="builder2")
	private DoProcessorCallbackResponse(
		DoProcessorResponseStatus status,
		Map<Integer, Map<String, String>> outputData,
		String message,
		Integer errorCode,
		String rawRequestLog,
		String rawResponseLog,
		String processorReference,
		String additionalReference,
		String userGuid,
		Integer amountCents,
		BigDecimal amount,
		Long transactionId,
		String iframeUrl,
		String iframeMethod,
		Map<String, String> iframePostData,
		DateTime expiryDate,
		String callbackResponse,
		String billingDescriptor,
		String iframeWindowTarget,
		String paymentType,
		String declineReason,
		Long paymentMethodId,
		ProcessorAccount processorAccount,
		Boolean updateProcessorAccount,
		ProcessorNotificationData notificationData,
		Map<String, String> additionalResponseData,
		TransactionRemarkData remark
	) {
		super(status, outputData, message, errorCode, rawRequestLog, rawResponseLog, processorReference, additionalReference, userGuid,
				amountCents, amount, transactionId, iframeUrl, iframeMethod, iframeWindowTarget, iframePostData, expiryDate,
				false, null, null, billingDescriptor, paymentType, declineReason, paymentMethodId,
				processorAccount, updateProcessorAccount, notificationData, additionalResponseData, remark);
		this.callbackResponse = callbackResponse;
	}
	
	public DoProcessorResponse doProcessorResponse() {
		return DoProcessorResponse.builder()
			.status(super.getStatus())
			.outputData(super.getOutputData())
			.message(super.getMessage())
			.errorCode(super.getErrorCode())
			.rawRequestLog(super.getRawRequestLog())
			.rawResponseLog(super.getRawResponseLog())
			.processorReference(super.getProcessorReference())
			.additionalReference(super.getAdditionalReference())
			.userGuid(super.getUserGuid())
			.amountCentsReceived(super.getAmountCentsReceived())
			.amount(super.getAmount())
			.transactionId(super.getTransactionId())
			.iframeUrl(super.getIframeUrl())
			.iframeMethod(super.getIframeMethod())
			.iframeWindowTarget(super.getIframeWindowTarget())
			.iframePostData(super.getIframePostData())
			.expiryDate(super.getExpiryDate())
			.redirectUrl(super.getRedirectUrl())
			.processorUserId(super.getProcessorUserId())
			.billingDescriptor(super.getBillingDescriptor())
			.paymentType(super.getPaymentType())
			.declineReason(super.getDeclineReason())
			.paymentMethodId(super.getPaymentMethodId())
			.processorAccount(super.getProcessorAccount())
			.updateProcessorAccount(super.getUpdateProcessorAccount())
			.notificationData(super.getNotificationData())
			.additionalResponseData(super.getAdditionalResponseData())
			.remark(super.getRemark())
			.build();
	}
}
