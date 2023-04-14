package lithium.service.cashier.processor.mvend.api.schema.withdraw;

import lombok.Data;

@Data
public class WithdrawConfirmProcessor {

    /**
     * Name of payment provider that processed the payment
     * example: paystack
     */
    String name;

    /**
     * Used to instruct the gateway on which provider to use.
     * example: other_bank
     */
    String type;

    /**
     * Account on payment provider to which funds would be sent
     * example: 00000000
     */
    String account;

    /**
     * If the account is a bank account, then this is set to the bank code that identifies the bank in which the
     * bank account was openned otherwise the field will be NULL or empty
     * example: 057
     */
    String bank_code;
}
