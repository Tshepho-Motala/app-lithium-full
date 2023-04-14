
package com.id3global.id3gws._2013._04;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalCaseDispatchServiceType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GlobalCaseDispatchServiceType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="None"/&gt;
 *     &lt;enumeration value="DVLA"/&gt;
 *     &lt;enumeration value="DisclosureScotland"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "GlobalCaseDispatchServiceType")
@XmlEnum
public enum GlobalCaseDispatchServiceTypeType {

    @XmlEnumValue("None")
    NONE("None"),
    DVLA("DVLA"),
    @XmlEnumValue("DisclosureScotland")
    DISCLOSURE_SCOTLAND("DisclosureScotland");
    private final String value;

    GlobalCaseDispatchServiceTypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GlobalCaseDispatchServiceTypeType fromValue(String v) {
        for (GlobalCaseDispatchServiceTypeType c: GlobalCaseDispatchServiceTypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
