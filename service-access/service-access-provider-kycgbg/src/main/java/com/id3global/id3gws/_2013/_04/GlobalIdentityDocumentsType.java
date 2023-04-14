
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalIdentityDocuments complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalIdentityDocuments"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="China" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalChina" minOccurs="0"/&gt;
 *         &lt;element name="India" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalIndia" minOccurs="0"/&gt;
 *         &lt;element name="NewZealand" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalNewZealand" minOccurs="0"/&gt;
 *         &lt;element name="InternationalPassport" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalInternationalPassport" minOccurs="0"/&gt;
 *         &lt;element name="EuropeanIdentityCard" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalEuropeanIdentityCard" minOccurs="0"/&gt;
 *         &lt;element name="UK" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalUKData" minOccurs="0"/&gt;
 *         &lt;element name="Australia" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalAustralia" minOccurs="0"/&gt;
 *         &lt;element name="US" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalUS" minOccurs="0"/&gt;
 *         &lt;element name="IdentityCard" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalIdentityCard" minOccurs="0"/&gt;
 *         &lt;element name="Canada" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalCanada" minOccurs="0"/&gt;
 *         &lt;element name="Mexico" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalMexico" minOccurs="0"/&gt;
 *         &lt;element name="Brazil" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalBrazil" minOccurs="0"/&gt;
 *         &lt;element name="Spain" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalSpain" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalIdentityDocuments", propOrder = {
    "china",
    "india",
    "newZealand",
    "internationalPassport",
    "europeanIdentityCard",
    "uk",
    "australia",
    "us",
    "identityCard",
    "canada",
    "mexico",
    "brazil",
    "spain"
})
public class GlobalIdentityDocumentsType {

