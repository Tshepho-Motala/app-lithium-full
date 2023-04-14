
package com.id3global.id3gws._2013._04;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalProduct.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GlobalProduct"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ID3check"/&gt;
 *     &lt;enumeration value="URU"/&gt;
 *     &lt;enumeration value="ID3global"/&gt;
 *     &lt;enumeration value="KYP"/&gt;
 *     &lt;enumeration value="LiteIDV"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "GlobalProduct")
@XmlEnum
public enum GlobalProductType {

    @XmlEnumValue("ID3check")
    ID_3_CHECK("ID3check"),
    URU("URU"),
    @XmlEnumValue("ID3global")
    ID_3_GLOBAL("ID3global"),
    KYP("KYP"),
    @XmlEnumValue("LiteIDV")
    LITE_IDV("LiteIDV");
    private final String value;

    GlobalProductType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GlobalProductType fromValue(String v) {
        for (GlobalProductType c: GlobalProductType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
