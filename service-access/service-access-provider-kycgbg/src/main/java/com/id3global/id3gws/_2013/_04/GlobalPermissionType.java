
package com.id3global.id3gws._2013._04;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalPermission.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GlobalPermission"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="None"/&gt;
 *     &lt;enumeration value="Read"/&gt;
 *     &lt;enumeration value="Execute"/&gt;
 *     &lt;enumeration value="Manage"/&gt;
 *     &lt;enumeration value="Full"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "GlobalPermission")
@XmlEnum
public enum GlobalPermissionType {

    @XmlEnumValue("None")
    NONE("None"),
    @XmlEnumValue("Read")
    READ("Read"),
    @XmlEnumValue("Execute")
    EXECUTE("Execute"),
    @XmlEnumValue("Manage")
    MANAGE("Manage"),
    @XmlEnumValue("Full")
    FULL("Full");
    private final String value;

    GlobalPermissionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GlobalPermissionType fromValue(String v) {
        for (GlobalPermissionType c: GlobalPermissionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
