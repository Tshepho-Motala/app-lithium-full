
package com.id3global.id3gws._2013._04;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalMatrixMatchType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GlobalMatrixMatchType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Unclassified"/&gt;
 *     &lt;enumeration value="Pass"/&gt;
 *     &lt;enumeration value="Refer"/&gt;
 *     &lt;enumeration value="Alert"/&gt;
 *     &lt;enumeration value="Fail"/&gt;
 *     &lt;enumeration value="NA"/&gt;
 *     &lt;enumeration value="Match"/&gt;
 *     &lt;enumeration value="Mismatch"/&gt;
 *     &lt;enumeration value="Nomatch"/&gt;
 *     &lt;enumeration value="Multiple"/&gt;
 *     &lt;enumeration value="Pending"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "GlobalMatrixMatchType")
@XmlEnum
public enum GlobalMatrixMatchTypeType {

    @XmlEnumValue("Unclassified")
    UNCLASSIFIED("Unclassified"),
    @XmlEnumValue("Pass")
    PASS("Pass"),
    @XmlEnumValue("Refer")
    REFER("Refer"),
    @XmlEnumValue("Alert")
    ALERT("Alert"),
    @XmlEnumValue("Fail")
    FAIL("Fail"),
    NA("NA"),
    @XmlEnumValue("Match")
    MATCH("Match"),
    @XmlEnumValue("Mismatch")
    MISMATCH("Mismatch"),
    @XmlEnumValue("Nomatch")
    NOMATCH("Nomatch"),
    @XmlEnumValue("Multiple")
    MULTIPLE("Multiple"),
    @XmlEnumValue("Pending")
    PENDING("Pending");
    private final String value;

    GlobalMatrixMatchTypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GlobalMatrixMatchTypeType fromValue(String v) {
        for (GlobalMatrixMatchTypeType c: GlobalMatrixMatchTypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
