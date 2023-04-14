package lithium.service.cashier.processor.cc.trustspay.data.enums;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum QueryResult implements Serializable {

	CONFIRMING(-2, "Confirming"),
	PROCESSING(-1, "Processing"),
	FAILED(0, "Failed"),
	SUCCESS(1, "Success"),
	NOTFOUND(2, "Order does not exist"),
	INCOMPLETEREQUEST(3, "Incoming parameters incomplete"),
	TOOMANYORDERS(4, "Max of 100 order numbers allowed in request"),
	MERCHANTERROR(5, "Merchant or gateway access error"),
	SIGNINFOERROR(6, "SignInfo invalid"),
	IPACCESSERROR(7, "IP access error"),
	UNKNOWN(999, "Unknown system error");
	
	public static QueryResult fromCode(int code) {
		for (QueryResult r: QueryResult.values()) {
			if (r.getCode() == code) return r;
		}
		return null;
	}

	@Getter
	private Integer code;
	@Getter
	private String description;

}