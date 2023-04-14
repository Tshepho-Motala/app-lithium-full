
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalSupportedFields complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalSupportedFields"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AddressFormat" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalAddressFormat" minOccurs="0"/&gt;
 *         &lt;element name="AddressLookupCountry" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DocumentImageStorage" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="DocumentImageValidation" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="PowerSearchEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="SupportedFields" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalSupportedField" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalSupportedFields", propOrder = {
    "addressFormat",
    "addressLookupCountry",
    "documentImageStorage",
    "documentImageValidation",
    "powerSearchEnabled",
    "supportedFields"
})
public class GlobalSupportedFieldsType {

    @XmlElement(name = "AddressFormat")
    @XmlSchemaType(name = "string")
    protected GlobalAddressFormatType addressFormat;
    @XmlElementRef(name = "AddressLookupCountry", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> addressLookupCountry;
    @XmlElement(name = "DocumentImageStorage")
    protected Boolean documentImageStorage;
    @XmlElement(name = "DocumentImageValidation")
    protected Boolean documentImageValidation;
    @XmlElement(name = "PowerSearchEnabled")
    protected Boolean powerSearchEnabled;
    @XmlElementRef(name = "SupportedFields", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalSupportedFieldType> supportedFields;

    /**
     * Gets the value of the addressFormat property.
     * 
     * @return
     *     possible object is
     *     {@link GlobalAddressFormatType }
     *     
     */
    public GlobalAddressFormatType getAddressFormat() {
        return addressFormat;
    }

    /**
     * Sets the value of the addressFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link GlobalAddressFormatType }
     *     
     */
    public void setAddressFormat(GlobalAddressFormatType value) {
        this.addressFormat = value;
    }

    /**
     * Gets the value of the addressLookupCountry property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAddressLookupCountry() {
        return addressLookupCountry;
    }

    /**
     * Sets the value of the addressLookupCountry property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAddressLookupCountry(JAXBElement<String> value) {
        this.addressLookupCountry = value;
    }

    /**
     * Gets the value of the documentImageStorage property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDocumentImageStorage() {
        return documentImageStorage;
    }

    /**
     * Sets the value of the documentImageStorage property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDocumentImageStorage(Boolean value) {
        this.documentImageStorage = value;
    }

    /**
     * Gets the value of the documentImageValidation property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDocumentImageValidation() {
        return documentImageValidation;
    }

    /**
     * Sets the value of the documentImageValidation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDocumentImageValidation(Boolean value) {
        this.documentImageValidation = value;
    }

    /**
     * Gets the value of the powerSearchEnabled property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPowerSearchEnabled() {
        return powerSearchEnabled;
    }

    /**
     * Sets the value of the powerSearchEnabled property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPowerSearchEnabled(Boolean value) {
        this.powerSearchEnabled = value;
    }

    /**
     * Gets the value of the supportedFields property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalSupportedFieldType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalSupportedFieldType> getSupportedFields() {
        return supportedFields;
    }

    /**
     * Sets the value of the supportedFields property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalSupportedFieldType }{@code >}
     *     
     */
    public void setSupportedFields(JAXBElement<ArrayOfGlobalSupportedFieldType> value) {
        this.supportedFields = value;
    }

}
