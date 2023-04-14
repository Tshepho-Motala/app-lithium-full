package lithium.exceptions;

import org.joda.time.DateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper=true)
public class Status415NegativeBalanceException extends NotRetryableErrorCodeException {

	private static int CODE = 415;

	private static final long serialVersionUID = 1L;
	Long amountCents;
	DateTime date;
	String transactionTypeCode;
	String contraAccountCode;
	String contraAccountTypeCode;
	String[] labels;
	String currencyCode;
	String domainName;
	String ownerGuid;
	String authorGuid;
	Boolean allowNegativeAdjust;

	/* Having a message only constructor is critical for Feign to be able to re-instantiate this class
	   if it is being thrown accross service boundaries. It will no longer contain all the data, but the
	   data is used internally only at this point.
	   TODO Considering having two different exceptions for this purpose.
	 */
	public Status415NegativeBalanceException(String message) {
		super(CODE, message, Status415NegativeBalanceException.class.getCanonicalName());
	}

	public Status415NegativeBalanceException(Long amountCents, DateTime date, String transactionTypeCode,
											 String contraAccountCode, String contraAccountTypeCode, String[] labels, String currencyCode,
											 String domainName, String ownerGuid, String authorGuid, Boolean allowNegativeAdjust, String message) {
		super(CODE, message, Status415NegativeBalanceException.class.getCanonicalName());
		this.amountCents = amountCents;
		this.date = date;
		this.transactionTypeCode = transactionTypeCode;
		this.contraAccountCode = contraAccountCode;
		this.contraAccountTypeCode = contraAccountTypeCode;
		this.labels = labels;
		this.currencyCode = currencyCode;
		this.domainName = domainName;
		this.ownerGuid = ownerGuid;
		this.authorGuid = authorGuid;
		this.allowNegativeAdjust = allowNegativeAdjust;
	}

}
