
package lithium.service.cashier.processor.bluem.api.data;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CurrencySimpleType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CurrencySimpleType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="EUR"/>
 *     &lt;enumeration value="GBP"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CurrencySimpleType")
@XmlEnum
public enum CurrencySimpleType {

    EUR,
    GBP;

    public String value() {
        return name();
    }

    public static CurrencySimpleType fromValue(String v) {
        return valueOf(v);
    }

}
