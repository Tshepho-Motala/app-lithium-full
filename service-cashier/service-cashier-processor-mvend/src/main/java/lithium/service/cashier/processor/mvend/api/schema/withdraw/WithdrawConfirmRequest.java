package lithium.service.cashier.processor.mvend.api.schema.withdraw;

import lombok.Data;

@Data
public class WithdrawConfirmRequest {

    /** Unique MVEND reference number */
    String transactionref;

    /** Unique transaction reference as provided by the actual payment provider */
    String paymentref;

    /**
     * Status to indicate the final transaction state
     * Possible values:
     *  "Completed" - Transaction was successful
     *  "Failed" - Transaction failed
     */
    String status;

    /** Payment provider that processed the payment. Example "paystack" */
    String processor;

    /** Details that were used to process withdraw with provider.
     *  For failed payments, this parameter may be optional depending on what stage it failed.
     */
    WithdrawConfirmProcessor processor_details;
}
