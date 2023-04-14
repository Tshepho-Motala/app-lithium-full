
package com.id3global.id3gws._2013._04;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalMatrixSummaryTemplateType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GlobalMatrixSummaryTemplateType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="None"/&gt;
 *     &lt;enumeration value="Address"/&gt;
 *     &lt;enumeration value="Cells"/&gt;
 *     &lt;enumeration value="Filter"/&gt;
 *     &lt;enumeration value="Disclosure"/&gt;
 *     &lt;enumeration value="Licence"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "GlobalMatrixSummaryTemplateType")
@XmlEnum
public enum GlobalMatrixSummaryTemplateTypeType {

    @XmlEnumValue("None")
    NONE("None"),
    @XmlEnumValue("Address")
    ADDRESS("Address"),
    @XmlEnumValue("Cells")
    CELLS("Cells"),
    @XmlEnumValue("Filter")
    FILTER("Filter"),
    @XmlEnumValue("Disclosure")
    DISCLOSURE("Disclosure"),
    @XmlEnumValue("Licence")
    LICENCE("Licence");
    private final String value;

    GlobalMatrixSummaryTemplateTypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GlobalMatrixSummaryTemplateTypeType fromValue(String v) {
        for (GlobalMatrixSummaryTemplateTypeType c: GlobalMatrixSummaryTemplateTypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
