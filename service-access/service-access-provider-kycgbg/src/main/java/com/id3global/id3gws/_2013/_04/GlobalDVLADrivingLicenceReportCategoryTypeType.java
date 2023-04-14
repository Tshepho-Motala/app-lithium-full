
package com.id3global.id3gws._2013._04;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalDVLADrivingLicenceReportCategoryType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GlobalDVLADrivingLicenceReportCategoryType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Full"/&gt;
 *     &lt;enumeration value="Provisional"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "GlobalDVLADrivingLicenceReportCategoryType")
@XmlEnum
public enum GlobalDVLADrivingLicenceReportCategoryTypeType {

    @XmlEnumValue("Full")
    FULL("Full"),
    @XmlEnumValue("Provisional")
    PROVISIONAL("Provisional");
    private final String value;

    GlobalDVLADrivingLicenceReportCategoryTypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GlobalDVLADrivingLicenceReportCategoryTypeType fromValue(String v) {
        for (GlobalDVLADrivingLicenceReportCategoryTypeType c: GlobalDVLADrivingLicenceReportCategoryTypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
