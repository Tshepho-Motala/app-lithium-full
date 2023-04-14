package lithium.service.cashier.client.internal;

import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class DoProcessorRequest {
	private int stage;
	@Builder.Default
	private Map<String, String> properties = new HashMap<>();
	@Builder.Default
	private Map<Integer, Map<String, String>> inputData = new HashMap<>();
	@Builder.Default
	private Map<Integer, Map<String, String>> outputData = new HashMap<>();
	private DoProcessorRequestUser user;
	private Long transactionId;
	private TransactionType transactionType;
	private String methodCode;
	private Boolean mobile;
	private Object previousProcessorRequest;
	private String processorUserId;
	private String processorReference;
	private String additionalReference;
	private ProcessorAccount processorAccount;
	private boolean transactionFinalized;
	private boolean transactionExpired;


	public String getProperty(String key) throws Exception {
		if (properties == null) throw new Exception("Processor properties missing");
		if (properties.get(key) == null) throw new Exception("Processor property " + key + " missing");
		return properties.get(key);
	}
	
	public Map<String, String> stageInputData(Integer stage) {
		if (inputData.get(stage) == null) inputData.put(stage, new HashMap<>());
		return inputData.get(stage);
	}

	public Map<String, String> stageOutputData(Integer stage) {
		if (outputData.get(stage) == null) outputData.put(stage, new HashMap<>());
		return outputData.get(stage);
	}
	
	public String stageInputData(Integer stage, String key) throws Exception {
		if (stageInputData(stage).get(key) == null) {
			throw new Exception("Expected " + key + " in stage " + stage);
		}
		return stageInputData(stage).get(key);
	}
	
	public String stageOutputData(Integer stage, String key) throws Exception {
		if (stageOutputData(stage).get(key) == null) {
			throw new Exception("Expected " + key + " in stage " + stage);
		}
		return stageOutputData(stage).get(key);
	}
	
	public BigDecimal inputAmount() throws Exception {
		return new BigDecimal(stageInputData(1, "amount"));
	}

	/**
	 * Dependent on the processor fee strategy gross or net amount.
	 * To be used for communication to processor in transactions.
	 * @return
	 * @throws Exception
	 */
	public BigDecimal processorCommunicationAmount() throws Exception {
		String tmpPcm = stageOutputData(1).get("processorCommunicationAmount");
		if (tmpPcm != null) {
			return new BigDecimal(tmpPcm);
		}
		return new BigDecimal(stageInputData(1, "amount"));
	}
	public Long inputAmountCents() throws Exception {
		return inputAmount().movePointRight(2).longValue();
	}

	@Override
	public String toString() {
		String inputDataString = inputData.entrySet().stream()
				.map(e -> e.getKey() + "={" + e.getValue().entrySet().stream()
						.map((e2) -> e2.getKey() + "=" + SensitiveData.maskSensitive(e2.getKey(), e2.getValue()))
						.collect(Collectors.joining(", ")) + "}")
				.collect(Collectors.joining(", "));
 		return "DoProcessorRequest{" + "stage=" + stage + ", properties=" + properties + ", inputData={" + inputDataString + "}, outputData=" + outputData + ", user=" + user + ", transactionId=" + transactionId + ", transactionType=" + transactionType + ", methodCode='" + methodCode + '\'' + ", mobile=" + mobile + ", previousProcessorRequest=" + previousProcessorRequest + ", processorUserId='" + processorUserId + '\'' + ", processorReference='" + processorReference + '\'' + ", additionalReference='" + additionalReference + '\'' + ", processorAccount=" + processorAccount + ", transactionFinalized=" + transactionFinalized + ", transactionExpired=" + transactionExpired + '}';
	}

}
