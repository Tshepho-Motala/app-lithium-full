
package com.id3global.id3gws._2013._04;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfGlobalSouthAfricaSupplier complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfGlobalSouthAfricaSupplier"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="GlobalSouthAfricaSupplier" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalSouthAfricaSupplier" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfGlobalSouthAfricaSupplier", propOrder = {
    "globalSouthAfricaSupplier"
})
public class ArrayOfGlobalSouthAfricaSupplierType {

    @XmlElement(name = "GlobalSouthAfricaSupplier", nillable = true)
    protected List<GlobalSouthAfricaSupplierType> globalSouthAfricaSupplier;

    /**
     * Gets the value of the globalSouthAfricaSupplier property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the globalSouthAfricaSupplier property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGlobalSouthAfricaSupplier().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GlobalSouthAfricaSupplierType }
     * 
     * 
     */
    public List<GlobalSouthAfricaSupplierType> getGlobalSouthAfricaSupplier() {
        if (globalSouthAfricaSupplier == null) {
            globalSouthAfricaSupplier = new ArrayList<GlobalSouthAfricaSupplierType>();
        }
        return this.globalSouthAfricaSupplier;
    }

}
