
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalInputData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalInputData"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Images" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalImage" minOccurs="0"/&gt;
 *         &lt;element name="Personal" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalPersonal" minOccurs="0"/&gt;
 *         &lt;element name="Addresses" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalAddresses" minOccurs="0"/&gt;
 *         &lt;element name="IdentityDocuments" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalIdentityDocuments" minOccurs="0"/&gt;
 *         &lt;element name="AddressDocuments" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalAddressDocuments" minOccurs="0"/&gt;
 *         &lt;element name="ContactDetails" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalContactDetails" minOccurs="0"/&gt;
 *         &lt;element name="Employment" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalEmployment" minOccurs="0"/&gt;
 *         &lt;element name="BankingDetails" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalBankingDetails" minOccurs="0"/&gt;
 *         &lt;element name="GlobalGeneric" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalGeneric" minOccurs="0"/&gt;
 *         &lt;element name="Location" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalLocation" minOccurs="0"/&gt;
 *         &lt;element name="Consent" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalConsent" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalInputData", propOrder = {
    "images",
    "personal",
    "addresses",
    "identityDocuments",
    "addressDocuments",
    "contactDetails",
    "employment",
    "bankingDetails",
    "globalGeneric",
    "location",
    "consent"
})
public class GlobalInputDataType {

    @XmlElementRef(name = "Images", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalImageType> images;
    @XmlElementRef(name = "Personal", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalPersonalType> personal;
    @XmlElementRef(name = "Addresses", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalAddressesType> addresses;
    @XmlElementRef(name = "IdentityDocuments", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalIdentityDocumentsType> identityDocuments;
    @XmlElementRef(name = "AddressDocuments", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalAddressDocumentsType> addressDocuments;
    @XmlElementRef(name = "ContactDetails", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalContactDetailsType> contactDetails;
    @XmlElementRef(name = "Employment", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalEmploymentType> employment;
    @XmlElementRef(name = "BankingDetails", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalBankingDetailsType> bankingDetails;
    @XmlElementRef(name = "GlobalGeneric", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalGenericType> globalGeneric;
    @XmlElementRef(name = "Location", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalLocationType> location;
    @XmlElementRef(name = "Consent", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalConsentType> consent;

    /**
     * Gets the value of the images property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalImageType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalImageType> getImages() {
        return images;
    }

    /**
     * Sets the value of the images property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalImageType }{@code >}
     *     
     */
    public void setImages(JAXBElement<ArrayOfGlobalImageType> value) {
        this.images = value;
    }

    /**
     * Gets the value of the personal property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalPersonalType }{@code >}
     *     
     */
    public JAXBElement<GlobalPersonalType> getPersonal() {
        return personal;
    }

    /**
     * Sets the value of the personal property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalPersonalType }{@code >}
     *     
     */
    public void setPersonal(JAXBElement<GlobalPersonalType> value) {
        this.personal = value;
    }

    /**
     * Gets the value of the addresses property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalAddressesType }{@code >}
     *     
     */
    public JAXBElement<GlobalAddressesType> getAddresses() {
        return addresses;
    }

    /**
     * Sets the value of the addresses property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalAddressesType }{@code >}
     *     
     */
    public void setAddresses(JAXBElement<GlobalAddressesType> value) {
        this.addresses = value;
    }

    /**
     * Gets the value of the identityDocuments property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalIdentityDocumentsType }{@code >}
     *     
     */
    public JAXBElement<GlobalIdentityDocumentsType> getIdentityDocuments() {
        return identityDocuments;
    }

    /**
     * Sets the value of the identityDocuments property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalIdentityDocumentsType }{@code >}
     *     
     */
    public void setIdentityDocuments(JAXBElement<GlobalIdentityDocumentsType> value) {
        this.identityDocuments = value;
    }

    /**
     * Gets the value of the addressDocuments property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalAddressDocumentsType }{@code >}
     *     
     */
    public JAXBElement<GlobalAddressDocumentsType> getAddressDocuments() {
        return addressDocuments;
    }

    /**
     * Sets the value of the addressDocuments property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalAddressDocumentsType }{@code >}
     *     
     */
    public void setAddressDocuments(JAXBElement<GlobalAddressDocumentsType> value) {
        this.addressDocuments = value;
    }

    /**
     * Gets the value of the contactDetails property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalContactDetailsType }{@code >}
     *     
     */
    public JAXBElement<GlobalContactDetailsType> getContactDetails() {
        return contactDetails;
    }

    /**
     * Sets the value of the contactDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalContactDetailsType }{@code >}
     *     
     */
    public void setContactDetails(JAXBElement<GlobalContactDetailsType> value) {
        this.contactDetails = value;
    }

    /**
     * Gets the value of the employment property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalEmploymentType }{@code >}
     *     
     */
    public JAXBElement<GlobalEmploymentType> getEmployment() {
        return employment;
    }

    /**
     * Sets the value of the employment property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalEmploymentType }{@code >}
     *     
     */
    public void setEmployment(JAXBElement<GlobalEmploymentType> value) {
        this.employment = value;
    }

    /**
     * Gets the value of the bankingDetails property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalBankingDetailsType }{@code >}
     *     
     */
    public JAXBElement<GlobalBankingDetailsType> getBankingDetails() {
        return bankingDetails;
    }

    /**
     * Sets the value of the bankingDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalBankingDetailsType }{@code >}
     *     
     */
    public void setBankingDetails(JAXBElement<GlobalBankingDetailsType> value) {
        this.bankingDetails = value;
    }

    /**
     * Gets the value of the globalGeneric property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalGenericType }{@code >}
     *     
     */
    public JAXBElement<GlobalGenericType> getGlobalGeneric() {
        return globalGeneric;
    }

    /**
     * Sets the value of the globalGeneric property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalGenericType }{@code >}
     *     
     */
    public void setGlobalGeneric(JAXBElement<GlobalGenericType> value) {
        this.globalGeneric = value;
    }

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalLocationType }{@code >}
     *     
     */
    public JAXBElement<GlobalLocationType> getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalLocationType }{@code >}
     *     
     */
    public void setLocation(JAXBElement<GlobalLocationType> value) {
        this.location = value;
    }

    /**
     * Gets the value of the consent property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalConsentType }{@code >}
     *     
     */
    public JAXBElement<GlobalConsentType> getConsent() {
        return consent;
    }

    /**
     * Sets the value of the consent property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalConsentType }{@code >}
     *     
     */
    public void setConsent(JAXBElement<GlobalConsentType> value) {
        this.consent = value;
    }

}
