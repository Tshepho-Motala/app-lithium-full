package lithium.service.cashier.processor.trustly.api.data.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum ErrorCode {
    ERROR_FUNCTION_ACCESS_DENIED(602, "The merchant does not have access to this function."),
    ERROR_HOST_ACCESS_DENIED(607,"The IP address of the merchant has not been added to Trustly's IP-whitelist."),
    ERROR_INVALID_AMOUNT	(615,"The Amount specified in the deposit call is invalid. The amount must be > 0 with 2 decimals."),
    ERROR_INVALID_CREDENTIALS	(616, "The username and/or password used in the API call is incorrect."),
    ERROR_UNKNOWN	(620,"There could be several reasons for this error, contact your integration manager for details."),
    ERROR_INVALID_PARAMETERS	(623,"Some value or parameter in the deposit call does not match the expected format."),
    ERROR_UNABLE_TO_VERIFY_RSA_SIGNATURE	(636,"The signature in the deposit call could not be verified using the merchant's public key. Either the wrong private key is used, or the the data object used to create the signature was serialized incorrectly."),
    ERROR_DUPLICATE_MESSAGE_ID	(637,"The MessageID sent in the deposit has been used before."),
    ERROR_ENDUSER_IS_BLOCKED	(638, "The enduser that initiated the payment is blocked."),
    ERROR_INVALID_LOCALE	(645, "The Locale-attribute is sent with an incorrect value."),
    ERROR_DUPLICATE_UUID	(688, "This uuid has been used before."),
    ERROR_ENDUSERID_IS_NULL	(696, "The EndUserID sent in the request is null."),
    ERROR_MESSAGEID_IS_NULL	(697, "The MessageID sent in the request is null"),
    ERROR_INVALID_IP	(698,"The IP attribute sent is invalid. Only one IP address can be sent."),
    ERROR_MALFORMED_SUCCESSURL	(700, "The SuccessURL sent in the request is malformed. It must be a valid http(s) address."),
    ERROR_MALFORMED_FAILURL	(701, "The FailURL sent in the request is malformed. It must be a valid http(s) address."),
    ERROR_MALFORMED_TEMPLATEURL	(702, "The TemplateURL sent in the request is malformed. It must be a valid http(s) address."),
    ERROR_MALFORMED_URLTARGET	(703, "The URLTarget sent in the request is malformed."),
    ERROR_MALFORMED_MESSAGEID	(704, "The MessageID sent in the request is malformed."),
    ERROR_MALFORMED_NOTIFICATIONURL	(705, "The NotificationURL sent in the request is malformed. It must be a valid http(s) address."),
    ERROR_MALFORMED_ENDUSERID	(706, "The EndUserID sent in the request is malformed.");

    private static Map map = new HashMap<>();

    @Getter
    @Accessors(fluent = true)
    private Integer code;

    @Getter
    @Accessors(fluent = true)
    private String message;

    static {
        for (ErrorCode errorCode : ErrorCode.values()) {
            map.put(errorCode.code, errorCode);
        }
    }
    public static ErrorCode formCode(int code) {
        return map.containsKey(code) ? (ErrorCode)map.get(code) : ErrorCode.ERROR_UNKNOWN;
    }

}
