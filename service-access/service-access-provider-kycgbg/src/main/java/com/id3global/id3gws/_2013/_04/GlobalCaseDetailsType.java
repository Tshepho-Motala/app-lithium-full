
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalCaseDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalCaseDetails"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.id3global.com/ID3gWS/2013/04}GlobalCase"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DispatchResult" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalDispatchResult" minOccurs="0"/&gt;
 *         &lt;element name="Reports" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalCaseReport" minOccurs="0"/&gt;
 *         &lt;element name="DocumentCategorySubmissionTypes" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalDocumentCategorySubmissionType" minOccurs="0"/&gt;
 *         &lt;element name="Documents" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalCaseDocument" minOccurs="0"/&gt;
 *         &lt;element name="Consents" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalCaseConsent" minOccurs="0"/&gt;
 *         &lt;element name="DispatchRecord" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalCaseDispatchRecord" minOccurs="0"/&gt;
 *         &lt;element name="DisclaimerAccepted" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="UpdatedBy" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/&gt;
 *         &lt;element name="UpdatedByAccount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalCaseDetails", propOrder = {
    "dispatchResult",
    "reports",
    "documentCategorySubmissionTypes",
    "documents",
    "consents",
    "dispatchRecord",
    "disclaimerAccepted",
    "updatedBy",
    "updatedByAccount"
})
public class GlobalCaseDetailsType
    extends GlobalCaseType
{

    @XmlElementRef(name = "DispatchResult", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalDispatchResultType> dispatchResult;
    @XmlElementRef(name = "Reports", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalCaseReportType> reports;
    @XmlElementRef(name = "DocumentCategorySubmissionTypes", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalDocumentCategorySubmissionTypeType> documentCategorySubmissionTypes;
    @XmlElementRef(name = "Documents", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalCaseDocumentType> documents;
    @XmlElementRef(name = "Consents", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalCaseConsentType> consents;
    @XmlElementRef(name = "DispatchRecord", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalCaseDispatchRecordType> dispatchRecord;
    @XmlElement(name = "DisclaimerAccepted")
    protected Boolean disclaimerAccepted;
    @XmlElement(name = "UpdatedBy")
    protected String updatedBy;
    @XmlElementRef(name = "UpdatedByAccount", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> updatedByAccount;

    /**
     * Gets the value of the dispatchResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalDispatchResultType }{@code >}
     *     
     */
    public JAXBElement<GlobalDispatchResultType> getDispatchResult() {
        return dispatchResult;
    }

    /**
     * Sets the value of the dispatchResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalDispatchResultType }{@code >}
     *     
     */
    public void setDispatchResult(JAXBElement<GlobalDispatchResultType> value) {
        this.dispatchResult = value;
    }

    /**
     * Gets the value of the reports property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalCaseReportType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalCaseReportType> getReports() {
        return reports;
    }

    /**
     * Sets the value of the reports property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalCaseReportType }{@code >}
     *     
     */
    public void setReports(JAXBElement<ArrayOfGlobalCaseReportType> value) {
        this.reports = value;
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
     * Gets the value of the dispatchRecord property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalCaseDispatchRecordType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalCaseDispatchRecordType> getDispatchRecord() {
        return dispatchRecord;
    }

    /**
     * Sets the value of the dispatchRecord property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalCaseDispatchRecordType }{@code >}
     *     
     */
    public void setDispatchRecord(JAXBElement<ArrayOfGlobalCaseDispatchRecordType> value) {
        this.dispatchRecord = value;
    }

    /**
     * Gets the value of the disclaimerAccepted property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDisclaimerAccepted() {
        return disclaimerAccepted;
    }

    /**
     * Sets the value of the disclaimerAccepted property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDisclaimerAccepted(Boolean value) {
        this.disclaimerAccepted = value;
    }

    /**
     * Gets the value of the updatedBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Sets the value of the updatedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpdatedBy(String value) {
        this.updatedBy = value;
    }

    /**
     * Gets the value of the updatedByAccount property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getUpdatedByAccount() {
        return updatedByAccount;
    }

    /**
     * Sets the value of the updatedByAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setUpdatedByAccount(JAXBElement<String> value) {
        this.updatedByAccount = value;
    }

}
