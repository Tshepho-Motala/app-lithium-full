
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="OrgID" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/&gt;
 *         &lt;element name="CaseID" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/&gt;
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="State" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="AuthorType" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="Verdict" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="DisclaimerAccepted" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="ActionContext" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="CustomerReferences" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalCaseReference" minOccurs="0"/&gt;
 *         &lt;element name="Properties" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalKeyValuePairOfstringstring" minOccurs="0"/&gt;
 *         &lt;element name="Profiles" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalProfileIDVersion" minOccurs="0"/&gt;
 *         &lt;element name="DocumentCategorySubmissionTypes" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalDocumentCategorySubmissionType" minOccurs="0"/&gt;
 *         &lt;element name="Documents" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalCaseDocument" minOccurs="0"/&gt;
 *         &lt;element name="Consents" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalCaseConsent" minOccurs="0"/&gt;
 *         &lt;element name="InputData" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalInputData" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "orgID",
    "caseID",
    "name",
    "state",
    "authorType",
    "verdict",
    "disclaimerAccepted",
    "actionContext",
    "customerReferences",
    "properties",
    "profiles",
    "documentCategorySubmissionTypes",
    "documents",
    "consents",
    "inputData"
})
@XmlRootElement(name = "UpdateCase")
public class UpdateCaseElement {

    @XmlElement(name = "OrgID")
    protected String orgID;
    @XmlElement(name = "CaseID")
    protected String caseID;
    @XmlElementRef(name = "Name", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> name;
    @XmlElement(name = "State")
    protected Integer state;
    @XmlElement(name = "AuthorType")
    protected Integer authorType;
    @XmlElementRef(name = "Verdict", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> verdict;
    @XmlElementRef(name = "DisclaimerAccepted", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<Boolean> disclaimerAccepted;
    @XmlElement(name = "ActionContext")
    protected Integer actionContext;
    @XmlElementRef(name = "CustomerReferences", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalCaseReferenceType> customerReferences;
    @XmlElementRef(name = "Properties", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalKeyValuePairOfstringstringType> properties;
    @XmlElementRef(name = "Profiles", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalProfileIDVersionType> profiles;
    @XmlElementRef(name = "DocumentCategorySubmissionTypes", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalDocumentCategorySubmissionTypeType> documentCategorySubmissionTypes;
    @XmlElementRef(name = "Documents", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalCaseDocumentType> documents;
    @XmlElementRef(name = "Consents", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalCaseConsentType> consents;
    @XmlElementRef(name = "InputData", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalInputDataType> inputData;

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
     *     {@link Integer }
     *     
     */
    public Integer getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setState(Integer value) {
        this.state = value;
    }

    /**
     * Gets the value of the authorType property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAuthorType() {
        return authorType;
    }

    /**
     * Sets the value of the authorType property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAuthorType(Integer value) {
        this.authorType = value;
    }

    /**
     * Gets the value of the verdict property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getVerdict() {
        return verdict;
    }

    /**
     * Sets the value of the verdict property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setVerdict(JAXBElement<Integer> value) {
        this.verdict = value;
    }

    /**
     * Gets the value of the disclaimerAccepted property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getDisclaimerAccepted() {
        return disclaimerAccepted;
    }

    /**
     * Sets the value of the disclaimerAccepted property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setDisclaimerAccepted(JAXBElement<Boolean> value) {
        this.disclaimerAccepted = value;
    }

    /**
     * Gets the value of the actionContext property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getActionContext() {
        return actionContext;
    }

    /**
     * Sets the value of the actionContext property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setActionContext(Integer value) {
        this.actionContext = value;
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
     * Gets the value of the profiles property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalProfileIDVersionType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalProfileIDVersionType> getProfiles() {
        return profiles;
    }

    /**
     * Sets the value of the profiles property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalProfileIDVersionType }{@code >}
     *     
     */
    public void setProfiles(JAXBElement<ArrayOfGlobalProfileIDVersionType> value) {
        this.profiles = value;
    }

    /**
     * Gets the value of the documentCategorySubmissionTypes property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalDocumentCategorySubmissionTypeType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalDocumentCategorySubmissionTypeType> getDocumentCategorySubmissionTypes() {
        return documentCategorySubmissionTypes;
    }

    /**
     * Sets the value of the documentCategorySubmissionTypes property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalDocumentCategorySubmissionTypeType }{@code >}
     *     
     */
    public void setDocumentCategorySubmissionTypes(JAXBElement<ArrayOfGlobalDocumentCategorySubmissionTypeType> value) {
        this.documentCategorySubmissionTypes = value;
    }

    /**
     * Gets the value of the documents property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalCaseDocumentType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalCaseDocumentType> getDocuments() {
        return documents;
    }

    /**
     * Sets the value of the documents property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalCaseDocumentType }{@code >}
     *     
     */
    public void setDocuments(JAXBElement<ArrayOfGlobalCaseDocumentType> value) {
        this.documents = value;
    }

    /**
     * Gets the value of the consents property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalCaseConsentType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalCaseConsentType> getConsents() {
        return consents;
    }

    /**
     * Sets the value of the consents property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalCaseConsentType }{@code >}
     *     
     */
    public void setConsents(JAXBElement<ArrayOfGlobalCaseConsentType> value) {
        this.consents = value;
    }

    /**
     * Gets the value of the inputData property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalInputDataType }{@code >}
     *     
     */
    public JAXBElement<GlobalInputDataType> getInputData() {
        return inputData;
    }

    /**
     * Sets the value of the inputData property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalInputDataType }{@code >}
     *     
     */
    public void setInputData(JAXBElement<GlobalInputDataType> value) {
        this.inputData = value;
    }

}
