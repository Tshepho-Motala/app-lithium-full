package lithium.service.cashier.processor.paystack.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.processor.paystack.api.schema.deposit.PaystackResponse;
import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.nonNull;

@Slf4j
public class PaystackCommonUtils {

	public static String getPaystackMessageFromBody(ObjectMapper mapper, String body) {
		try {
			PaystackResponse response = mapper.readValue(body,  PaystackResponse.class);
			return response.getMessage();
		} catch (Exception ex) {
			return body;
		}
	}

	public static void checkFinalizedAndStatus(DoProcessorRequest request, DoProcessorResponse response) {
		if (request.isTransactionFinalized() && nonNull(response.getStatus())) {
			log.warn("Transaction (" + request.getTransactionId() + ") already finalized and can't be change status to " + response.getStatus().name());
			response.addRawResponseLog("Transaction already finalized and can't be change status to " + response.getStatus().name());
			response.setStatus(null);
		}
	}
}
