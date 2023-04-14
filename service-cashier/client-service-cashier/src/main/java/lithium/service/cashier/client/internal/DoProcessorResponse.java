package lithium.service.cashier.client.internal;

import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorNotificationData;
import lithium.service.cashier.client.objects.TransactionRemarkData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import static java.util.Optional.ofNullable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoProcessorResponse {
	private DoProcessorResponseStatus status;
	private Map<Integer, Map<String, String>> outputData;
	private String message;
	private Integer errorCode;
	private String rawRequestLog;
	private String rawResponseLog;
	private String processorReference;
	private String additionalReference;
	private String userGuid;
	private Integer amountCentsReceived;
	private BigDecimal amount;
	private Long transactionId;
	private String iframeUrl;
	private String iframeMethod = "POST";
	private String iframeWindowTarget = "cashier_iframe_content"; // Id of current window object in frontend (this is nasty, I know)
	private Map<String, String> iframePostData = new HashMap<>();
	private DateTime expiryDate;
	private Boolean removeTtl = false;
	private String redirectUrl;
	private String processorUserId = null;
	private String billingDescriptor;
	private String paymentType;
	private String declineReason;
	private Long paymentMethodId;
	private ProcessorAccount processorAccount;
	private Boolean updateProcessorAccount;
	private ProcessorNotificationData notificationData;
	private Map<String, String> additionalResponseData = new HashMap<>();
	private TransactionRemarkData remark;

	@Override
	public String toString() {
		return joinFieldsWithoutRawLog()
				.add("rawRequestLog='" + rawRequestLog + "'")
				.add("rawResponseLog='" + rawResponseLog + "'")
				.toString();
	}

	public String toStringTrim() {
		return joinFieldsWithoutRawLog()
				.add("rawRequestLog='" + ofNullable(rawRequestLog)
						.map(s -> s.length() > 5000 ? s.substring(0, 5000) : s)
						.orElse(null) + "'")
				.add("rawResponseLog='" + ofNullable(rawResponseLog)
						.map(s -> s.length() > 20000 ? s.substring(0, 20000) : s)
						.orElse(null) + "'")
				.toString();
	}

	private StringJoiner joinFieldsWithoutRawLog() {
		return new StringJoiner(", ", DoProcessorResponse.class.getSimpleName() + "(", ")")
				.add("status=" + status)
				.add("outputData=" + outputData)
				.add("message='" + message + "'")
				.add("errorCode=" + errorCode)
				.add("processorReference='" + processorReference + "'")
				.add("additionalReference='" + additionalReference + "'")
				.add("userGuid='" + userGuid + "'")
				.add("amountCentsReceived=" + amountCentsReceived)
				.add("amount=" + amount)
				.add("transactionId=" + transactionId)
				.add("iframeUrl='" + iframeUrl + "'")
				.add("iframeMethod='" + iframeMethod + "'")
				.add("iframeWindowTarget='" + iframeWindowTarget + "'")
				.add("iframePostData=" + iframePostData)
				.add("expiryDate=" + expiryDate)
				.add("removeTtl=" + removeTtl)
				.add("redirectUrl='" + redirectUrl + "'")
				.add("processorUserId='" + processorUserId + "'")
				.add("billingDescriptor='" + billingDescriptor + "'")
				.add("paymentType='" + paymentType + "'")
				.add("declineReason='" + declineReason + "'")
				.add("paymentMethodId=" + paymentMethodId)
				.add("processorAccount=" + processorAccount)
				.add("updateProcessorAccount=" + updateProcessorAccount)
				.add("notificationData=" + notificationData)
				.add("additionalResponseData=" + additionalResponseData)
				.add("remark=" + remark);
	}

	public String getAdditionalResponseData(String key) {
		if (additionalResponseData == null) additionalResponseData = new HashMap<>();
		return additionalResponseData.get(key);
	}

	public void setAdditionalResponseData(String key, String value) {
		if (additionalResponseData == null) additionalResponseData = new HashMap<>();
		additionalResponseData.put(key, value);
	}

	public Map<String, String> stageOutputData(Integer stage) {
		if (outputData == null) outputData = new HashMap<>();
		if (outputData.get(stage) == null) outputData.put(stage, new HashMap<>());
		return outputData.get(stage);
	}
	
	public String stageOutputData(Integer stage, String key) {
		return stageOutputData(stage).get(key);
	}

	public void setOutputData(Integer stage, String key, String value) {
		stageOutputData(stage).put(key, value);
	}
	
	public void addRawResponseLog(String log) {
		if (log == null) log = "";
		if (rawResponseLog == null) rawResponseLog = "";
		if (rawResponseLog.length() > 0) rawResponseLog += "\n";
		rawResponseLog = rawResponseLog + log.trim();
	}

	public void addRawRequestLog(String log) {
		if (log == null) log = "";
		if (rawRequestLog == null) rawRequestLog = "";
		if (rawRequestLog.length() > 0) rawRequestLog += "\n";
		rawRequestLog = rawRequestLog + log.trim();
	}
	
	public void addObjectFieldsAsIframePostData(Object o) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (PropertyDescriptor p: BeanUtils.getPropertyDescriptors(o.getClass())) {
			String value = "";
			Object valueO = p.getReadMethod().invoke(o);
			if (valueO != null) value = valueO.toString();
			iframePostData.put(p.getName(), value);
		}
	}
}
