
package com.id3global.id3gws._2013._04;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfGlobalCriminalRecordCheckDisclosureOffence complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfGlobalCriminalRecordCheckDisclosureOffence"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="GlobalCriminalRecordCheckDisclosureOffence" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalCriminalRecordCheckDisclosureOffence" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfGlobalCriminalRecordCheckDisclosureOffence", propOrder = {
    "globalCriminalRecordCheckDisclosureOffence"
})
public class ArrayOfGlobalCriminalRecordCheckDisclosureOffenceType {

    @XmlElement(name = "GlobalCriminalRecordCheckDisclosureOffence", nillable = true)
    protected List<GlobalCriminalRecordCheckDisclosureOffenceType> globalCriminalRecordCheckDisclosureOffence;

    /**
     * Gets the value of the globalCriminalRecordCheckDisclosureOffence property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the globalCriminalRecordCheckDisclosureOffence property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGlobalCriminalRecordCheckDisclosureOffence().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GlobalCriminalRecordCheckDisclosureOffenceType }
     * 
     * 
     */
    public List<GlobalCriminalRecordCheckDisclosureOffenceType> getGlobalCriminalRecordCheckDisclosureOffence() {
        if (globalCriminalRecordCheckDisclosureOffence == null) {
            globalCriminalRecordCheckDisclosureOffence = new ArrayList<GlobalCriminalRecordCheckDisclosureOffenceType>();
        }
        return this.globalCriminalRecordCheckDisclosureOffence;
    }

}
