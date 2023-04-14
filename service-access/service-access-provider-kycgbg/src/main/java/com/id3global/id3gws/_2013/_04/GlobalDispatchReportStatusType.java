
package com.id3global.id3gws._2013._04;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalDispatchReportStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GlobalDispatchReportStatus"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="None"/&gt;
 *     &lt;enumeration value="Clear"/&gt;
 *     &lt;enumeration value="Unclear"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "GlobalDispatchReportStatus")
@XmlEnum
public enum GlobalDispatchReportStatusType {

    @XmlEnumValue("None")
    NONE("None"),
    @XmlEnumValue("Clear")
    CLEAR("Clear"),
    @XmlEnumValue("Unclear")
    UNCLEAR("Unclear");
    private final String value;

    GlobalDispatchReportStatusType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GlobalDispatchReportStatusType fromValue(String v) {
        for (GlobalDispatchReportStatusType c: GlobalDispatchReportStatusType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
