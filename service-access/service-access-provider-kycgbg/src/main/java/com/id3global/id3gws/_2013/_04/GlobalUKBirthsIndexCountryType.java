
package com.id3global.id3gws._2013._04;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalUKBirthsIndexCountry.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GlobalUKBirthsIndexCountry"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="UNSPECIFIED"/&gt;
 *     &lt;enumeration value="ENGLANDWALES"/&gt;
 *     &lt;enumeration value="OTHER"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "GlobalUKBirthsIndexCountry")
@XmlEnum
public enum GlobalUKBirthsIndexCountryType {

    UNSPECIFIED,
    ENGLANDWALES,
    OTHER;

    public String value() {
        return name();
    }

    public static GlobalUKBirthsIndexCountryType fromValue(String v) {
        return valueOf(v);
    }

}
