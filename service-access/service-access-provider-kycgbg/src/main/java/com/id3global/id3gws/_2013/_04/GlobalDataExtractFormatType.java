
package com.id3global.id3gws._2013._04;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalDataExtractFormat.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GlobalDataExtractFormat"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="CSV"/&gt;
 *     &lt;enumeration value="TSV"/&gt;
 *     &lt;enumeration value="EXCEL"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "GlobalDataExtractFormat")
@XmlEnum
public enum GlobalDataExtractFormatType {

    CSV,
    TSV,
    EXCEL;

    public String value() {
        return name();
    }

    public static GlobalDataExtractFormatType fromValue(String v) {
        return valueOf(v);
    }

}
