
package lithium.service.cashier.processor.bluem.api.data;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StatusSimpleType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="StatusSimpleType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="New"/>
 *     &lt;enumeration value="Open"/>
 *     &lt;enumeration value="Cancelled"/>
 *     &lt;enumeration value="Success"/>
 *     &lt;enumeration value="Failure"/>
 *     &lt;enumeration value="Expired"/>
 *     &lt;enumeration value="Pending"/>
 *     &lt;enumeration value="SuccessManual"/>
 *     &lt;enumeration value="BankSelected"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "StatusSimpleType")
@XmlEnum
public enum StatusSimpleType {

    @XmlEnumValue("New")
    NEW("New"),
    @XmlEnumValue("Open")
    OPEN("Open"),
    @XmlEnumValue("Cancelled")
    CANCELLED("Cancelled"),
    @XmlEnumValue("Success")
    SUCCESS("Success"),
    @XmlEnumValue("Failure")
    FAILURE("Failure"),
    @XmlEnumValue("Expired")
    EXPIRED("Expired"),
    @XmlEnumValue("Pending")
    PENDING("Pending"),
    @XmlEnumValue("SuccessManual")
    SUCCESS_MANUAL("SuccessManual"),
    @XmlEnumValue("BankSelected")
    BANK_SELECTED("BankSelected");
    private final String value;

    StatusSimpleType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static StatusSimpleType fromValue(String v) {
        for (StatusSimpleType c: StatusSimpleType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
