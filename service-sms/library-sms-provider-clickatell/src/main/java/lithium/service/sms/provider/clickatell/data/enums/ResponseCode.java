package lithium.service.sms.provider.clickatell.data.enums;

import java.io.Serializable;

import lombok.Getter;
import lombok.ToString;

@ToString
public enum ResponseCode implements Serializable {
	I200("200", "OK"),
	I202("202", "Accepted"),
	I207("207", "Multi-status"),
	I400("400", "Bad request"),
	I401("401", "Unauthorized"),
	I402("402", "Payment required"),
	I404("404", "Not found"),
	I405("405", "Method not allowed"),
	I410("410", "Gone"),
	I429("429", "Too many requests"),
	I503("503", "Service unavailable");
	
	ResponseCode(String code, String description) {
		this.code = code;
		this.description = description;
	}
	
	@Getter
	private String code;
	@Getter
	private String description;
	
	public static ResponseCode find(final String code) {
		for (ResponseCode rc: ResponseCode.values()) {
			if (rc.getCode().equals(code)) return rc;
		}
		return null;
	}
}