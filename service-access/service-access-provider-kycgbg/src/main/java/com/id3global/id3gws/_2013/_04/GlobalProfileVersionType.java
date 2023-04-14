
package com.id3global.id3gws._2013._04;

import java.util.Date;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter1;


/**
 * <p>Java class for GlobalProfileVersion complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalProfileVersion"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.id3global.com/ID3gWS/2013/04}GlobalProfile"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Version" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="Revision" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="State" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalProfileState" minOccurs="0"/&gt;
 *         &lt;element name="Start" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="End" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="AddressFormat" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalAddressFormat" minOccurs="0"/&gt;
 *         &lt;element name="ReAuthenticationLifetime" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="AddressLookupEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="SessionBlocking" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="DisableInputStorage" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="RevisionNote" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Country" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="VersionCreated" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="VersionUpdated" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="VersionUpdatedBy" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/&gt;
 *         &lt;element name="DocumentImageValidation" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="DocumentImageStorage" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="VersionUpdatedByAccount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="IsURU" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="HasVelocity" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalProfileVersion", propOrder = {
    "version",
    "revision",
    "state",
    "start",
    "end",
    "addressFormat",
    "reAuthenticationLifetime",
    "addressLookupEnabled",
    "sessionBlocking",
    "disableInputStorage",
    "revisionNote",
    "country",
    "versionCreated",
    "versionUpdated",
    "versionUpdatedBy",
    "documentImageValidation",
    "documentImageStorage",
    "versionUpdatedByAccount",
    "isURU",
    "hasVelocity"
})
@XmlSeeAlso({
    GlobalProfileDetailsType.class
})
public class GlobalProfileVersionType
    extends GlobalProfileType
{

    @XmlElement(name = "Version")
    @XmlSchemaType(name = "unsignedInt")
    protected Long version;
    @XmlElement(name = "Revision")
    @XmlSchemaType(name = "unsignedInt")
    protected Long revision;
    @XmlElement(name = "State")
    @XmlSchemaType(name = "string")
    protected GlobalProfileStateType state;
    @XmlElementRef(name = "Start", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<Date> start;
    @XmlElementRef(name = "End", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<Date> end;
    @XmlElement(name = "AddressFormat")
    @XmlSchemaType(name = "string")
    protected GlobalAddressFormatType addressFormat;
    @XmlElementRef(name = "ReAuthenticationLifetime", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<Long> reAuthenticationLifetime;
    @XmlElement(name = "AddressLookupEnabled")
    protected Boolean addressLookupEnabled;
    @XmlElement(name = "SessionBlocking")
    protected Boolean sessionBlocking;
    @XmlElement(name = "DisableInputStorage")
    protected Boolean disableInputStorage;
    @XmlElementRef(name = "RevisionNote", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> revisionNote;
    @XmlElementRef(name = "Country", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> country;
    @XmlElement(name = "VersionCreated", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date versionCreated;
    @XmlElement(name = "VersionUpdated", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date versionUpdated;
    @XmlElement(name = "VersionUpdatedBy")
    protected String versionUpdatedBy;
    @XmlElement(name = "DocumentImageValidation")
    protected Boolean documentImageValidation;
    @XmlElement(name = "DocumentImageStorage")
    protected Boolean documentImageStorage;
    @XmlElementRef(name = "VersionUpdatedByAccount", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> versionUpdatedByAccount;
    @XmlElementRef(name = "IsURU", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> isURU;
    @XmlElement(name = "HasVelocity")
    protected Boolean hasVelocity;

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setVersion(Long value) {
        this.version = value;
    }

    /**
     * Gets the value of the revision property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getRevision() {
        return revision;
    }

    /**
     * Sets the value of the revision property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setRevision(Long value) {
        this.revision = value;
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link GlobalProfileStateType }
     *     
     */
    public GlobalProfileStateType getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link GlobalProfileStateType }
     *     
     */
    public void setState(GlobalProfileStateType value) {
        this.state = value;
    }

    /**
     * Gets the value of the start property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Date }{@code >}
     *     
     */
    public JAXBElement<Date> getStart() {
        return start;
    }

    /**
     * Sets the value of the start property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Date }{@code >}
     *     
     */
    public void setStart(JAXBElement<Date> value) {
        this.start = value;
    }

    /**
     * Gets the value of the end property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Date }{@code >}
     *     
     */
    public JAXBElement<Date> getEnd() {
        return end;
    }

    /**
     * Sets the value of the end property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Date }{@code >}
     *     
     */
    public void setEnd(JAXBElement<Date> value) {
        this.end = value;
    }

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
     * Gets the value of the reAuthenticationLifetime property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public JAXBElement<Long> getReAuthenticationLifetime() {
        return reAuthenticationLifetime;
    }

    /**
     * Sets the value of the reAuthenticationLifetime property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public void setReAuthenticationLifetime(JAXBElement<Long> value) {
        this.reAuthenticationLifetime = value;
    }

    /**
     * Gets the value of the addressLookupEnabled property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAddressLookupEnabled() {
        return addressLookupEnabled;
    }

    /**
     * Sets the value of the addressLookupEnabled property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAddressLookupEnabled(Boolean value) {
        this.addressLookupEnabled = value;
    }

    /**
     * Gets the value of the sessionBlocking property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSessionBlocking() {
        return sessionBlocking;
    }

    /**
     * Sets the value of the sessionBlocking property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSessionBlocking(Boolean value) {
        this.sessionBlocking = value;
    }

    /**
     * Gets the value of the disableInputStorage property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDisableInputStorage() {
        return disableInputStorage;
    }

    /**
     * Sets the value of the disableInputStorage property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDisableInputStorage(Boolean value) {
        this.disableInputStorage = value;
    }

    /**
     * Gets the value of the revisionNote property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRevisionNote() {
        return revisionNote;
    }

    /**
     * Sets the value of the revisionNote property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRevisionNote(JAXBElement<String> value) {
        this.revisionNote = value;
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

    /**
     * Gets the value of the versionCreated property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getVersionCreated() {
        return versionCreated;
    }

    /**
     * Sets the value of the versionCreated property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersionCreated(Date value) {
        this.versionCreated = value;
    }

    /**
     * Gets the value of the versionUpdated property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getVersionUpdated() {
        return versionUpdated;
    }

    /**
     * Sets the value of the versionUpdated property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersionUpdated(Date value) {
        this.versionUpdated = value;
    }

    /**
     * Gets the value of the versionUpdatedBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersionUpdatedBy() {
        return versionUpdatedBy;
    }

    /**
     * Sets the value of the versionUpdatedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersionUpdatedBy(String value) {
        this.versionUpdatedBy = value;
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
     * Gets the value of the versionUpdatedByAccount property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getVersionUpdatedByAccount() {
        return versionUpdatedByAccount;
    }

    /**
     * Sets the value of the versionUpdatedByAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setVersionUpdatedByAccount(JAXBElement<String> value) {
        this.versionUpdatedByAccount = value;
    }

    /**
     * Gets the value of the isURU property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getIsURU() {
        return isURU;
    }

    /**
     * Sets the value of the isURU property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setIsURU(JAXBElement<String> value) {
        this.isURU = value;
    }

    /**
     * Gets the value of the hasVelocity property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isHasVelocity() {
        return hasVelocity;
    }

    /**
     * Sets the value of the hasVelocity property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHasVelocity(Boolean value) {
        this.hasVelocity = value;
    }

}
