package lithium.service.access.provider.sphonic.schema.iban;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lithium.service.access.provider.sphonic.util.SphonicDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VendorData {
	@JsonProperty("IBAN")
	@JsonDeserialize(using = SphonicDeserializer.class) // We sometimes receive an empty object, i.e {}
	private String iban;
	@JsonDeserialize(using = SphonicDeserializer.class) // We sometimes receive an empty object, i.e {}
	private String assumedName;
	@JsonProperty("DebtorReference")
	@JsonDeserialize(using = SphonicDeserializer.class) // We sometimes receive an empty object, i.e {}
	private String debtorReference;
	@JsonProperty("IBANResult")
	@JsonDeserialize(using = SphonicDeserializer.class) // We sometimes receive an empty object, i.e {}
	private String ibanResult;
	@JsonProperty("NameResult")
	@JsonDeserialize(using = SphonicDeserializer.class) // We sometimes receive an empty object, i.e {}
	private String nameResult;
	@JsonProperty("SuggestedName")
	@JsonDeserialize(using = SphonicDeserializer.class) // We sometimes receive an empty object, i.e {}
	private String suggestedName;
	@JsonProperty("AccountStatus")
	@JsonDeserialize(using = SphonicDeserializer.class) // We sometimes receive an empty object, i.e {}
	private String accountStatus;
	@JsonProperty("AccountType")
	@JsonDeserialize(using = SphonicDeserializer.class) // We sometimes receive an empty object, i.e {}
	private String accountType;
	@JsonProperty("IsJointAccount")
	@JsonDeserialize(using = SphonicDeserializer.class) // We sometimes receive an empty object, i.e {}
	private String isJointAccount;
	@JsonProperty("NumberOfAccountHolders")
	@JsonDeserialize(using = SphonicDeserializer.class) // We sometimes receive an empty object, i.e {}
	private String numberOfAccountHolders;
	@JsonProperty("CountryName")
	@JsonDeserialize(using = SphonicDeserializer.class) // We sometimes receive an empty object, i.e {}
	private String countryName;
    public lithium.service.kyc.provider.objects.VendorData convertVendorData() {
        return lithium.service.kyc.provider.objects.VendorData.builder()
                .name("IBAN verification")
                .data(toMap())
                .build();
    }
    public Map<String, String> toMap() {
        Map<String, String> data = new LinkedHashMap<>();
        Optional.ofNullable(this.getIban()).ifPresent(an -> data.put("iban", an));
        Optional.ofNullable(this.getAssumedName()).ifPresent(an -> data.put("assumedName", an));
        Optional.ofNullable(this.getDebtorReference()).ifPresent(dr -> data.put("debtorReference", dr));
        Optional.ofNullable(this.getIbanResult()).ifPresent(ir -> data.put("ibanResult", ir));
        Optional.ofNullable(this.getNameResult()).ifPresent(nr -> data.put("nameResult", nr));
        Optional.ofNullable(this.getSuggestedName()).ifPresent(sn -> data.put("suggestedName", sn));
        Optional.ofNullable(this.getAccountStatus()).ifPresent(as -> data.put("accountStatus", as));
        Optional.ofNullable(this.getAccountType()).ifPresent(at -> data.put("accountType", at));
        Optional.ofNullable(this.getIsJointAccount()).ifPresent(ja -> data.put("isJointAccount", ja));
        Optional.ofNullable(this.getNumberOfAccountHolders()).ifPresent(nah -> data.put("numberOfAccountHolders", nah));
        Optional.ofNullable(this.getCountryName()).ifPresent(cn -> data.put("countryName", cn));
        return data;
    }
}
