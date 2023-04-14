package lithium.service.cashier.processor.skrill.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="transaction")
public class Transaction {
	/**
	 * Amount paid in the currency of your Skrill account.
	 */
	@XmlElement(name="amount")
	private String amount;
	/**
	 * Currency of your Skrill account.
	 */
	@XmlElement(name="currency")
	private String currency;
	/**
	 * Transaction ID.
	 */
	@XmlElement(name="id")
	private String transactionId;
	/**
	 * Numeric value of the transaction status:
	 * 1 – scheduled (if beneficiary is not yet registered at Skrill)
	 * 2 - processed (if beneficiary is registered)
	 */
	@XmlElement(name="status")
	private Integer status;
	/**
	 * Text value of the transaction status.
	 */
	@XmlElement(name="status_msg")
	private String statusMsg;
}
