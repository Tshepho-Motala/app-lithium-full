
package com.id3global.id3gws._2013._04;

import java.util.Date;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter1;


/**
 * <p>Java class for GlobalCaseDocumentReport complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalCaseDocumentReport"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Profile" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalProfileIDVersion" minOccurs="0"/&gt;
 *         &lt;element name="ValidationID" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/&gt;
 *         &lt;element name="AuthenticationID" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/&gt;
 *         &lt;element name="Result" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalMatrixResult" minOccurs="0"/&gt;
 *         &lt;element name="DocumentType" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalDocumentType" minOccurs="0"/&gt;
 *         &lt;element name="LastChecked" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="Status" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalDIVData.GlobalDocumentExtractedStatus" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalCaseDocumentReport", propOrder = {
    "profile",
    "validationID",
    "authenticationID",
    "result",
    "documentType",
    "lastChecked",
    "status"
})
public class GlobalCaseDocumentReportType {

    @XmlElementRef(name = "Profile", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalProfileIDVersionType> profile;
    @XmlElement(name = "ValidationID")
    protected String validationID;
    @XmlElementRef(name = "AuthenticationID", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> authenticationID;
    @XmlElementRef(name = "Result", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalMatrixResultType> result;
    @XmlElement(name = "DocumentType")
    @XmlSchemaType(name = "string")
    protected GlobalDocumentTypeType documentType;
    @XmlElement(name = "LastChecked", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date lastChecked;
    @XmlElement(name = "Status")
    @XmlSchemaType(name = "string")
    protected GlobalDIVDataGlobalDocumentExtractedStatusType status;

    /**
     * Gets the value of the profile property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalProfileIDVersionType }{@code >}
     *     
     */
    public JAXBElement<GlobalProfileIDVersionType> getProfile() {
        return profile;
    }

    /**
     * Sets the value of the profile property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalProfileIDVersionType }{@code >}
     *     
     */
    public void setProfile(JAXBElement<GlobalProfileIDVersionType> value) {
        this.profile = value;
    }

    /**
     * Gets the value of the validationID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValidationID() {
        return validationID;
    }

    /**
     * Sets the value of the validationID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValidationID(String value) {
        this.validationID = value;
    }

    /**
     * Gets the value of the authenticationID property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAuthenticationID() {
        return authenticationID;
    }

    /**
     * Sets the value of the authenticationID property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAuthenticationID(JAXBElement<String> value) {
        this.authenticationID = value;
    }

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalMatrixResultType }{@code >}
     *     
     */
    public JAXBElement<GlobalMatrixResultType> getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalMatrixResultType }{@code >}
     *     
     */
    public void setResult(JAXBElement<GlobalMatrixResultType> value) {
        this.result = value;
    }

    /**
     * Gets the value of the documentType property.
     * 
     * @return
     *     possible object is
     *     {@link GlobalDocumentTypeType }
     *     
     */
    public GlobalDocumentTypeType getDocumentType() {
        return documentType;
    }

    /**
     * Sets the value of the documentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link GlobalDocumentTypeType }
     *     
     */
    public void setDocumentType(GlobalDocumentTypeType value) {
        this.documentType = value;
    }

    /**
     * Gets the value of the lastChecked property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getLastChecked() {
        return lastChecked;
    }

    /**
     * Sets the value of the lastChecked property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastChecked(Date value) {
        this.lastChecked = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link GlobalDIVDataGlobalDocumentExtractedStatusType }
     *     
     */
    public GlobalDIVDataGlobalDocumentExtractedStatusType getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link GlobalDIVDataGlobalDocumentExtractedStatusType }
     *     
     */
    public void setStatus(GlobalDIVDataGlobalDocumentExtractedStatusType value) {
        this.status = value;
    }

}
