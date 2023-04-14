
package org.datacontract.schemas._2004._07.globalcheck_useraccountlib;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Enums.Format.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Enums.Format"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="TSV"/&gt;
 *     &lt;enumeration value="CSV"/&gt;
 *     &lt;enumeration value="EXCEL"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "Enums.Format", namespace = "http://schemas.datacontract.org/2004/07/GlobalCheck.UserAccountLib.Common")
@XmlEnum
public enum EnumsFormat {

    TSV,
    CSV,
    EXCEL;

    public String value() {
        return name();
    }

    public static EnumsFormat fromValue(String v) {
        return valueOf(v);
    }

}
