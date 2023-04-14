
package com.id3global.id3gws._2013._04;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalDocumentType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GlobalDocumentType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Passport"/&gt;
 *     &lt;enumeration value="DrivingLicence"/&gt;
 *     &lt;enumeration value="IDCard"/&gt;
 *     &lt;enumeration value="Unknown"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "GlobalDocumentType")
@XmlEnum
public enum GlobalDocumentTypeType {

    @XmlEnumValue("Passport")
    PASSPORT("Passport"),
    @XmlEnumValue("DrivingLicence")
    DRIVING_LICENCE("DrivingLicence"),
    @XmlEnumValue("IDCard")
    ID_CARD("IDCard"),
    @XmlEnumValue("Unknown")
    UNKNOWN("Unknown");
    private final String value;

    GlobalDocumentTypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GlobalDocumentTypeType fromValue(String v) {
        for (GlobalDocumentTypeType c: GlobalDocumentTypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
