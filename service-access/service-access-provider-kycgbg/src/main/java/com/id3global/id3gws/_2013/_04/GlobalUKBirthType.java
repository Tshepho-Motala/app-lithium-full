
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalUKBirth complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalUKBirth"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="MothersMaidenName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SurnameAtBirth" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TownOfBirth" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ProvinceOfBirth" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MunicipalityOfBirth" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Country" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalUKBirthsIndexCountry" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalUKBirth", propOrder = {
    "mothersMaidenName",
    "surnameAtBirth",
    "townOfBirth",
    "provinceOfBirth",
    "municipalityOfBirth",
    "country"
})
public class GlobalUKBirthType {

    @XmlElementRef(name = "MothersMaidenName", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> mothersMaidenName;
    @XmlElementRef(name = "SurnameAtBirth", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> surnameAtBirth;
    @XmlElementRef(name = "TownOfBirth", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> townOfBirth;
    @XmlElementRef(name = "ProvinceOfBirth", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> provinceOfBirth;
    @XmlElementRef(name = "MunicipalityOfBirth", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> municipalityOfBirth;
    @XmlElement(name = "Country")
    @XmlSchemaType(name = "string")
    protected GlobalUKBirthsIndexCountryType country;

    /**
     * Gets the value of the mothersMaidenName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMothersMaidenName() {
        return mothersMaidenName;
    }

    /**
     * Sets the value of the mothersMaidenName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMothersMaidenName(JAXBElement<String> value) {
        this.mothersMaidenName = value;
    }

    /**
     * Gets the value of the surnameAtBirth property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSurnameAtBirth() {
        return surnameAtBirth;
    }

    /**
     * Sets the value of the surnameAtBirth property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSurnameAtBirth(JAXBElement<String> value) {
        this.surnameAtBirth = value;
    }

    /**
     * Gets the value of the townOfBirth property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTownOfBirth() {
        return townOfBirth;
    }

    /**
     * Sets the value of the townOfBirth property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTownOfBirth(JAXBElement<String> value) {
        this.townOfBirth = value;
    }

    /**
     * Gets the value of the provinceOfBirth property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getProvinceOfBirth() {
        return provinceOfBirth;
    }

    /**
     * Sets the value of the provinceOfBirth property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setProvinceOfBirth(JAXBElement<String> value) {
        this.provinceOfBirth = value;
    }

    /**
     * Gets the value of the municipalityOfBirth property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMunicipalityOfBirth() {
        return municipalityOfBirth;
    }

    /**
     * Sets the value of the municipalityOfBirth property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMunicipalityOfBirth(JAXBElement<String> value) {
        this.municipalityOfBirth = value;
    }

    /**
     * Gets the value of the country property.
     * 
     * @return
     *     possible object is
     *     {@link GlobalUKBirthsIndexCountryType }
     *     
     */
    public GlobalUKBirthsIndexCountryType getCountry() {
        return country;
    }

    /**
     * Sets the value of the country property.
     * 
     * @param value
     *     allowed object is
     *     {@link GlobalUKBirthsIndexCountryType }
     *     
     */
    public void setCountry(GlobalUKBirthsIndexCountryType value) {
        this.country = value;
    }

}
