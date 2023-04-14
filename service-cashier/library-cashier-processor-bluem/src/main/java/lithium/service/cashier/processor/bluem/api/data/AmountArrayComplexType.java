
package lithium.service.cashier.processor.bluem.api.data;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AmountArrayComplexType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AmountArrayComplexType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AmountOption" type="{}AmountOptionComplexType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AmountArrayComplexType", propOrder = {
    "amountOption"
})
public class AmountArrayComplexType {

    @XmlElement(name = "AmountOption", required = true)
    protected List<AmountOptionComplexType> amountOption;

    /**
     * Gets the value of the amountOption property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the amountOption property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAmountOption().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AmountOptionComplexType }
     * 
     * 
     */
    public List<AmountOptionComplexType> getAmountOption() {
        if (amountOption == null) {
            amountOption = new ArrayList<AmountOptionComplexType>();
        }
        return this.amountOption;
    }

}
