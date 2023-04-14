
package com.id3global.id3gws._2013._04;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalSanctionsDateType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GlobalSanctionsDateType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Unknown"/&gt;
 *     &lt;enumeration value="Birth"/&gt;
 *     &lt;enumeration value="Death"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "GlobalSanctionsDateType")
@XmlEnum
public enum GlobalSanctionsDateTypeType {

    @XmlEnumValue("Unknown")
    UNKNOWN("Unknown"),
    @XmlEnumValue("Birth")
    BIRTH("Birth"),
    @XmlEnumValue("Death")
    DEATH("Death");
    private final String value;

    GlobalSanctionsDateTypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GlobalSanctionsDateTypeType fromValue(String v) {
        for (GlobalSanctionsDateTypeType c: GlobalSanctionsDateTypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
