
package com.id3global.id3gws._2013._04;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfGlobalCIFASProductCode complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfGlobalCIFASProductCode"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="GlobalCIFASProductCode" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalCIFASProductCode" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfGlobalCIFASProductCode", propOrder = {
    "globalCIFASProductCode"
})
public class ArrayOfGlobalCIFASProductCodeType {

    @XmlElement(name = "GlobalCIFASProductCode", nillable = true)
    protected List<GlobalCIFASProductCodeType> globalCIFASProductCode;

    /**
     * Gets the value of the globalCIFASProductCode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the globalCIFASProductCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGlobalCIFASProductCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GlobalCIFASProductCodeType }
     * 
     * 
     */
    public List<GlobalCIFASProductCodeType> getGlobalCIFASProductCode() {
        if (globalCIFASProductCode == null) {
            globalCIFASProductCode = new ArrayList<GlobalCIFASProductCodeType>();
        }
        return this.globalCIFASProductCode;
    }

}
