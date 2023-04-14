
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfstring;


/**
 * <p>Java class for GlobalPEPIntelligenceData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalPEPIntelligenceData"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Fullname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Aliases" type="{http://schemas.microsoft.com/2003/10/Serialization/Arrays}ArrayOfstring" minOccurs="0"/&gt;
 *         &lt;element name="SanctionsAddresses" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalSanctionsAddress" minOccurs="0"/&gt;
 *         &lt;element name="SanctionsDates" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalSanctionsDate" minOccurs="0"/&gt;
 *         &lt;element name="IdentityInformation" type="{http://schemas.microsoft.com/2003/10/Serialization/Arrays}ArrayOfstring" minOccurs="0"/&gt;
 *         &lt;element name="SanctionsPositions" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalSanctionsPosition" minOccurs="0"/&gt;
 *         &lt;element name="Tier" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalPEPIntelligenceData", propOrder = {
    "fullname",
    "aliases",
    "sanctionsAddresses",
    "sanctionsDates",
    "identityInformation",
    "sanctionsPositions",
    "tier"
})
public class GlobalPEPIntelligenceDataType {

    @XmlElementRef(name = "Fullname", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> fullname;
    @XmlElementRef(name = "Aliases", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfstring> aliases;
    @XmlElementRef(name = "SanctionsAddresses", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalSanctionsAddressType> sanctionsAddresses;
    @XmlElementRef(name = "SanctionsDates", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalSanctionsDateType> sanctionsDates;
    @XmlElementRef(name = "IdentityInformation", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfstring> identityInformation;
    @XmlElementRef(name = "SanctionsPositions", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalSanctionsPositionType> sanctionsPositions;
    @XmlElement(name = "Tier")
    protected Integer tier;

    /**
     * Gets the value of the fullname property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getFullname() {
        return fullname;
    }

    /**
     * Sets the value of the fullname property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setFullname(JAXBElement<String> value) {
        this.fullname = value;
    }

    /**
     * Gets the value of the aliases property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}
     *     
     */
    public JAXBElement<ArrayOfstring> getAliases() {
        return aliases;
    }

    /**
     * Sets the value of the aliases property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}
     *     
     */
    public void setAliases(JAXBElement<ArrayOfstring> value) {
        this.aliases = value;
    }

    /**
     * Gets the value of the sanctionsAddresses property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalSanctionsAddressType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalSanctionsAddressType> getSanctionsAddresses() {
        return sanctionsAddresses;
    }

    /**
     * Sets the value of the sanctionsAddresses property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalSanctionsAddressType }{@code >}
     *     
     */
    public void setSanctionsAddresses(JAXBElement<ArrayOfGlobalSanctionsAddressType> value) {
        this.sanctionsAddresses = value;
    }

    /**
     * Gets the value of the sanctionsDates property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalSanctionsDateType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalSanctionsDateType> getSanctionsDates() {
        return sanctionsDates;
    }

    /**
     * Sets the value of the sanctionsDates property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalSanctionsDateType }{@code >}
     *     
     */
    public void setSanctionsDates(JAXBElement<ArrayOfGlobalSanctionsDateType> value) {
        this.sanctionsDates = value;
    }

    /**
     * Gets the value of the identityInformation property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}
     *     
     */
    public JAXBElement<ArrayOfstring> getIdentityInformation() {
        return identityInformation;
    }

    /**
     * Sets the value of the identityInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}
     *     
     */
    public void setIdentityInformation(JAXBElement<ArrayOfstring> value) {
        this.identityInformation = value;
    }

    /**
     * Gets the value of the sanctionsPositions property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalSanctionsPositionType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalSanctionsPositionType> getSanctionsPositions() {
        return sanctionsPositions;
    }

    /**
     * Sets the value of the sanctionsPositions property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalSanctionsPositionType }{@code >}
     *     
     */
    public void setSanctionsPositions(JAXBElement<ArrayOfGlobalSanctionsPositionType> value) {
        this.sanctionsPositions = value;
    }

    /**
     * Gets the value of the tier property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTier() {
        return tier;
    }

    /**
     * Sets the value of the tier property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTier(Integer value) {
        this.tier = value;
    }

}
