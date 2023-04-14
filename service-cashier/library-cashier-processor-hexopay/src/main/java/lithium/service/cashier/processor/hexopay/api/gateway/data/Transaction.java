package lithium.service.cashier.processor.hexopay.api.gateway.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {
    private String uid;
    private String status;
    private Long amount;
    private String currency;
    private String description;
    private String type;
    @JsonProperty("payment_method_type")
    private String paymentMethodType;
    @JsonProperty("tracking_id")
    private String trackingId;
    String message;
    private boolean test;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("paid_at")
    private String paidAt;
    @JsonProperty("expired_at")
    private String expiredAt;
    @JsonProperty("closed_at")
    private String closedAt;
    @JsonProperty("settled_at")
    private String settledAt;
    private String language;
    @JsonProperty("redirect_url")
    private String redirectUrl;
    @JsonProperty("credit_card")
    private CreditCard creditCard;
    @JsonProperty("recipient_credit_card")
    private CreditCard recipientCreditCard;
    private String id;
    //Object additional_data;
    @JsonProperty("be_protected_verification")
    private BeProtectedResult beProtected;
    @JsonProperty("three_d_secure_verification")
    private ThreeDSecureVerification threeDSecure;
    //correct one should be used according to the response type
    private Payment payment;
    private Payment payout;
    private Payment credit;
    @JsonProperty("avs_cvc_verification")
    private AvsCvcVerificationResponse avsCvcVerification;
    @JsonProperty("billing_address")
    private BillingAddress billingAddress;
    @JsonProperty("recipient_billing_address")
    private BillingAddress recipientBillingAddress;
    private Customer customer;
}