    @XmlElementRef(name = "China", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalChinaType> china;
    @XmlElementRef(name = "India", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalIndiaType> india;
    @XmlElementRef(name = "NewZealand", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalNewZealandType> newZealand;
    @XmlElementRef(name = "InternationalPassport", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalInternationalPassportType> internationalPassport;
    @XmlElementRef(name = "EuropeanIdentityCard", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalEuropeanIdentityCardType> europeanIdentityCard;
    @XmlElementRef(name = "UK", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalUKDataType> uk;
    @XmlElementRef(name = "Australia", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalAustraliaType> australia;
    @XmlElementRef(name = "US", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalUSType> us;
    @XmlElementRef(name = "IdentityCard", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalIdentityCardType> identityCard;
    @XmlElementRef(name = "Canada", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalCanadaType> canada;
    @XmlElementRef(name = "Mexico", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalMexicoType> mexico;
    @XmlElementRef(name = "Brazil", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalBrazilType> brazil;
    @XmlElementRef(name = "Spain", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalSpainType> spain;

    /**
     * Gets the value of the china property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalChinaType }{@code >}
     *     
     */
    public JAXBElement<GlobalChinaType> getChina() {
        return china;
    }

    /**
     * Sets the value of the china property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalChinaType }{@code >}
     *     
     */
    public void setChina(JAXBElement<GlobalChinaType> value) {
        this.china = value;
    }

    /**
     * Gets the value of the india property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalIndiaType }{@code >}
     *     
     */
    public JAXBElement<GlobalIndiaType> getIndia() {
        return india;
    }

    /**
     * Sets the value of the india property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalIndiaType }{@code >}
     *     
     */
    public void setIndia(JAXBElement<GlobalIndiaType> value) {
        this.india = value;
    }

    /**
     * Gets the value of the newZealand property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalNewZealandType }{@code >}
     *     
     */
    public JAXBElement<GlobalNewZealandType> getNewZealand() {
        return newZealand;
    }

    /**
     * Sets the value of the newZealand property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalNewZealandType }{@code >}
     *     
     */
    public void setNewZealand(JAXBElement<GlobalNewZealandType> value) {
        this.newZealand = value;
    }

    /**
     * Gets the value of the internationalPassport property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalInternationalPassportType }{@code >}
     *     
     */
    public JAXBElement<GlobalInternationalPassportType> getInternationalPassport() {
        return internationalPassport;
    }

    /**
     * Sets the value of the internationalPassport property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalInternationalPassportType }{@code >}
     *     
     */
    public void setInternationalPassport(JAXBElement<GlobalInternationalPassportType> value) {
        this.internationalPassport = value;
    }

    /**
     * Gets the value of the europeanIdentityCard property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalEuropeanIdentityCardType }{@code >}
     *     
     */
    public JAXBElement<GlobalEuropeanIdentityCardType> getEuropeanIdentityCard() {
        return europeanIdentityCard;
    }

    /**
     * Sets the value of the europeanIdentityCard property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalEuropeanIdentityCardType }{@code >}
     *     
     */
    public void setEuropeanIdentityCard(JAXBElement<GlobalEuropeanIdentityCardType> value) {
        this.europeanIdentityCard = value;
    }

    /**
     * Gets the value of the uk property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalUKDataType }{@code >}
     *     
     */
    public JAXBElement<GlobalUKDataType> getUK() {
        return uk;
    }

    /**
     * Sets the value of the uk property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalUKDataType }{@code >}
     *     
     */
    public void setUK(JAXBElement<GlobalUKDataType> value) {
        this.uk = value;
    }

    /**
     * Gets the value of the australia property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalAustraliaType }{@code >}
     *     
     */
    public JAXBElement<GlobalAustraliaType> getAustralia() {
        return australia;
    }

    /**
     * Sets the value of the australia property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalAustraliaType }{@code >}
     *     
     */
    public void setAustralia(JAXBElement<GlobalAustraliaType> value) {
        this.australia = value;
    }

    /**
     * Gets the value of the us property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalUSType }{@code >}
     *     
     */
    public JAXBElement<GlobalUSType> getUS() {
        return us;
    }

    /**
     * Sets the value of the us property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalUSType }{@code >}
     *     
     */
    public void setUS(JAXBElement<GlobalUSType> value) {
        this.us = value;
    }

    /**
     * Gets the value of the identityCard property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalIdentityCardType }{@code >}
     *     
     */
    public JAXBElement<GlobalIdentityCardType> getIdentityCard() {
        return identityCard;
    }

    /**
     * Sets the value of the identityCard property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalIdentityCardType }{@code >}
     *     
     */
    public void setIdentityCard(JAXBElement<GlobalIdentityCardType> value) {
        this.identityCard = value;
    }

    /**
     * Gets the value of the canada property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalCanadaType }{@code >}
     *     
     */
    public JAXBElement<GlobalCanadaType> getCanada() {
        return canada;
    }

    /**
     * Sets the value of the canada property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalCanadaType }{@code >}
     *     
     */
    public void setCanada(JAXBElement<GlobalCanadaType> value) {
        this.canada = value;
    }

    /**
     * Gets the value of the mexico property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalMexicoType }{@code >}
     *     
     */
    public JAXBElement<GlobalMexicoType> getMexico() {
        return mexico;
    }

    /**
     * Sets the value of the mexico property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalMexicoType }{@code >}
     *     
     */
    public void setMexico(JAXBElement<GlobalMexicoType> value) {
        this.mexico = value;
    }

    /**
     * Gets the value of the brazil property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalBrazilType }{@code >}
     *     
     */
    public JAXBElement<GlobalBrazilType> getBrazil() {
        return brazil;
    }

    /**
     * Sets the value of the brazil property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalBrazilType }{@code >}
     *     
     */
    public void setBrazil(JAXBElement<GlobalBrazilType> value) {
        this.brazil = value;
    }

    /**
     * Gets the value of the spain property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalSpainType }{@code >}
     *     
     */
    public JAXBElement<GlobalSpainType> getSpain() {
        return spain;
    }

    /**
     * Sets the value of the spain property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalSpainType }{@code >}
     *     
     */
    public void setSpain(JAXBElement<GlobalSpainType> value) {
        this.spain = value;
    }

}
