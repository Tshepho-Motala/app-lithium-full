
package com.id3global.id3gws._2013._04;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalMatch.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GlobalMatch"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="NA"/&gt;
 *     &lt;enumeration value="Match"/&gt;
 *     &lt;enumeration value="Mismatch"/&gt;
 *     &lt;enumeration value="Nomatch"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "GlobalMatch")
@XmlEnum
public enum GlobalMatchType {

    NA("NA"),
    @XmlEnumValue("Match")
    MATCH("Match"),
    @XmlEnumValue("Mismatch")
    MISMATCH("Mismatch"),
    @XmlEnumValue("Nomatch")
    NOMATCH("Nomatch");
    private final String value;

    GlobalMatchType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GlobalMatchType fromValue(String v) {
        for (GlobalMatchType c: GlobalMatchType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
