
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
 * <p>Java class for GlobalSupplierAccount complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalSupplierAccount"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SupplierAccountID" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/&gt;
 *         &lt;element name="Created" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="Tested" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="Success" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="CanInherit" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="OwningOrg" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalSupplierAccountOrg" minOccurs="0"/&gt;
 *         &lt;element name="ParentOrg" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalSupplierAccountOrg" minOccurs="0"/&gt;
 *         &lt;element name="SharedByOrgs" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalSupplierAccountOrg" minOccurs="0"/&gt;
 *         &lt;element name="SupplierID" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/&gt;
 *         &lt;element name="PendingOrg" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalSupplierAccountOrg" minOccurs="0"/&gt;
 *         &lt;element name="Active" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="PasswordChange" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalSupplierAccount", propOrder = {
    "supplierAccountID",
    "created",
    "tested",
    "success",
    "status",
    "canInherit",
    "owningOrg",
    "parentOrg",
    "sharedByOrgs",
    "supplierID",
    "pendingOrg",
    "active",
    "passwordChange"
})
@XmlSeeAlso({
    GlobalGlobalDataConsortiumAccountType.class,
    GlobalAddressLookupAccountType.class,
    GlobalAustraliaAccountType.class,
    GlobalCanadaAccountType.class,
    GlobalCardPreAccountType.class,
    GlobalCreditHeaderAccountType.class,
    GlobalDeviceAccountType.class,
    GlobalFraudAccountType.class,
    GlobalGermanAccountType.class,
    GlobalGermanAccount2Type.class,
    GlobalIPVerificationAccountType.class,
    GlobalPaymentPredictorAccountType.class,
    GlobalSouthAfricaAccountType.class,
    GlobalBankingAccountType.class,
    GlobalStandardAccountType.class,
    GlobalCreditReportAccountType.class,
    GlobalSanctionsAccountType.class,
    GlobalLifestyleAccountType.class,
    GlobalHongKongAccountType.class,
    GlobalBelgiumAccountType.class,
    GlobalUnitedStatesAccountType.class,
    GlobalCIFASAccountType.class,
    GlobalMobileAccountType.class,
    GlobalMalaysiaAccountType.class,
    GlobalNetherlandsAccountType.class,
    GlobalTrustopiaAccountType.class,
    GlobalSouthAfricaCPBAccountType.class
})
public class GlobalSupplierAccountType {

    @XmlElementRef(name = "SupplierAccountID", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> supplierAccountID;
    @XmlElement(name = "Created", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date created;
    @XmlElement(name = "Tested", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date tested;
    @XmlElement(name = "Success")
    protected Boolean success;
    @XmlElementRef(name = "Status", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> status;
    @XmlElement(name = "CanInherit")
    protected Boolean canInherit;
    @XmlElementRef(name = "OwningOrg", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalSupplierAccountOrgType> owningOrg;
    @XmlElementRef(name = "ParentOrg", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalSupplierAccountOrgType> parentOrg;
    @XmlElementRef(name = "SharedByOrgs", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalSupplierAccountOrgType> sharedByOrgs;
    @XmlElement(name = "SupplierID")
    protected String supplierID;
    @XmlElementRef(name = "PendingOrg", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalSupplierAccountOrgType> pendingOrg;
    @XmlElement(name = "Active")
    protected Boolean active;
    @XmlElement(name = "PasswordChange", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date passwordChange;

    /**
     * Gets the value of the supplierAccountID property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSupplierAccountID() {
        return supplierAccountID;
    }

    /**
     * Sets the value of the supplierAccountID property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSupplierAccountID(JAXBElement<String> value) {
        this.supplierAccountID = value;
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
     * Gets the value of the tested property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getTested() {
        return tested;
    }

    /**
     * Sets the value of the tested property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTested(Date value) {
        this.tested = value;
    }

    /**
     * Gets the value of the success property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSuccess() {
        return success;
    }

    /**
     * Sets the value of the success property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSuccess(Boolean value) {
        this.success = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStatus(JAXBElement<String> value) {
        this.status = value;
    }

    /**
     * Gets the value of the canInherit property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCanInherit() {
        return canInherit;
    }

    /**
     * Sets the value of the canInherit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCanInherit(Boolean value) {
        this.canInherit = value;
    }

    /**
     * Gets the value of the owningOrg property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalSupplierAccountOrgType }{@code >}
     *     
     */
    public JAXBElement<GlobalSupplierAccountOrgType> getOwningOrg() {
        return owningOrg;
    }

    /**
     * Sets the value of the owningOrg property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalSupplierAccountOrgType }{@code >}
     *     
     */
    public void setOwningOrg(JAXBElement<GlobalSupplierAccountOrgType> value) {
        this.owningOrg = value;
    }

    /**
     * Gets the value of the parentOrg property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalSupplierAccountOrgType }{@code >}
     *     
     */
    public JAXBElement<GlobalSupplierAccountOrgType> getParentOrg() {
        return parentOrg;
    }

    /**
     * Sets the value of the parentOrg property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalSupplierAccountOrgType }{@code >}
     *     
     */
    public void setParentOrg(JAXBElement<GlobalSupplierAccountOrgType> value) {
        this.parentOrg = value;
    }

    /**
     * Gets the value of the sharedByOrgs property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalSupplierAccountOrgType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalSupplierAccountOrgType> getSharedByOrgs() {
        return sharedByOrgs;
    }

    /**
     * Sets the value of the sharedByOrgs property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalSupplierAccountOrgType }{@code >}
     *     
     */
    public void setSharedByOrgs(JAXBElement<ArrayOfGlobalSupplierAccountOrgType> value) {
        this.sharedByOrgs = value;
    }

    /**
     * Gets the value of the supplierID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSupplierID() {
        return supplierID;
    }

    /**
     * Sets the value of the supplierID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSupplierID(String value) {
        this.supplierID = value;
    }

    /**
     * Gets the value of the pendingOrg property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalSupplierAccountOrgType }{@code >}
     *     
     */
    public JAXBElement<GlobalSupplierAccountOrgType> getPendingOrg() {
        return pendingOrg;
    }

    /**
     * Sets the value of the pendingOrg property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalSupplierAccountOrgType }{@code >}
     *     
     */
    public void setPendingOrg(JAXBElement<GlobalSupplierAccountOrgType> value) {
        this.pendingOrg = value;
    }

    /**
     * Gets the value of the active property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isActive() {
        return active;
    }

    /**
     * Sets the value of the active property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setActive(Boolean value) {
        this.active = value;
    }

    /**
     * Gets the value of the passwordChange property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getPasswordChange() {
        return passwordChange;
    }

    /**
     * Sets the value of the passwordChange property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPasswordChange(Date value) {
        this.passwordChange = value;
    }

}
