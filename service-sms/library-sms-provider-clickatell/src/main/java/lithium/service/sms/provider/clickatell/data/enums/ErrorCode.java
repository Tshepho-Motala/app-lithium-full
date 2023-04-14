package lithium.service.sms.provider.clickatell.data.enums;

import java.io.Serializable;

import lombok.Getter;
import lombok.ToString;

@ToString
public enum ErrorCode implements Serializable {
	I001("001", "Authentication failed"),
	I007("007", "IP lockdown violation"),
	I100("100", "Data malformed"),
	I101("101", "Invalid or missing parameters"),
	I102("102", "Invalid user data header"),
	I105("105", "Invalid destination address"),
	I106("106", "Invalid source address"),
	I108("108", "Invalid or missing API ID"),
	I109("109", "Missing message ID"),
	I113("113", "Maximum message parts exceeded"),
	I114("114", "Cannot route message"),
	I116("116", "Invalid unicode data"),
	I120("120", "clientMessageId contains space(s)"),
	I121("121", "Destination mobile number blocked"),
	I122("122", "Destination mobile opted out"),
	I123("123", "Invalid Sender ID"),
	I128("128", "Number delisted"),
	I130("130", "Maximum MT limit exceeded"),
	I160("160", "HTTP method is not supported on this resource"),
	I161("161", "Resource does not exist"),
	I165("165", "Invalid or no version header specified"),
	I166("166", "Invalid accept header specified"),
	I167("167", "Invalid or no content-type specified"),
	I301("301", "No credit left"),
	I901("901", "Internal error");
	
	ErrorCode(String code, String description) {
		this.code = code;
		this.description = description;
	}
	
	@Getter
	private String code;
	@Getter
	private String description;
}