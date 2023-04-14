package lithium.service.cashier.processor.interswitch.api.schema;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Payment {

    @XmlElement(name = "IsRepeated")
    private Boolean isRepeated; //	Optional		Boolean	To know if the notification has been previously sent
    @XmlElement(name = "ProductGroupCode")
    private String productGroupCode; //	Mandatory	ANY	Alphanumeric	Used internally by Interswitch systems
    @XmlElement(name = "PaymentLogId")
    private Long paymentLogId; //	Mandatory	ANY	Number	Unique integer ID for the payment
    @XmlElement(name = "CustReference", required = true)
    private String custReference; // Mandatory	ANY	Alphanumeric	Unique ID for the paying customer or specific order. Typically provided to the paying customer by the merchant
    @XmlElement(name = "AlternateCustReference")
    private String alternateCustReference; //	Optional	ANY	Alphanumeric	This is an alternate ID used to identify the same Customer
    @XmlElement(name = "Amount", required = true)
    private BigDecimal amount; //	Mandatory	ANY	Numeric	Amount paid by the customer in Major Denomination. This must be verified before giving value to the Customer. For reversal payment notification, the amount has is negative
    @XmlElement(name = "PaymentStatus")
    private Integer paymentStatus; //	Mandatory	1	Numeric	Status of Payment. Note that third party systems would only get notification for successful payments, so this defaults to 0
    @XmlElement(name = "PaymentMethod")
    private String paymentMethod; //	Mandatory	ANY	String	Method by which customer made payment. See list of acceptable values in appendix
    @XmlElement(name = "PaymentReference")
    private String paymentReference; //	Mandatory	ANY	Alphanumeric	Unique reference for the payment as issued to the customer at the point of payment. This value is unique for all transactions.
    @XmlElement(name = "TerminalId")
    private String terminalId; //	Optional	ANY	Alphanumeric	Id of terminal in use
    @XmlElement(name = "ChannelName")
    private String channelName; //	Mandatory	10	String	Name of channel used for transaction. See acceptable values in appendix
    @XmlElement(name = "Location")
    private String location; //	Optional	ANY	String	Location payment was made
    @XmlElement(name = "IsReversal")
    private Boolean isReversal; //	Optional		Boolean	This specifies if the notification is a reversal payment notification
    @XmlElement(name = "PaymentDate")
    private String paymentDate; //	Mandatory		DateTime	Date payment was made in format MM/DD/YYYY hh:mm:ss
    @XmlElement(name = "SettlementDate")
    private String settlementDate; //	Mandatory		DateTime	Date payment would be settled into Merchant account in format MM/DD/YYYY hh:mm:ss
    @XmlElement(name = "InstitutionId")
    private String institutionId; //	Mandatory	5	Alphanumeric	Unique ID given to the merchant
    @XmlElement(name = "InstitutionName")
    private String institutionName; //	Mandatory    ANY	String	Merchant’s configured name within the Bill payment system
    @XmlElement(name = "BranchName")
    private String branchName; //	Optional	ANY	String	Bank Branch where the payment was made, if applicable
    @XmlElement(name = "BankName")
    private String bankName; //   Optional			Bank where the payment was made in case of a cash payment or bank whose card was used to pay in the case of a card based payment
    @XmlElement(name = "FeeName")
    private String feeName; //	Optional	20	Alphanumeric	Name of Transactional Fee applied to the payment(purely informational)
    @XmlElement(name = "CustomerName")
    private String customerName; //	Mandatory	ANY	String	Name of paying customer
    @XmlElement(name = "OtherCustomerInfo")
    private String otherCustomerInfo; //	Optional	ANY	Alphanumeric	Further Details on paying customer
    @XmlElement(name = "ReceiptNo")
    private Long receiptNo; //	Mandatory	ANY	Numeric	Receipt Number issued to customer
    @XmlElement(name = "CollectionsAccount")
    private String collectionsAccount; //	Optional	ANY	String	The account of the Collecting Bank
    @XmlElement(name = "ThirdPartyCode")
    private String thirdPartyCode; //	Optional	10	String	Code for Custom Data

    @XmlElementWrapper(name = "PaymentItems")
    @XmlElement(name = "PaymentItem")
    private List<PaymentNotificationItem> paymentItemList;

    @XmlElement(name = "BankCode")	//Optional	3	String	A code representing the Bank where the payment was made or bank whose card was used to pay in the case of a card based payment. See Bank Code in Appendix.
    private String bankCode;
    @XmlElement(name = "CustomerAddress")	//Optional	ANY	String	This is the customer’s address details
    private String customerAddress;
    @XmlElement(name = "CustomerPhoneNumber")	//Optional	ANY	String	The Phone Number of the paying customer
    private String customerPhoneNumber;
    @XmlElement(name = "DepositorName")	//Optional	ANY	String	Name of person who made the payment
    private String depositorName;
    @XmlElement(name = "DepositorSlipNumber")	// Optional	ANY	Alphanumeric	The number on the Deposit Slip used for payments
    private String depositorSlipNumber;
    @XmlElement(name = "PaymentCurrency") //	Mandatory	3	Numeric	The Code that identifies the currency in which the payment was made. 566 for naira.
    private Long paymentCurrency;
    @XmlElement(name = "OriginalPaymentLogId")	// Optional	ANY	Number	Used in Payment Reversal Notification to indicate the Unique Integer ID for the transaction which needs to be reversed
    private Long originalPaymentLogId;
    @XmlElement(name = "OriginalPaymentReference")	// Optional	ANY	Alphanumeric	Used in Payment Reversal Notification to indicate the Payment reference for the transaction which needs to be reversed
    private String originalPaymentReference;
    @XmlElement(name = "Teller")	// Optional	ANY	Alphanumeric	The teller name
    private String teller;

    public boolean getIsReversal () {
        return Optional.ofNullable(isReversal).orElse(false);
    }
}
