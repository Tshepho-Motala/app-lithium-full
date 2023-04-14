package lithium.service.cashier.client.objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCard {
    private String reference;
    private String name;
    private String cardType;
    private String scheme;
    private String lastFourDigits;
    private String bin;
    private String expiryDate;
    private String fingerprint;
    private String providerData;
    private Boolean isActive;
    private PaymentMethodStatusType status;
    private Boolean isVerified;
    private Boolean isDefault;
    private String cardName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String bank;
	private String issuingCountry;
}
