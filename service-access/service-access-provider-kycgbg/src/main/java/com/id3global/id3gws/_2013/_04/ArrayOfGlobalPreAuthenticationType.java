
package com.id3global.id3gws._2013._04;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfGlobalPreAuthentication complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfGlobalPreAuthentication"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="GlobalPreAuthentication" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalPreAuthentication" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfGlobalPreAuthentication", propOrder = {
    "globalPreAuthentication"
})
public class ArrayOfGlobalPreAuthenticationType {

    @XmlElement(name = "GlobalPreAuthentication", nillable = true)
    protected List<GlobalPreAuthenticationType> globalPreAuthentication;

    /**
     * Gets the value of the globalPreAuthentication property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the globalPreAuthentication property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGlobalPreAuthentication().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GlobalPreAuthenticationType }
     * 
     * 
     */
    public List<GlobalPreAuthenticationType> getGlobalPreAuthentication() {
        if (globalPreAuthentication == null) {
            globalPreAuthentication = new ArrayList<GlobalPreAuthenticationType>();
        }
        return this.globalPreAuthentication;
    }

}
