
package com.id3global.id3gws._2013._04;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalDataExtractState.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GlobalDataExtractState"&gt;
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
@XmlType(name = "GlobalDataExtractState")
@XmlEnum
public enum GlobalDataExtractStateType {

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

    GlobalDataExtractStateType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GlobalDataExtractStateType fromValue(String v) {
        for (GlobalDataExtractStateType c: GlobalDataExtractStateType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
