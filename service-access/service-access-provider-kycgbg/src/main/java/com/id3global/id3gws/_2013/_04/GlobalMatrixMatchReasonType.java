
package com.id3global.id3gws._2013._04;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalMatrixMatchReason.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GlobalMatrixMatchReason"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="None"/&gt;
 *     &lt;enumeration value="InsufficientDataMatches"/&gt;
 *     &lt;enumeration value="InvalidDetails"/&gt;
 *     &lt;enumeration value="DataMatchAlert"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "GlobalMatrixMatchReason")
@XmlEnum
public enum GlobalMatrixMatchReasonType {

    @XmlEnumValue("None")
    NONE("None"),
    @XmlEnumValue("InsufficientDataMatches")
    INSUFFICIENT_DATA_MATCHES("InsufficientDataMatches"),
    @XmlEnumValue("InvalidDetails")
    INVALID_DETAILS("InvalidDetails"),
    @XmlEnumValue("DataMatchAlert")
    DATA_MATCH_ALERT("DataMatchAlert");
    private final String value;

    GlobalMatrixMatchReasonType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GlobalMatrixMatchReasonType fromValue(String v) {
        for (GlobalMatrixMatchReasonType c: GlobalMatrixMatchReasonType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
