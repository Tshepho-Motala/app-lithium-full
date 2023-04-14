
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalPersonal complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalPersonal"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PersonalDetails" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalPersonalDetails" minOccurs="0"/&gt;
 *         &lt;element name="AlternateName" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalAlternateName" minOccurs="0"/&gt;
 *         &lt;element name="Aliases" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalAlternateName" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalPersonal", propOrder = {
    "personalDetails",
    "alternateName",
    "aliases"
})
public class GlobalPersonalType {

    @XmlElementRef(name = "PersonalDetails", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalPersonalDetailsType> personalDetails;
    @XmlElementRef(name = "AlternateName", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalAlternateNameType> alternateName;
    @XmlElementRef(name = "Aliases", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalAlternateNameType> aliases;

    /**
     * Gets the value of the personalDetails property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalPersonalDetailsType }{@code >}
     *     
     */
    public JAXBElement<GlobalPersonalDetailsType> getPersonalDetails() {
        return personalDetails;
    }

    /**
     * Sets the value of the personalDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalPersonalDetailsType }{@code >}
     *     
     */
    public void setPersonalDetails(JAXBElement<GlobalPersonalDetailsType> value) {
        this.personalDetails = value;
    }

    /**
     * Gets the value of the alternateName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalAlternateNameType }{@code >}
     *     
     */
    public JAXBElement<GlobalAlternateNameType> getAlternateName() {
        return alternateName;
    }

    /**
     * Sets the value of the alternateName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalAlternateNameType }{@code >}
     *     
     */
    public void setAlternateName(JAXBElement<GlobalAlternateNameType> value) {
        this.alternateName = value;
    }

    /**
     * Gets the value of the aliases property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalAlternateNameType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalAlternateNameType> getAliases() {
        return aliases;
    }

    /**
     * Sets the value of the aliases property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalAlternateNameType }{@code >}
     *     
     */
    public void setAliases(JAXBElement<ArrayOfGlobalAlternateNameType> value) {
        this.aliases = value;
    }

}
