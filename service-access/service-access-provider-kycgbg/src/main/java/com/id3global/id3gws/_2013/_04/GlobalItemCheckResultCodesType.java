
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalItemCheckResultCodes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalItemCheckResultCodes"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Comment" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalItemCheckResultCode" minOccurs="0"/&gt;
 *         &lt;element name="Match" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalItemCheckResultCode" minOccurs="0"/&gt;
 *         &lt;element name="Warning" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalItemCheckResultCode" minOccurs="0"/&gt;
 *         &lt;element name="Mismatch" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalItemCheckResultCode" minOccurs="0"/&gt;
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="Pass" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalMatch" minOccurs="0"/&gt;
 *         &lt;element name="Address" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalMatch" minOccurs="0"/&gt;
 *         &lt;element name="Forename" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalMatch" minOccurs="0"/&gt;
 *         &lt;element name="Surname" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalMatch" minOccurs="0"/&gt;
 *         &lt;element name="DOB" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalMatch" minOccurs="0"/&gt;
 *         &lt;element name="Alert" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalMatch" minOccurs="0"/&gt;
 *         &lt;element name="Country" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalItemCheckResultCodes", propOrder = {
    "name",
    "description",
    "comment",
    "match",
    "warning",
    "mismatch",
    "id",
    "pass",
    "address",
    "forename",
    "surname",
    "dob",
    "alert",
    "country"
})
@XmlSeeAlso({
    GlobalTelephoneResultCodesType.class,
    GlobalSharedFraudResultCodesType.class,
    GlobalSanctionsResultCodesType.class,
    GlobalDeviceIDResultCodesType.class,
    GlobalCreditReportResultCodesType.class,
    Global3DSecureCheckResultCodesType.class,
    GlobalDocumentCheckResultCodesType.class,
    GlobalLifestyleResultCodesType.class,
    GlobalCIFASResultCodesType.class,
    GlobalCreditHeaderResultCodesType.class
})
public class GlobalItemCheckResultCodesType {

    @XmlElementRef(name = "Name", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> name;
    @XmlElementRef(name = "Description", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> description;
    @XmlElementRef(name = "Comment", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalItemCheckResultCodeType> comment;
    @XmlElementRef(name = "Match", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalItemCheckResultCodeType> match;
    @XmlElementRef(name = "Warning", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalItemCheckResultCodeType> warning;
    @XmlElementRef(name = "Mismatch", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalItemCheckResultCodeType> mismatch;
    @XmlElement(name = "ID")
    protected Integer id;
    @XmlElement(name = "Pass")
    @XmlSchemaType(name = "string")
    protected GlobalMatchType pass;
    @XmlElement(name = "Address")
    @XmlSchemaType(name = "string")
    protected GlobalMatchType address;
    @XmlElement(name = "Forename")
    @XmlSchemaType(name = "string")
    protected GlobalMatchType forename;
    @XmlElement(name = "Surname")
    @XmlSchemaType(name = "string")
    protected GlobalMatchType surname;
    @XmlElement(name = "DOB")
    @XmlSchemaType(name = "string")
    protected GlobalMatchType dob;
    @XmlElement(name = "Alert")
    @XmlSchemaType(name = "string")
    protected GlobalMatchType alert;
    @XmlElementRef(name = "Country", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> country;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setName(JAXBElement<String> value) {
        this.name = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDescription(JAXBElement<String> value) {
        this.description = value;
    }

    /**
     * Gets the value of the comment property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalItemCheckResultCodeType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalItemCheckResultCodeType> getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalItemCheckResultCodeType }{@code >}
     *     
     */
    public void setComment(JAXBElement<ArrayOfGlobalItemCheckResultCodeType> value) {
        this.comment = value;
    }

    /**
     * Gets the value of the match property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalItemCheckResultCodeType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalItemCheckResultCodeType> getMatch() {
        return match;
    }

    /**
     * Sets the value of the match property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalItemCheckResultCodeType }{@code >}
     *     
     */
    public void setMatch(JAXBElement<ArrayOfGlobalItemCheckResultCodeType> value) {
        this.match = value;
    }

    /**
     * Gets the value of the warning property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalItemCheckResultCodeType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalItemCheckResultCodeType> getWarning() {
        return warning;
    }

    /**
     * Sets the value of the warning property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalItemCheckResultCodeType }{@code >}
     *     
     */
    public void setWarning(JAXBElement<ArrayOfGlobalItemCheckResultCodeType> value) {
        this.warning = value;
    }

    /**
     * Gets the value of the mismatch property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalItemCheckResultCodeType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalItemCheckResultCodeType> getMismatch() {
        return mismatch;
    }

    /**
     * Sets the value of the mismatch property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalItemCheckResultCodeType }{@code >}
     *     
     */
    public void setMismatch(JAXBElement<ArrayOfGlobalItemCheckResultCodeType> value) {
        this.mismatch = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setID(Integer value) {
        this.id = value;
    }

    /**
     * Gets the value of the pass property.
     * 
     * @return
     *     possible object is
     *     {@link GlobalMatchType }
     *     
     */
    public GlobalMatchType getPass() {
        return pass;
    }

    /**
     * Sets the value of the pass property.
     * 
     * @param value
     *     allowed object is
     *     {@link GlobalMatchType }
     *     
     */
    public void setPass(GlobalMatchType value) {
        this.pass = value;
    }

    /**
     * Gets the value of the address property.
     * 
     * @return
     *     possible object is
     *     {@link GlobalMatchType }
     *     
     */
    public GlobalMatchType getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link GlobalMatchType }
     *     
     */
    public void setAddress(GlobalMatchType value) {
        this.address = value;
    }

    /**
     * Gets the value of the forename property.
     * 
     * @return
     *     possible object is
     *     {@link GlobalMatchType }
     *     
     */
    public GlobalMatchType getForename() {
        return forename;
    }

    /**
     * Sets the value of the forename property.
     * 
     * @param value
     *     allowed object is
     *     {@link GlobalMatchType }
     *     
     */
    public void setForename(GlobalMatchType value) {
        this.forename = value;
    }

    /**
     * Gets the value of the surname property.
     * 
     * @return
     *     possible object is
     *     {@link GlobalMatchType }
     *     
     */
    public GlobalMatchType getSurname() {
        return surname;
    }

    /**
     * Sets the value of the surname property.
     * 
     * @param value
     *     allowed object is
     *     {@link GlobalMatchType }
     *     
     */
    public void setSurname(GlobalMatchType value) {
        this.surname = value;
    }

    /**
     * Gets the value of the dob property.
     * 
     * @return
     *     possible object is
     *     {@link GlobalMatchType }
     *     
     */
    public GlobalMatchType getDOB() {
        return dob;
    }

    /**
     * Sets the value of the dob property.
     * 
     * @param value
     *     allowed object is
     *     {@link GlobalMatchType }
     *     
     */
    public void setDOB(GlobalMatchType value) {
        this.dob = value;
    }

    /**
     * Gets the value of the alert property.
     * 
     * @return
     *     possible object is
     *     {@link GlobalMatchType }
     *     
     */
    public GlobalMatchType getAlert() {
        return alert;
    }

    /**
     * Sets the value of the alert property.
     * 
     * @param value
     *     allowed object is
     *     {@link GlobalMatchType }
     *     
     */
    public void setAlert(GlobalMatchType value) {
        this.alert = value;
    }

    /**
     * Gets the value of the country property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCountry() {
        return country;
    }

    /**
     * Sets the value of the country property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCountry(JAXBElement<String> value) {
        this.country = value;
    }

}
