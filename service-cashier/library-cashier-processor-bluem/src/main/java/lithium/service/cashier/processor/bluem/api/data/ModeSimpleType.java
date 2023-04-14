
package lithium.service.cashier.processor.bluem.api.data;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ModeSimpleType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ModeSimpleType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="direct"/>
 *     &lt;enumeration value="batch"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ModeSimpleType")
@XmlEnum
public enum ModeSimpleType {

    @XmlEnumValue("direct")
    DIRECT("direct"),
    @XmlEnumValue("batch")
    BATCH("batch");
    private final String value;

    ModeSimpleType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ModeSimpleType fromValue(String v) {
        for (ModeSimpleType c: ModeSimpleType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
