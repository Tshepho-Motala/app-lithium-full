package lithium.service.cashier.processor.inpay.api.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum InpayState {
	RECEIVED("received", false),
	PROCESSING("processing", false),
	PENDING("pending", false),
	REFUND_PENDING("refund_pending", false),
	ACTION_REQUIRED("action-required", false),
	REJECTED("rejected",  true),
	COMPLETED("completed",false),
	RETURNED("returned", true);

	@Getter
	String status;
	@Getter
	boolean failed;

	public static InpayState getState(String incomingState) {
		for (InpayState st : InpayState.values()) {
			if (st.getStatus().equalsIgnoreCase(incomingState)) return st;
		}
		return null;
	}

	public boolean isReturned() {
		return status.equalsIgnoreCase("returned");
	}
}