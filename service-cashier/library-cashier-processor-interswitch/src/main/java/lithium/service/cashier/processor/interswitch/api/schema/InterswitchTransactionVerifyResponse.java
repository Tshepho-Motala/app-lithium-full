package lithium.service.cashier.processor.interswitch.api.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class InterswitchTransactionVerifyResponse {
    private Recharge recharge;
    private String amount;
    private String currencyCode;
    private String customer;
    private String customerEmail;
    private String customerMobile;
    private String paymentDate;
    private String requestReference;
    private String serviceCode;
    private String serviceName;
    private String serviceProviderId;
    private String status;
    private String surcharge;
    private String transactionRef;
    private String transactionResponseCode;
    private String transactionSet;
    private String responseCode;
    private InterswitchError error;
    private List<InterswitchError> errors;
}

