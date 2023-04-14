
package com.id3global.id3gws._2013._04;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfGlobalMatrixResultItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfGlobalMatrixResultItem"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="GlobalMatrixResultItem" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalMatrixResultItem" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfGlobalMatrixResultItem", propOrder = {
    "globalMatrixResultItem"
})
public class ArrayOfGlobalMatrixResultItemType {

    @XmlElement(name = "GlobalMatrixResultItem", nillable = true)
    protected List<GlobalMatrixResultItemType> globalMatrixResultItem;

    /**
     * Gets the value of the globalMatrixResultItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the globalMatrixResultItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGlobalMatrixResultItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GlobalMatrixResultItemType }
     * 
     * 
     */
    public List<GlobalMatrixResultItemType> getGlobalMatrixResultItem() {
        if (globalMatrixResultItem == null) {
            globalMatrixResultItem = new ArrayList<GlobalMatrixResultItemType>();
        }
        return this.globalMatrixResultItem;
    }

}
