
package com.id3global.id3gws._2013._04;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalEmploymentStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GlobalEmploymentStatus"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="FTPerm"/&gt;
 *     &lt;enumeration value="PTPerm"/&gt;
 *     &lt;enumeration value="SelfEmployed"/&gt;
 *     &lt;enumeration value="Retired"/&gt;
 *     &lt;enumeration value="Homemaker"/&gt;
 *     &lt;enumeration value="Unemployed"/&gt;
 *     &lt;enumeration value="Student"/&gt;
 *     &lt;enumeration value="ArmedForces"/&gt;
 *     &lt;enumeration value="Employed"/&gt;
 *     &lt;enumeration value="ReceivesPension"/&gt;
 *     &lt;enumeration value="CarerChild"/&gt;
 *     &lt;enumeration value="CarerOver16"/&gt;
 *     &lt;enumeration value="FTEducation"/&gt;
 *     &lt;enumeration value="Other"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "GlobalEmploymentStatus")
@XmlEnum
public enum GlobalEmploymentStatusType {

    @XmlEnumValue("FTPerm")
    FT_PERM("FTPerm"),
    @XmlEnumValue("PTPerm")
    PT_PERM("PTPerm"),
    @XmlEnumValue("SelfEmployed")
    SELF_EMPLOYED("SelfEmployed"),
    @XmlEnumValue("Retired")
    RETIRED("Retired"),
    @XmlEnumValue("Homemaker")
    HOMEMAKER("Homemaker"),
    @XmlEnumValue("Unemployed")
    UNEMPLOYED("Unemployed"),
    @XmlEnumValue("Student")
    STUDENT("Student"),
    @XmlEnumValue("ArmedForces")
    ARMED_FORCES("ArmedForces"),
    @XmlEnumValue("Employed")
    EMPLOYED("Employed"),
    @XmlEnumValue("ReceivesPension")
    RECEIVES_PENSION("ReceivesPension"),
    @XmlEnumValue("CarerChild")
    CARER_CHILD("CarerChild"),
    @XmlEnumValue("CarerOver16")
    CARER_OVER_16("CarerOver16"),
    @XmlEnumValue("FTEducation")
    FT_EDUCATION("FTEducation"),
    @XmlEnumValue("Other")
    OTHER("Other");
    private final String value;

    GlobalEmploymentStatusType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GlobalEmploymentStatusType fromValue(String v) {
        for (GlobalEmploymentStatusType c: GlobalEmploymentStatusType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
