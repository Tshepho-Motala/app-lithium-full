
package com.id3global.id3gws._2013._04;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalCaseDispatchRecordStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GlobalCaseDispatchRecordStatus"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Sent"/&gt;
 *     &lt;enumeration value="ResendConfirmationRequired"/&gt;
 *     &lt;enumeration value="ProcessResend"/&gt;
 *     &lt;enumeration value="Resent"/&gt;
 *     &lt;enumeration value="Complete"/&gt;
 *     &lt;enumeration value="Rejected"/&gt;
 *     &lt;enumeration value="Failed"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "GlobalCaseDispatchRecordStatus")
@XmlEnum
public enum GlobalCaseDispatchRecordStatusType {

    @XmlEnumValue("Sent")
    SENT("Sent"),
    @XmlEnumValue("ResendConfirmationRequired")
    RESEND_CONFIRMATION_REQUIRED("ResendConfirmationRequired"),
    @XmlEnumValue("ProcessResend")
    PROCESS_RESEND("ProcessResend"),
    @XmlEnumValue("Resent")
    RESENT("Resent"),
    @XmlEnumValue("Complete")
    COMPLETE("Complete"),
    @XmlEnumValue("Rejected")
    REJECTED("Rejected"),
    @XmlEnumValue("Failed")
    FAILED("Failed");
    private final String value;

    GlobalCaseDispatchRecordStatusType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GlobalCaseDispatchRecordStatusType fromValue(String v) {
        for (GlobalCaseDispatchRecordStatusType c: GlobalCaseDispatchRecordStatusType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
