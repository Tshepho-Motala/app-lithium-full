
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
 * <p>Java class for GlobalCaseState complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalCaseState"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="State" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="OldState" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="CreatedAccountID" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/&gt;
 *         &lt;element name="CreatedUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Created" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="CreatedOrganisationID" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/&gt;
 *         &lt;element name="CreatedDomain" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalCaseState", propOrder = {
    "state",
    "oldState",
    "createdAccountID",
    "createdUser",
    "created",
    "createdOrganisationID",
    "createdDomain"
})
public class GlobalCaseStateType {

    @XmlElement(name = "State")
    @XmlSchemaType(name = "unsignedInt")
    protected Long state;
    @XmlElement(name = "OldState")
    @XmlSchemaType(name = "unsignedInt")
    protected Long oldState;
    @XmlElement(name = "CreatedAccountID")
    protected String createdAccountID;
    @XmlElementRef(name = "CreatedUser", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> createdUser;
    @XmlElement(name = "Created", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date created;
    @XmlElement(name = "CreatedOrganisationID")
    protected String createdOrganisationID;
    @XmlElementRef(name = "CreatedDomain", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> createdDomain;

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
     * Gets the value of the oldState property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getOldState() {
        return oldState;
    }

    /**
     * Sets the value of the oldState property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setOldState(Long value) {
        this.oldState = value;
    }

    /**
     * Gets the value of the createdAccountID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreatedAccountID() {
        return createdAccountID;
    }

    /**
     * Sets the value of the createdAccountID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreatedAccountID(String value) {
        this.createdAccountID = value;
    }

    /**
     * Gets the value of the createdUser property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCreatedUser() {
        return createdUser;
    }

    /**
     * Sets the value of the createdUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCreatedUser(JAXBElement<String> value) {
        this.createdUser = value;
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
     * Gets the value of the createdDomain property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCreatedDomain() {
        return createdDomain;
    }

    /**
     * Sets the value of the createdDomain property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCreatedDomain(JAXBElement<String> value) {
        this.createdDomain = value;
    }

}
