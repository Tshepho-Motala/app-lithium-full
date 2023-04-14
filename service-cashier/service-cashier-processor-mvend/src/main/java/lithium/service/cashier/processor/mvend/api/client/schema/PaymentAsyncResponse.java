package lithium.service.cashier.processor.mvend.api.client.schema;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
//@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentAsyncResponse {

    /**
     * Status code to indicate whether transaction is captured for processing
     *
     * Return Code	Description
     * 0	Success
     * 100	Operation Successful
     * 101	Operation Failed, Invalid Account Number/Subscriber ID
     * 102	Operation Failed, Less than required amount paid in transaction
     * 103	Operation Failed, Invalid invoice or product selection
     * 104	Insufficient Account Balance
     * 105	This transaction cannot be completed at this time
     * 106	Invalid UserID/Password Provided
     * 107	Invalid API Key Provided
     * 999	Operation Failed, General Failure
     *
     * Example: 100
     */
    Integer returncode;

    /**
     * Textual description of the returnCode
     * Example: Transactioned processed
     */
    String message;

    /**
     * Status to indicate whether transaction is completed, failed or pending
     *
     * Status	Description
     * Pending	Transaction has been captured for processing
     * Failed	Transaction failed
     *
     * Example: Pending
     */
    String status;

    /**
     * An undocumented field that sometimes comes in. Seems it is there when there is a duplicate transaction error with a null response.
     */
    String transactionref;

    /**
     * LIVESCORE-387 - Network reference number not visible in Lithium backoffice under cashier transaction list
     */
    String paymentref;
}
