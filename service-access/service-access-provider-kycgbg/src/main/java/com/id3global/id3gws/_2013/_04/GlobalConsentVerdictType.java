
package com.id3global.id3gws._2013._04;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalConsentVerdict.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GlobalConsentVerdict"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="None"/&gt;
 *     &lt;enumeration value="Approved"/&gt;
 *     &lt;enumeration value="Reject"/&gt;
 *     &lt;enumeration value="RequestInfo"/&gt;
 *     &lt;enumeration value="Cancel"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "GlobalConsentVerdict")
@XmlEnum
public enum GlobalConsentVerdictType {

    @XmlEnumValue("None")
    NONE("None"),
    @XmlEnumValue("Approved")
    APPROVED("Approved"),
    @XmlEnumValue("Reject")
    REJECT("Reject"),
    @XmlEnumValue("RequestInfo")
    REQUEST_INFO("RequestInfo"),
    @XmlEnumValue("Cancel")
    CANCEL("Cancel");
    private final String value;

    GlobalConsentVerdictType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GlobalConsentVerdictType fromValue(String v) {
        for (GlobalConsentVerdictType c: GlobalConsentVerdictType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
