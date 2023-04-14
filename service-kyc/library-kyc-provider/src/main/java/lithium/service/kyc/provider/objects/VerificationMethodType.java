package lithium.service.kyc.provider.objects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum VerificationMethodType {
    METHOD_PASSPORT("METHOD_PASSPORT"),
    METHOD_NATIONAL_ID("METHOD_NATIONAL_ID"),
    METHOD_DRIVERS_LICENSE("METHOD_DRIVERS_LICENSE"),
    METHOD_BANK_ACCOUNT("METHOD_BANK_ACCOUNT"),
    METHOD_BVN("METHOD_BVN"),
    METHOD_VOTER_ID("METHOD_VOTER_ID"),
    METHOD_NIN("METHOD_NIN"),
	METHOD_NIN_PHONE_NUMBER("METHOD_NIN_PHONE_NUMBER"),
    METHOD_IDIN_VERIFICATION("IDIN_VERIFICATION");
    @Getter
    private final String value;

	public static VerificationMethodType findByValue(String val){
		for(VerificationMethodType vmt : values()){
			if( vmt.getValue().equalsIgnoreCase(val)){
				return vmt;
			}
		}
		return null;
	}
}
