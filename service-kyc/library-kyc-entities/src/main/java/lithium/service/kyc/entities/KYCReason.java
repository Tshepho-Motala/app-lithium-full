package lithium.service.kyc.entities;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum KYCReason implements Serializable {

	CHECKS_PASSED(0, true,"Checks passed"),
	METHOD_UNAVAILABLE(1, false,"Method Unavailable"),
	DOB_MISMATCH(2, false,"DOB mismatch"),
	SURNAME_MISMATCH(3, false,"Surname mismatch"),
	DOB_AND_SURNAME_MISMATCH(4, false,"DOB & Surname mismatch"),
	REASON_UNDEFINED(5, false,"Reason undefined");

	@Getter
	@Accessors(fluent = true)
	private Integer id;

	@Getter
	@Accessors(fluent = true)
	private Boolean success;

	@Getter
	@Accessors(fluent = true)
	private String reason;
}
