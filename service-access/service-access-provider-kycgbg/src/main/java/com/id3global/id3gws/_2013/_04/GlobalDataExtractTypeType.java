
package com.id3global.id3gws._2013._04;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalDataExtractType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GlobalDataExtractType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="UserDefined"/&gt;
 *     &lt;enumeration value="Day"/&gt;
 *     &lt;enumeration value="Week"/&gt;
 *     &lt;enumeration value="Fortnight"/&gt;
 *     &lt;enumeration value="Month"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "GlobalDataExtractType")
@XmlEnum
public enum GlobalDataExtractTypeType {

    @XmlEnumValue("UserDefined")
    USER_DEFINED("UserDefined"),
    @XmlEnumValue("Day")
    DAY("Day"),
    @XmlEnumValue("Week")
    WEEK("Week"),
    @XmlEnumValue("Fortnight")
    FORTNIGHT("Fortnight"),
    @XmlEnumValue("Month")
    MONTH("Month");
    private final String value;

    GlobalDataExtractTypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GlobalDataExtractTypeType fromValue(String v) {
        for (GlobalDataExtractTypeType c: GlobalDataExtractTypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
