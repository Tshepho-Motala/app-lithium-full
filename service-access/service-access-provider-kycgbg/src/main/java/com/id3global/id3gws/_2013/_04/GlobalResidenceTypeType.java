
package com.id3global.id3gws._2013._04;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalResidenceType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GlobalResidenceType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="HomeOwnerOutright"/&gt;
 *     &lt;enumeration value="HomeOwnerMortgage"/&gt;
 *     &lt;enumeration value="Tenant"/&gt;
 *     &lt;enumeration value="LivingWithRelatives"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "GlobalResidenceType")
@XmlEnum
public enum GlobalResidenceTypeType {

    @XmlEnumValue("HomeOwnerOutright")
    HOME_OWNER_OUTRIGHT("HomeOwnerOutright"),
    @XmlEnumValue("HomeOwnerMortgage")
    HOME_OWNER_MORTGAGE("HomeOwnerMortgage"),
    @XmlEnumValue("Tenant")
    TENANT("Tenant"),
    @XmlEnumValue("LivingWithRelatives")
    LIVING_WITH_RELATIVES("LivingWithRelatives");
    private final String value;

    GlobalResidenceTypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GlobalResidenceTypeType fromValue(String v) {
        for (GlobalResidenceTypeType c: GlobalResidenceTypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
