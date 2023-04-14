package lithium.service.kyc.provider.smileindentity.service.util;

import lithium.service.kyc.provider.exceptions.Status407InvalidVerificationIdException;
import lithium.service.kyc.provider.objects.VerificationMethodType;

public class SmileIdentityIdType {
    public final static String BVN = "BVN";
    public final static String NIN = "NIN";
    public final static String NATIONAL_ID = "NATIONAL_ID";
    public final static String PASSPORT = "PASSPORT";
    public final static String DRIVERS_LICENSE = "DRIVERS_LICENSE";
    public final static String VOTER_ID = "VOTER_ID";
    public final static String TIN = "TIN";
    public final static String CAC = "CAC";
    public final static String BANK_ACCOUNT = "BANK_ACCOUNT";

    public static String resolveIdType(VerificationMethodType type) throws Status407InvalidVerificationIdException {
        if (VerificationMethodType.METHOD_PASSPORT.equals(type)) {
            return PASSPORT;
        } else if (VerificationMethodType.METHOD_BANK_ACCOUNT.equals(type)) {
            return BANK_ACCOUNT;
        } else if (VerificationMethodType.METHOD_NATIONAL_ID.equals(type)) {
            return NATIONAL_ID;
        } else if (VerificationMethodType.METHOD_DRIVERS_LICENSE.equals(type)) {
            return DRIVERS_LICENSE;
        } else if (VerificationMethodType.METHOD_BVN.equals(type)) {
            return BVN;
        } else if (VerificationMethodType.METHOD_VOTER_ID.equals(type)) {
            return VOTER_ID;
        } else if (VerificationMethodType.METHOD_NIN.equals(type)) {
            return NIN;
        } else if (VerificationMethodType.METHOD_NIN_PHONE_NUMBER.equals(type)) {
	        return NIN;
        }
        throw new Status407InvalidVerificationIdException("Smile-Dentity: Unsupported verification type: " + type);
    }
}
