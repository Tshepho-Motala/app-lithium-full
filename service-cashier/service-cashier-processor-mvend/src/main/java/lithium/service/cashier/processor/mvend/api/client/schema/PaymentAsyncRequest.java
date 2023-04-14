package lithium.service.cashier.processor.mvend.api.client.schema;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PaymentAsyncRequest {

    /**
     * Timestamp in format YYYYMMDDhhmmss. Example 20200325143756
     */
    String timestamp;

    /**
     * A computed token string: token = sha256 hash ( appid + apikey + timestamp )
     * Example: 8263d7b41bc61c41dbad1795a05b2bde5e18e05878f998f579cd364a2cd37e95
     */
    String token;

    /**
     * Customer phone number tied to his betting account
     * Example: 2347008009000
     */
    String msisdn;

    /**
     * Amount to credit into customer bank account or opay account
     * Example: 1000.00
     */
    String amount;

    /**
     * Country currency
     * Example: NGN
     */
    String currency;

    /**
     * This is set to 'makepaymentrequest'
     * Example: makepaymentrequest
     */
    String requesttype;

    /**
     * Unique reference for this transaction in Lithium
     * Example: 10020003
     */
    String transactionref;

    /**
     * In the absense of this parameter, transaction shall be processed in synchronous mode. When the parameter is
     * present and set to true, transaction shall be processed in asynchronous mode. The final status of the
     * transaction shall be posted back to the core betting engine through a separate api that they shall expose
     * Example: true
     */
    boolean async;
}

