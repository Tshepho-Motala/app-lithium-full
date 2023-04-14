
package lithium.service.cashier.processor.bluem.api.data;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PaymentMethodsSimpleType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PaymentMethodsSimpleType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="IDEAL"/>
 *     &lt;enumeration value="PAYPAL"/>
 *     &lt;enumeration value="VISA_MASTER"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PaymentMethodsSimpleType")
@XmlEnum
public enum PaymentMethodsSimpleType {

    IDEAL,
    PAYPAL,
    VISA_MASTER;

    public String value() {
        return name();
    }

    public static PaymentMethodsSimpleType fromValue(String v) {
        return valueOf(v);
    }

}
