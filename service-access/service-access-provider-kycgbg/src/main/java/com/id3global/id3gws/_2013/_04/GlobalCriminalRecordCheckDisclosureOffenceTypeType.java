
package com.id3global.id3gws._2013._04;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalCriminalRecordCheckDisclosureOffenceType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GlobalCriminalRecordCheckDisclosureOffenceType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Caution"/&gt;
 *     &lt;enumeration value="Conviction"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "GlobalCriminalRecordCheckDisclosureOffenceType")
@XmlEnum
public enum GlobalCriminalRecordCheckDisclosureOffenceTypeType {

    @XmlEnumValue("Caution")
    CAUTION("Caution"),
    @XmlEnumValue("Conviction")
    CONVICTION("Conviction");
    private final String value;

    GlobalCriminalRecordCheckDisclosureOffenceTypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GlobalCriminalRecordCheckDisclosureOffenceTypeType fromValue(String v) {
        for (GlobalCriminalRecordCheckDisclosureOffenceTypeType c: GlobalCriminalRecordCheckDisclosureOffenceTypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
