
package com.id3global.id3gws._2013._04;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalProfileState.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GlobalProfileState"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Test"/&gt;
 *     &lt;enumeration value="PreEffective"/&gt;
 *     &lt;enumeration value="Effective"/&gt;
 *     &lt;enumeration value="Retired"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "GlobalProfileState")
@XmlEnum
public enum GlobalProfileStateType {

    @XmlEnumValue("Test")
    TEST("Test"),
    @XmlEnumValue("PreEffective")
    PRE_EFFECTIVE("PreEffective"),
    @XmlEnumValue("Effective")
    EFFECTIVE("Effective"),
    @XmlEnumValue("Retired")
    RETIRED("Retired");
    private final String value;

    GlobalProfileStateType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GlobalProfileStateType fromValue(String v) {
        for (GlobalProfileStateType c: GlobalProfileStateType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
