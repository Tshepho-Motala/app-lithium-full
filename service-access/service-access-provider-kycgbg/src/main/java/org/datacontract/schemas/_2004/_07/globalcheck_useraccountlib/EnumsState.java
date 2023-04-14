
package org.datacontract.schemas._2004._07.globalcheck_useraccountlib;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Enums.State.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Enums.State"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Scheduled"/&gt;
 *     &lt;enumeration value="Ready"/&gt;
 *     &lt;enumeration value="Failed"/&gt;
 *     &lt;enumeration value="Expired"/&gt;
 *     &lt;enumeration value="Cancelled"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "Enums.State", namespace = "http://schemas.datacontract.org/2004/07/GlobalCheck.UserAccountLib.Common")
@XmlEnum
public enum EnumsState {

    @XmlEnumValue("Scheduled")
    SCHEDULED("Scheduled"),
    @XmlEnumValue("Ready")
    READY("Ready"),
    @XmlEnumValue("Failed")
    FAILED("Failed"),
    @XmlEnumValue("Expired")
    EXPIRED("Expired"),
    @XmlEnumValue("Cancelled")
    CANCELLED("Cancelled");
    private final String value;

    EnumsState(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EnumsState fromValue(String v) {
        for (EnumsState c: EnumsState.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
