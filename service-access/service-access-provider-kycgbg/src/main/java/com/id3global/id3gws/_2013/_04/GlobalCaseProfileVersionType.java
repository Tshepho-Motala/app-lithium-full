
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfunsignedInt;


/**
 * <p>Java class for GlobalCaseProfileVersion complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalCaseProfileVersion"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ProfileVersion" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalProfileVersion" minOccurs="0"/&gt;
 *         &lt;element name="ItemIDs" type="{http://schemas.microsoft.com/2003/10/Serialization/Arrays}ArrayOfunsignedInt" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalCaseProfileVersion", propOrder = {
    "profileVersion",
    "itemIDs"
})
public class GlobalCaseProfileVersionType {

    @XmlElementRef(name = "ProfileVersion", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalProfileVersionType> profileVersion;
    @XmlElementRef(name = "ItemIDs", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfunsignedInt> itemIDs;

    /**
     * Gets the value of the profileVersion property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalProfileVersionType }{@code >}
     *     
     */
    public JAXBElement<GlobalProfileVersionType> getProfileVersion() {
        return profileVersion;
    }

    /**
     * Sets the value of the profileVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalProfileVersionType }{@code >}
     *     
     */
    public void setProfileVersion(JAXBElement<GlobalProfileVersionType> value) {
        this.profileVersion = value;
    }

    /**
     * Gets the value of the itemIDs property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfunsignedInt }{@code >}
     *     
     */
    public JAXBElement<ArrayOfunsignedInt> getItemIDs() {
        return itemIDs;
    }

    /**
     * Sets the value of the itemIDs property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfunsignedInt }{@code >}
     *     
     */
    public void setItemIDs(JAXBElement<ArrayOfunsignedInt> value) {
        this.itemIDs = value;
    }

}
