
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
 * <p>Java class for GlobalMatrixResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalMatrixResult"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AuthenticationId" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/&gt;
 *         &lt;element name="ProfileId" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/&gt;
 *         &lt;element name="Overall" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalMatrixResultItem" minOccurs="0"/&gt;
 *         &lt;element name="Summary" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalMatrixResultItem" minOccurs="0"/&gt;
 *         &lt;element name="OverallFields" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalMatrixResultField" minOccurs="0"/&gt;
 *         &lt;element name="CellGroups" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalMatrixCellGroup" minOccurs="0"/&gt;
 *         &lt;element name="SummaryTemplate" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalMatrixSummaryTemplateType" minOccurs="0"/&gt;
 *         &lt;element name="Pending" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="ExternalDataIds" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalKeyValuePairOfstringint" minOccurs="0"/&gt;
 *         &lt;element name="Date" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalMatrixResult", propOrder = {
    "authenticationId",
    "profileId",
    "overall",
    "summary",
    "overallFields",
    "cellGroups",
    "summaryTemplate",
    "pending",
    "externalDataIds",
    "date"
})
public class GlobalMatrixResultType {

    @XmlElement(name = "AuthenticationId")
    protected String authenticationId;
    @XmlElement(name = "ProfileId")
    protected String profileId;
    @XmlElementRef(name = "Overall", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalMatrixResultItemType> overall;
    @XmlElementRef(name = "Summary", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalMatrixResultItemType> summary;
    @XmlElementRef(name = "OverallFields", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalMatrixResultFieldType> overallFields;
    @XmlElementRef(name = "CellGroups", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalMatrixCellGroupType> cellGroups;
    @XmlElement(name = "SummaryTemplate")
    @XmlSchemaType(name = "string")
    protected GlobalMatrixSummaryTemplateTypeType summaryTemplate;
    @XmlElement(name = "Pending")
    protected Boolean pending;
    @XmlElementRef(name = "ExternalDataIds", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalKeyValuePairOfstringintType> externalDataIds;
    @XmlElement(name = "Date", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date date;

    /**
     * Gets the value of the authenticationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthenticationId() {
        return authenticationId;
    }

    /**
     * Sets the value of the authenticationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthenticationId(String value) {
        this.authenticationId = value;
    }

    /**
     * Gets the value of the profileId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProfileId() {
        return profileId;
    }

    /**
     * Sets the value of the profileId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProfileId(String value) {
        this.profileId = value;
    }

    /**
     * Gets the value of the overall property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalMatrixResultItemType }{@code >}
     *     
     */
    public JAXBElement<GlobalMatrixResultItemType> getOverall() {
        return overall;
    }

    /**
     * Sets the value of the overall property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalMatrixResultItemType }{@code >}
     *     
     */
    public void setOverall(JAXBElement<GlobalMatrixResultItemType> value) {
        this.overall = value;
    }

    /**
     * Gets the value of the summary property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalMatrixResultItemType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalMatrixResultItemType> getSummary() {
        return summary;
    }

    /**
     * Sets the value of the summary property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalMatrixResultItemType }{@code >}
     *     
     */
    public void setSummary(JAXBElement<ArrayOfGlobalMatrixResultItemType> value) {
        this.summary = value;
    }

    /**
     * Gets the value of the overallFields property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalMatrixResultFieldType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalMatrixResultFieldType> getOverallFields() {
        return overallFields;
    }

    /**
     * Sets the value of the overallFields property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalMatrixResultFieldType }{@code >}
     *     
     */
    public void setOverallFields(JAXBElement<ArrayOfGlobalMatrixResultFieldType> value) {
        this.overallFields = value;
    }

    /**
     * Gets the value of the cellGroups property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalMatrixCellGroupType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalMatrixCellGroupType> getCellGroups() {
        return cellGroups;
    }

    /**
     * Sets the value of the cellGroups property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalMatrixCellGroupType }{@code >}
     *     
     */
    public void setCellGroups(JAXBElement<ArrayOfGlobalMatrixCellGroupType> value) {
        this.cellGroups = value;
    }

    /**
     * Gets the value of the summaryTemplate property.
     * 
     * @return
     *     possible object is
     *     {@link GlobalMatrixSummaryTemplateTypeType }
     *     
     */
    public GlobalMatrixSummaryTemplateTypeType getSummaryTemplate() {
        return summaryTemplate;
    }

    /**
     * Sets the value of the summaryTemplate property.
     * 
     * @param value
     *     allowed object is
     *     {@link GlobalMatrixSummaryTemplateTypeType }
     *     
     */
    public void setSummaryTemplate(GlobalMatrixSummaryTemplateTypeType value) {
        this.summaryTemplate = value;
    }

    /**
     * Gets the value of the pending property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPending() {
        return pending;
    }

    /**
     * Sets the value of the pending property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPending(Boolean value) {
        this.pending = value;
    }

    /**
     * Gets the value of the externalDataIds property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalKeyValuePairOfstringintType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalKeyValuePairOfstringintType> getExternalDataIds() {
        return externalDataIds;
    }

    /**
     * Sets the value of the externalDataIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalKeyValuePairOfstringintType }{@code >}
     *     
     */
    public void setExternalDataIds(JAXBElement<ArrayOfGlobalKeyValuePairOfstringintType> value) {
        this.externalDataIds = value;
    }

    /**
     * Gets the value of the date property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the value of the date property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDate(Date value) {
        this.date = value;
    }

}
