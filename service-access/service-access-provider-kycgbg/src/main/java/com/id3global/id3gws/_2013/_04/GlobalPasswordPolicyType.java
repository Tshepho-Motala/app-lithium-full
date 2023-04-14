
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalPasswordPolicy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalPasswordPolicy"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Length" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="Uppercase" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="Lowercase" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="Numeric" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="Symbols" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="DisableInactiveDays" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="ChangePasswordDays" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="RequirePINForSignin" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalPasswordPolicy", propOrder = {
    "length",
    "uppercase",
    "lowercase",
    "numeric",
    "symbols",
    "disableInactiveDays",
    "changePasswordDays",
    "requirePINForSignin"
})
public class GlobalPasswordPolicyType {

    @XmlElement(name = "Length")
    @XmlSchemaType(name = "unsignedInt")
    protected Long length;
    @XmlElement(name = "Uppercase")
    @XmlSchemaType(name = "unsignedInt")
    protected Long uppercase;
    @XmlElement(name = "Lowercase")
    @XmlSchemaType(name = "unsignedInt")
    protected Long lowercase;
    @XmlElement(name = "Numeric")
    @XmlSchemaType(name = "unsignedInt")
    protected Long numeric;
    @XmlElement(name = "Symbols")
    @XmlSchemaType(name = "unsignedInt")
    protected Long symbols;
    @XmlElementRef(name = "DisableInactiveDays", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<Long> disableInactiveDays;
    @XmlElementRef(name = "ChangePasswordDays", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<Long> changePasswordDays;
    @XmlElement(name = "RequirePINForSignin")
    protected Boolean requirePINForSignin;

    /**
     * Gets the value of the length property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getLength() {
        return length;
    }

    /**
     * Sets the value of the length property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setLength(Long value) {
        this.length = value;
    }

    /**
     * Gets the value of the uppercase property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getUppercase() {
        return uppercase;
    }

    /**
     * Sets the value of the uppercase property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setUppercase(Long value) {
        this.uppercase = value;
    }

    /**
     * Gets the value of the lowercase property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getLowercase() {
        return lowercase;
    }

    /**
     * Sets the value of the lowercase property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setLowercase(Long value) {
        this.lowercase = value;
    }

    /**
     * Gets the value of the numeric property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getNumeric() {
        return numeric;
    }

    /**
     * Sets the value of the numeric property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setNumeric(Long value) {
        this.numeric = value;
    }

    /**
     * Gets the value of the symbols property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSymbols() {
        return symbols;
    }

    /**
     * Sets the value of the symbols property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSymbols(Long value) {
        this.symbols = value;
    }

    /**
     * Gets the value of the disableInactiveDays property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public JAXBElement<Long> getDisableInactiveDays() {
        return disableInactiveDays;
    }

    /**
     * Sets the value of the disableInactiveDays property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public void setDisableInactiveDays(JAXBElement<Long> value) {
        this.disableInactiveDays = value;
    }

    /**
     * Gets the value of the changePasswordDays property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public JAXBElement<Long> getChangePasswordDays() {
        return changePasswordDays;
    }

    /**
     * Sets the value of the changePasswordDays property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public void setChangePasswordDays(JAXBElement<Long> value) {
        this.changePasswordDays = value;
    }

    /**
     * Gets the value of the requirePINForSignin property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRequirePINForSignin() {
        return requirePINForSignin;
    }

    /**
     * Sets the value of the requirePINForSignin property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRequirePINForSignin(Boolean value) {
        this.requirePINForSignin = value;
    }

}
