
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
 * <p>Java class for GlobalCase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalCase"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CaseID" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/&gt;
 *         &lt;element name="OrgID" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/&gt;
 *         &lt;element name="LinkedAccountID" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/&gt;
 *         &lt;element name="Reference" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="State" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="OrganisationName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Created" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="Updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="Verdicts" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalCaseVerdict" minOccurs="0"/&gt;
 *         &lt;element name="AuthorType" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="ChargingPoints" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalCaseChargingPoint" minOccurs="0"/&gt;
 *         &lt;element name="CreatedOrganisationID" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/&gt;
 *         &lt;element name="Properties" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalKeyValuePairOfstringstring" minOccurs="0"/&gt;
 *         &lt;element name="CustomerReferences" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalCaseReference" minOccurs="0"/&gt;
 *         &lt;element name="IsMultipleDocument" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="DocumentCount" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalCase", propOrder = {
    "caseID",
    "orgID",
    "linkedAccountID",
    "reference",
    "name",
    "state",
    "organisationName",
    "created",
    "updated",
    "verdicts",
    "authorType",
    "chargingPoints",
    "createdOrganisationID",
    "properties",
    "customerReferences",
    "isMultipleDocument",
    "documentCount"
})
@XmlSeeAlso({
    GlobalCaseDetailsType.class
})
public class GlobalCaseType {

    @XmlElement(name = "CaseID")
    protected String caseID;
    @XmlElement(name = "OrgID")
    protected String orgID;
    @XmlElementRef(name = "LinkedAccountID", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> linkedAccountID;
    @XmlElementRef(name = "Reference", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> reference;
    @XmlElementRef(name = "Name", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> name;
    @XmlElement(name = "State")
    @XmlSchemaType(name = "unsignedInt")
    protected Long state;
    @XmlElementRef(name = "OrganisationName", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> organisationName;
    @XmlElement(name = "Created", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date created;
    @XmlElement(name = "Updated", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date updated;
    @XmlElementRef(name = "Verdicts", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalCaseVerdictType> verdicts;
    @XmlElement(name = "AuthorType")
    @XmlSchemaType(name = "unsignedInt")
    protected Long authorType;
    @XmlElementRef(name = "ChargingPoints", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalCaseChargingPointType> chargingPoints;
    @XmlElement(name = "CreatedOrganisationID")
    protected String createdOrganisationID;
    @XmlElementRef(name = "Properties", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalKeyValuePairOfstringstringType> properties;
    @XmlElementRef(name = "CustomerReferences", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalCaseReferenceType> customerReferences;
    @XmlElement(name = "IsMultipleDocument")
    protected Boolean isMultipleDocument;
    @XmlElement(name = "DocumentCount")
    protected Integer documentCount;

    /**
     * Gets the value of the caseID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCaseID() {
        return caseID;
    }

    /**
     * Sets the value of the caseID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCaseID(String value) {
        this.caseID = value;
    }

    /**
     * Gets the value of the orgID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgID() {
        return orgID;
    }

    /**
     * Sets the value of the orgID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgID(String value) {
        this.orgID = value;
    }

    /**
     * Gets the value of the linkedAccountID property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLinkedAccountID() {
        return linkedAccountID;
    }

    /**
     * Sets the value of the linkedAccountID property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLinkedAccountID(JAXBElement<String> value) {
        this.linkedAccountID = value;
    }

    /**
     * Gets the value of the reference property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getReference() {
        return reference;
    }

    /**
     * Sets the value of the reference property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setReference(JAXBElement<String> value) {
        this.reference = value;
    }

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
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setState(Long value) {
        this.state = value;
    }

    /**
     * Gets the value of the organisationName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getOrganisationName() {
        return organisationName;
    }

    /**
     * Sets the value of the organisationName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setOrganisationName(JAXBElement<String> value) {
        this.organisationName = value;
    }

    /**
     * Gets the value of the created property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getCreated() {
        return created;
    }

    /**
     * Sets the value of the created property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreated(Date value) {
        this.created = value;
    }

    /**
     * Gets the value of the updated property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getUpdated() {
        return updated;
    }

    /**
     * Sets the value of the updated property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpdated(Date value) {
        this.updated = value;
    }

    /**
     * Gets the value of the verdicts property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalCaseVerdictType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalCaseVerdictType> getVerdicts() {
        return verdicts;
    }

    /**
     * Sets the value of the verdicts property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalCaseVerdictType }{@code >}
     *     
     */
    public void setVerdicts(JAXBElement<ArrayOfGlobalCaseVerdictType> value) {
        this.verdicts = value;
    }

    /**
     * Gets the value of the authorType property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAuthorType() {
        return authorType;
    }

    /**
     * Sets the value of the authorType property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAuthorType(Long value) {
        this.authorType = value;
    }

    /**
     * Gets the value of the chargingPoints property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalCaseChargingPointType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalCaseChargingPointType> getChargingPoints() {
        return chargingPoints;
    }

    /**
     * Sets the value of the chargingPoints property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalCaseChargingPointType }{@code >}
     *     
     */
    public void setChargingPoints(JAXBElement<ArrayOfGlobalCaseChargingPointType> value) {
        this.chargingPoints = value;
    }

    /**
     * Gets the value of the createdOrganisationID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreatedOrganisationID() {
        return createdOrganisationID;
    }

    /**
     * Sets the value of the createdOrganisationID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreatedOrganisationID(String value) {
        this.createdOrganisationID = value;
    }

    /**
     * Gets the value of the properties property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalKeyValuePairOfstringstringType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalKeyValuePairOfstringstringType> getProperties() {
        return properties;
    }

    /**
     * Sets the value of the properties property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalKeyValuePairOfstringstringType }{@code >}
     *     
     */
    public void setProperties(JAXBElement<ArrayOfGlobalKeyValuePairOfstringstringType> value) {
        this.properties = value;
    }

    /**
     * Gets the value of the customerReferences property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalCaseReferenceType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalCaseReferenceType> getCustomerReferences() {
        return customerReferences;
    }

    /**
     * Sets the value of the customerReferences property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalCaseReferenceType }{@code >}
     *     
     */
    public void setCustomerReferences(JAXBElement<ArrayOfGlobalCaseReferenceType> value) {
        this.customerReferences = value;
    }

    /**
     * Gets the value of the isMultipleDocument property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsMultipleDocument() {
        return isMultipleDocument;
    }

    /**
     * Sets the value of the isMultipleDocument property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsMultipleDocument(Boolean value) {
        this.isMultipleDocument = value;
    }

    /**
     * Gets the value of the documentCount property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDocumentCount() {
        return documentCount;
    }

    /**
     * Sets the value of the documentCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDocumentCount(Integer value) {
        this.documentCount = value;
    }

}
