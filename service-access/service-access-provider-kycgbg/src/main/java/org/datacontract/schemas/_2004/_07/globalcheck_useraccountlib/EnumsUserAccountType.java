
package org.datacontract.schemas._2004._07.globalcheck_useraccountlib;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Enums.UserAccountType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Enums.UserAccountType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="All"/&gt;
 *     &lt;enumeration value="Active"/&gt;
 *     &lt;enumeration value="Expired"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "Enums.UserAccountType", namespace = "http://schemas.datacontract.org/2004/07/GlobalCheck.UserAccountLib.Common")
@XmlEnum
public enum EnumsUserAccountType {

    @XmlEnumValue("All")
    ALL("All"),
    @XmlEnumValue("Active")
    ACTIVE("Active"),
    @XmlEnumValue("Expired")
    EXPIRED("Expired");
    private final String value;

    EnumsUserAccountType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EnumsUserAccountType fromValue(String v) {
        for (EnumsUserAccountType c: EnumsUserAccountType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
