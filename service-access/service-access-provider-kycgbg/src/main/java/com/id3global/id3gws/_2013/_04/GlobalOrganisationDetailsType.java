
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
import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfstring;
import org.w3._2001.xmlschema.Adapter1;


/**
 * <p>Java class for GlobalOrganisationDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalOrganisationDetails"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.id3global.com/ID3gWS/2013/04}GlobalOrganisation"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ManagementContact" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalContact" minOccurs="0"/&gt;
 *         &lt;element name="TechnicalContact" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalContact" minOccurs="0"/&gt;
 *         &lt;element name="UserPolicy" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalPasswordPolicy" minOccurs="0"/&gt;
 *         &lt;element name="AdministratorPolicy" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalPasswordPolicy" minOccurs="0"/&gt;
 *         &lt;element name="SupportContact" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalSupportContact" minOccurs="0"/&gt;
 *         &lt;element name="ManagedBy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="UseEmailAsAccount" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="ContractStart" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="ContractEnd" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="SelfcarePasswordResets" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="ManagedCustomer" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="BINList" type="{http://schemas.microsoft.com/2003/10/Serialization/Arrays}ArrayOfstring" minOccurs="0"/&gt;
 *         &lt;element name="BINResponseText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="WelcomeEmailSent" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="DataRetentionDetails" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalDataRetentionDetails" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalOrganisationDetails", propOrder = {
    "managementContact",
    "technicalContact",
    "userPolicy",
    "administratorPolicy",
    "supportContact",
    "managedBy",
    "useEmailAsAccount",
    "contractStart",
    "contractEnd",
    "selfcarePasswordResets",
    "managedCustomer",
    "binList",
    "binResponseText",
    "welcomeEmailSent",
    "dataRetentionDetails"
})
public class GlobalOrganisationDetailsType
    extends GlobalOrganisationType
{

    @XmlElementRef(name = "ManagementContact", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalContactType> managementContact;
    @XmlElementRef(name = "TechnicalContact", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalContactType> technicalContact;
    @XmlElementRef(name = "UserPolicy", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalPasswordPolicyType> userPolicy;
    @XmlElementRef(name = "AdministratorPolicy", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalPasswordPolicyType> administratorPolicy;
    @XmlElementRef(name = "SupportContact", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalSupportContactType> supportContact;
    @XmlElementRef(name = "ManagedBy", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> managedBy;
    @XmlElement(name = "UseEmailAsAccount")
    protected Boolean useEmailAsAccount;
    @XmlElement(name = "ContractStart", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date contractStart;
    @XmlElementRef(name = "ContractEnd", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<Date> contractEnd;
    @XmlElement(name = "SelfcarePasswordResets")
    protected Boolean selfcarePasswordResets;
    @XmlElement(name = "ManagedCustomer")
    protected Boolean managedCustomer;
    @XmlElementRef(name = "BINList", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfstring> binList;
    @XmlElementRef(name = "BINResponseText", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> binResponseText;
    @XmlElementRef(name = "WelcomeEmailSent", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<Date> welcomeEmailSent;
    @XmlElementRef(name = "DataRetentionDetails", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalDataRetentionDetailsType> dataRetentionDetails;

    /**
     * Gets the value of the managementContact property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalContactType }{@code >}
     *     
     */
    public JAXBElement<GlobalContactType> getManagementContact() {
        return managementContact;
    }

    /**
     * Sets the value of the managementContact property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalContactType }{@code >}
     *     
     */
    public void setManagementContact(JAXBElement<GlobalContactType> value) {
        this.managementContact = value;
    }

    /**
     * Gets the value of the technicalContact property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalContactType }{@code >}
     *     
     */
    public JAXBElement<GlobalContactType> getTechnicalContact() {
        return technicalContact;
    }

    /**
     * Sets the value of the technicalContact property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalContactType }{@code >}
     *     
     */
    public void setTechnicalContact(JAXBElement<GlobalContactType> value) {
        this.technicalContact = value;
    }

    /**
     * Gets the value of the userPolicy property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalPasswordPolicyType }{@code >}
     *     
     */
    public JAXBElement<GlobalPasswordPolicyType> getUserPolicy() {
        return userPolicy;
    }

    /**
     * Sets the value of the userPolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalPasswordPolicyType }{@code >}
     *     
     */
    public void setUserPolicy(JAXBElement<GlobalPasswordPolicyType> value) {
        this.userPolicy = value;
    }

    /**
     * Gets the value of the administratorPolicy property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalPasswordPolicyType }{@code >}
     *     
     */
    public JAXBElement<GlobalPasswordPolicyType> getAdministratorPolicy() {
        return administratorPolicy;
    }

    /**
     * Sets the value of the administratorPolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalPasswordPolicyType }{@code >}
     *     
     */
    public void setAdministratorPolicy(JAXBElement<GlobalPasswordPolicyType> value) {
        this.administratorPolicy = value;
    }

    /**
     * Gets the value of the supportContact property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalSupportContactType }{@code >}
     *     
     */
    public JAXBElement<GlobalSupportContactType> getSupportContact() {
        return supportContact;
    }

    /**
     * Sets the value of the supportContact property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalSupportContactType }{@code >}
     *     
     */
    public void setSupportContact(JAXBElement<GlobalSupportContactType> value) {
        this.supportContact = value;
    }

    /**
     * Gets the value of the managedBy property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getManagedBy() {
        return managedBy;
    }

    /**
     * Sets the value of the managedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setManagedBy(JAXBElement<String> value) {
        this.managedBy = value;
    }

    /**
     * Gets the value of the useEmailAsAccount property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUseEmailAsAccount() {
        return useEmailAsAccount;
    }

    /**
     * Sets the value of the useEmailAsAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUseEmailAsAccount(Boolean value) {
        this.useEmailAsAccount = value;
    }

    /**
     * Gets the value of the contractStart property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getContractStart() {
        return contractStart;
    }

    /**
     * Sets the value of the contractStart property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContractStart(Date value) {
        this.contractStart = value;
    }

    /**
     * Gets the value of the contractEnd property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Date }{@code >}
     *     
     */
    public JAXBElement<Date> getContractEnd() {
        return contractEnd;
    }

    /**
     * Sets the value of the contractEnd property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Date }{@code >}
     *     
     */
    public void setContractEnd(JAXBElement<Date> value) {
        this.contractEnd = value;
    }

    /**
     * Gets the value of the selfcarePasswordResets property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSelfcarePasswordResets() {
        return selfcarePasswordResets;
    }

    /**
     * Sets the value of the selfcarePasswordResets property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSelfcarePasswordResets(Boolean value) {
        this.selfcarePasswordResets = value;
    }

    /**
     * Gets the value of the managedCustomer property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isManagedCustomer() {
        return managedCustomer;
    }

    /**
     * Sets the value of the managedCustomer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setManagedCustomer(Boolean value) {
        this.managedCustomer = value;
    }

    /**
     * Gets the value of the binList property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}
     *     
     */
    public JAXBElement<ArrayOfstring> getBINList() {
        return binList;
    }

    /**
     * Sets the value of the binList property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}
     *     
     */
    public void setBINList(JAXBElement<ArrayOfstring> value) {
        this.binList = value;
    }

    /**
     * Gets the value of the binResponseText property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getBINResponseText() {
        return binResponseText;
    }

    /**
     * Sets the value of the binResponseText property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setBINResponseText(JAXBElement<String> value) {
        this.binResponseText = value;
    }

    /**
     * Gets the value of the welcomeEmailSent property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Date }{@code >}
     *     
     */
    public JAXBElement<Date> getWelcomeEmailSent() {
        return welcomeEmailSent;
    }

    /**
     * Sets the value of the welcomeEmailSent property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Date }{@code >}
     *     
     */
    public void setWelcomeEmailSent(JAXBElement<Date> value) {
        this.welcomeEmailSent = value;
    }

    /**
     * Gets the value of the dataRetentionDetails property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalDataRetentionDetailsType }{@code >}
     *     
     */
    public JAXBElement<GlobalDataRetentionDetailsType> getDataRetentionDetails() {
        return dataRetentionDetails;
    }

    /**
     * Sets the value of the dataRetentionDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalDataRetentionDetailsType }{@code >}
     *     
     */
    public void setDataRetentionDetails(JAXBElement<GlobalDataRetentionDetailsType> value) {
        this.dataRetentionDetails = value;
    }

}
