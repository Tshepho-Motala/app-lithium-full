package lithium.service.cashier.method.premierpay.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {
//	Request codes:
	RF1("RF1", "Required fields are missing or invalid"),
	IF1("IF1", "Some parameters are invalid"),
//	Signature codes:
	SIG001("SIG001", "Signature incorrect."),
//	Payment codes:
	E100("100", "Payment already processed."),
	E101("101","Payment is not available for this transaction."),
//	Settlement codes:
	E400("400","Cannot settle previously settled transaction."),
	E401("401","Settlement is not available for this transaction."),
	E402("402","Cannot settle a voided preauthorization."),
//	Refund codes:
	E500("500","Cannot refund more than the available balance"),
	E501("501","There is a refund attempt still queued for this transaction"),
	E502("502","Refund has been queued for processing"),
	E503("503","Partial refunds are not accepted on this solution"),
	E504("504","Refund is not available for this transaction"),
	E505("505","Cannot refund payments/settlements over 180 days old"),
	E506("506","Cannot refund charged back transaction"),
	E507("507","Cannot process a refund for $0.00 or a negative amount"),
	E508("508","Bank only allows 1 refund per transaction"),
	E509("509","Limit of refunds per day exceeded"),
//	Void codes:
	E600("600","Cannot void payments/settlements over 5 days old"),
	E601("601","Cannot void previously refunded transaction"),
	E602("602","Cannot void previously voided transaction"),
	E603("603","Partial voids are not accepted by this solution"),
	E604("604","Cannot void charged back transaction"),
//	Chargeback codes:
	E700("700","Cannot chargeback previously charged back transaction"),
//	Retrieval codes:
	E800("800","Cannot retrieve previously retrieved transaction"),
//	System codes:
	G00("G00","The gateway is currently unavailable while system maintenance and required updates are performed. This transaction has not been recorded of submitted for processing. Please try again shortly"),
	G01("G01","{field_description} (\"{field name}\") is required by the active solution"),
	G02("G02","The active solution requires Secure3D to be enabled. Configuration error. Contact technical support"),
	E3DA("3DA","The 3D Secure authentication failed or was not completed"),
//	Direct Debit response codes:
	NBA("NBA","No bank account available"),
	E900("900","Account information incorrectly entered"),
	E901("901","Insufficient Funds"),
	E902("902","Account not Found"),
	E903("903","Payment Stopped"),
	E904("904","Post/Stale Dated"),
	E905("905","Account Closed"),
	E907("907","No Debit Allowed"),
	E908("908","Funds Not Cleared"),
	E909("909","Currency/Account Mismatch"),
	E910("910","Payor/Payee Deceased"),
	E911("911","Account Frozen"),
	E912("912","Invalid/Incorrect Account No"),
	E914("914","Incorrect Payor/Payee Name"),
	E915("915","No Agreement Existed"),
	E916("916","Not in accordance with Agreement"),
	E917("917","Agreement Revoked - Personal"),
	E918("918","No Confirmation/Pre-Notification"),
	E919("919","Not in accordance with Agreement"),
	E920("920","Agreement Revoked - Business"),
	E921("921","No Pre-Notification - Business"),
	E922("922","Customer Initiated Return"),
	E990("990","Institution in Default"),
	E999("999","Chargeback"),
//	Bank	"standard codes:
	E00("00","successful approval (corresponds to 200 response)."),
	E01("01","refer to issuer."),
	E02("02","refer to issuer's special conditions."),
	E03("03","invalid merchant."),
	E04("04","pickup card."),
	E05("05","do not honour."),
	E06("06","error."),
	E07("07","pickup card, special conditions."),
	E08("08","honour with ID (signature)(corresponds to 200 response)."),
	E09("09","request in progress."),
	E10("10","approved for partial amount."),
	E11("11","approved VIP."),
	E12("12","invalid transaction."),
	E13("13","invalid amount."),
	E14("14","invalid card number."),
	E15("15","no such issuer."),
	E16("16","approved, update track 3."),
	E17("17","customer cancellation."),
	E18("18","customer dispute."),
	E19("19","re-enter transaction."),
	E20("20","invalid response."),
	E21("21","no action taken."),
	E22("22","suspected malfunction."),
	E23("23","unacceptable transaction fee."),
	E24("24","file date not supported."),
	E25("25","unable to locate record on file."),
	E26("26","duplicate file update record, old record replaced."),
	E27("27","file update field error."),
	E28("28","file update file locked out."),
	E29("29","file update not successful, contact acquirer."),
	E30("30","format error."),
	E31("31","bank not supported by switch."),
	E32("32","completed partially."),
	E33("33","expired card."),
	E34("34","suspected fraud."),
	E35("35","contact acquirer."),
	E36("36","restricted card."),
	E37("37","contact acquirer security."),
	E38("38","allowable PIN retries exceeded."),
	E39("39","no credit account."),
	E40("40","request function not supported."),
	E41("41","lost card."),
	E42("42","no universal account."),
	E43("43","stolen card."),
	E44("44","no investment account."),
	E45("45","reserved, will not be returned."),
	E46("46","reserved, will not be returned."),
	E47("47","reserved, will not be returned."),
	E48("48","reserved, will not be returned."),
	E49("49","reserved, will not be returned."),
	E50("50","reserved, will not be returned."),
	E51("51","insufficient funds."),
	E52("52","no cheque account."),
	E53("53","no savings account."),
	E54("54","expired card."),
	E55("55","incorrect PIN."),
	E56("56","no card record."),
	E57("57","transaction not permitted to cardholder."),
	E58("58","transaction not permitted to terminal."),
	E59("59","suspected fraud."),
	E60("60","contact acquirer."),
	E61("61","exceeds withdrawal amount limit."),
	E62("62","restricted card."),
	E63("63","security violation."),
	E64("64","original amount incorrect."),
	E65("65","exceeds withdrawal frequency limit."),
	E66("66","contact acquirer security."),
	E67("67","hard capture."),
	E68("68","response received too late."),
	E69("69","reserved, will not be returned."),
	E70("70","reserved, will not be returned."),
	E71("71","reserved, will not be returned."),
	E72("72","reserved, will not be returned."),
	E73("73","reserved, will not be returned."),
	E74("74","reserved, will not be returned."),
	E75("75","allowable number of PIN retries exceeded."),
	E76("76","reserved, will not be returned."),
	E77("77","reserved, will not be returned."),
	E78("78","reserved, will not be returned."),
	E79("79","reserved, will not be returned."),
	E80("80","reserved, will not be returned."),
	E81("81","reserved, will not be returned."),
	E82("82","reserved, will not be returned."),
	E83("83","reserved, will not be returned."),
	E84("84","reserved, will not be returned."),
	E85("85","reserved, will not be returned."),
	E86("86","reserved, will not be returned."),
	E87("87","reserved, will not be returned."),
	E88("88","reserved, will not be returned."),
	E89("89","reserved, will not be returned."),
	E90("90","cutoff in progress."),
	E91("91","issuer inoperative."),
	E92("92","financial institution cannot be found."),
	E93("93","transaction cannot be completed, violation of law."),
	E94("94","duplicate transmission."),
	E95("95","reconcile error."),
	E96("96","system malfunction."),
	E97("97","reconciliation totals have been reset."),
	E98("98","MAC error."),
	E99("99","reserved, will not be returned.");
	
	@Getter
	@Setter
	@Accessors(fluent = true)
	private String code;
	@Getter
	@Setter
	@Accessors(fluent = true)
	private String description;
	
	@JsonCreator
	public static ErrorCode fromCode(String code) {
		for (ErrorCode ec : ErrorCode.values()) {
			if (ec.code.equalsIgnoreCase(code)) {
				return ec;
			}
		}
		return null;
	}
}