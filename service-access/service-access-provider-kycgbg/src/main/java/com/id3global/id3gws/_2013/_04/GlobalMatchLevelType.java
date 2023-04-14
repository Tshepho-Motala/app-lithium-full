
package com.id3global.id3gws._2013._04;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalMatchLevel.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GlobalMatchLevel"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Basic"/&gt;
 *     &lt;enumeration value="Level1"/&gt;
 *     &lt;enumeration value="Enhanced"/&gt;
 *     &lt;enumeration value="Advanced"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "GlobalMatchLevel")
@XmlEnum
public enum GlobalMatchLevelType {

    @XmlEnumValue("Basic")
    BASIC("Basic"),
    @XmlEnumValue("Level1")
    LEVEL_1("Level1"),
    @XmlEnumValue("Enhanced")
    ENHANCED("Enhanced"),
    @XmlEnumValue("Advanced")
    ADVANCED("Advanced");
    private final String value;

    GlobalMatchLevelType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GlobalMatchLevelType fromValue(String v) {
        for (GlobalMatchLevelType c: GlobalMatchLevelType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
