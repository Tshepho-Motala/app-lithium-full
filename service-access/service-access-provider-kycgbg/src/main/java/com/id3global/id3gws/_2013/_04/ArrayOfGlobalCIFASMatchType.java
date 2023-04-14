
package com.id3global.id3gws._2013._04;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfGlobalCIFASMatch complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfGlobalCIFASMatch"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="GlobalCIFASMatch" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalCIFASMatch" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfGlobalCIFASMatch", propOrder = {
    "globalCIFASMatch"
})
public class ArrayOfGlobalCIFASMatchType {

    @XmlElement(name = "GlobalCIFASMatch", nillable = true)
    protected List<GlobalCIFASMatchType> globalCIFASMatch;

    /**
     * Gets the value of the globalCIFASMatch property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the globalCIFASMatch property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGlobalCIFASMatch().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GlobalCIFASMatchType }
     * 
     * 
     */
    public List<GlobalCIFASMatchType> getGlobalCIFASMatch() {
        if (globalCIFASMatch == null) {
            globalCIFASMatch = new ArrayList<GlobalCIFASMatchType>();
        }
        return this.globalCIFASMatch;
    }

}
