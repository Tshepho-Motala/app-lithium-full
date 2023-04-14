package lithium.service.raf.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum ReferralConversionStatus {
    SUCCESS_REFERRAL("Successful referral"),
    SUCCESS_REFERRAL_AND_CONVERTED("Successful referral and Successfully Converted"),
    PASSED_CONVERSION_CRITERIA("Already past the conversion criteria"),
    ALREADY_REFERRED ("Already referred");
    private String message;
    public String getMessage() { return message; }
}
