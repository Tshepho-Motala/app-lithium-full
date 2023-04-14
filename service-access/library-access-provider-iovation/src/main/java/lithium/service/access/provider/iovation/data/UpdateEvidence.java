package lithium.service.access.provider.iovation.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEvidence {
	/**
	 * Required
	 * The unique identifier for the evidence that you will update. When you add evidence,
	 * this ID is returned in the response body in the id parameter.
	 */
	private String id;
	/**
	 * Required
	 * The evidence type. You must submit the evidence type using the precise X-X format. For example,
	 * to submit evidence of credit card fraud, the format must be:
	 * 1-1
	 * This can be any of the evidence types documented here:
	 * https://help.iovation.com/001_FraudForce/02_Stopping_Fraud_and_Abuse/Flagging_Fraudulent_Accounts_and_Devices/09_Evidence_Types_Reference.
	 * You can apply one instance of a given evidence type to an account. If you attempt to post the same
	 * type of evidence more than once, the existing evidence record will be updated with a new comment.
	 */
	private String evidenceType;
	/**
	 * Required
	 * Comment describing the evidence. May be up to 4000 characters.
	 */
	private String comment;
	/**
	 * Required
	 * The account or device to apply the evidence to. The appliedTo entity can contain an account or a device.
	 */
	private AppliedTo appliedTo;
}
