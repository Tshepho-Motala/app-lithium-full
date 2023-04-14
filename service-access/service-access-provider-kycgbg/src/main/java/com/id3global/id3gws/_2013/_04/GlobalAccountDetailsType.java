
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalAccountDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalAccountDetails"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.id3global.com/ID3gWS/2013/04}GlobalAccount"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RequireChangePassword" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="DisabledByInactivity" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="DisabledByAdministrator" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="PIN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="PasswordNeverExpires" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="IsAdminPolicy" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="FailedAttempts" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalAccountDetails", propOrder = {
    "requireChangePassword",
    "disabledByInactivity",
    "disabledByAdministrator",
    "pin",
    "passwordNeverExpires",
    "isAdminPolicy",
    "failedAttempts"
})
public class GlobalAccountDetailsType
    extends GlobalAccountType
{

    @XmlElement(name = "RequireChangePassword")
    protected Boolean requireChangePassword;
    @XmlElement(name = "DisabledByInactivity")
    protected Boolean disabledByInactivity;
    @XmlElement(name = "DisabledByAdministrator")
    protected Boolean disabledByAdministrator;
    @XmlElementRef(name = "PIN", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> pin;
    @XmlElement(name = "PasswordNeverExpires")
    protected Boolean passwordNeverExpires;
    @XmlElement(name = "IsAdminPolicy")
    protected Boolean isAdminPolicy;
    @XmlElement(name = "FailedAttempts")
    @XmlSchemaType(name = "unsignedInt")
    protected Long failedAttempts;

    /**
     * Gets the value of the requireChangePassword property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRequireChangePassword() {
        return requireChangePassword;
    }

    /**
     * Sets the value of the requireChangePassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRequireChangePassword(Boolean value) {
        this.requireChangePassword = value;
    }

    /**
     * Gets the value of the disabledByInactivity property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDisabledByInactivity() {
        return disabledByInactivity;
    }

    /**
     * Sets the value of the disabledByInactivity property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDisabledByInactivity(Boolean value) {
        this.disabledByInactivity = value;
    }

    /**
     * Gets the value of the disabledByAdministrator property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDisabledByAdministrator() {
        return disabledByAdministrator;
    }

    /**
     * Sets the value of the disabledByAdministrator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDisabledByAdministrator(Boolean value) {
        this.disabledByAdministrator = value;
    }

    /**
     * Gets the value of the pin property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPIN() {
        return pin;
    }

    /**
     * Sets the value of the pin property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPIN(JAXBElement<String> value) {
        this.pin = value;
    }

    /**
     * Gets the value of the passwordNeverExpires property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPasswordNeverExpires() {
        return passwordNeverExpires;
    }

    /**
     * Sets the value of the passwordNeverExpires property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPasswordNeverExpires(Boolean value) {
        this.passwordNeverExpires = value;
    }

    /**
     * Gets the value of the isAdminPolicy property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsAdminPolicy() {
        return isAdminPolicy;
    }

    /**
     * Sets the value of the isAdminPolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsAdminPolicy(Boolean value) {
        this.isAdminPolicy = value;
    }

    /**
     * Gets the value of the failedAttempts property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getFailedAttempts() {
        return failedAttempts;
    }

    /**
     * Sets the value of the failedAttempts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setFailedAttempts(Long value) {
        this.failedAttempts = value;
    }

}
