
package com.id3global.id3gws._2013._04;

import java.util.Date;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.datacontract.schemas._2004._07.globalcheck_useraccountlib.EnumsFormat;
import org.datacontract.schemas._2004._07.globalcheck_useraccountlib.EnumsState;
import org.datacontract.schemas._2004._07.globalcheck_useraccountlib.EnumsUserAccountType;


/**
 * <p>Java class for UserAccountExtract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UserAccountExtract"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ID" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/&gt;
 *         &lt;element name="OrgID" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/&gt;
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Type" type="{http://schemas.datacontract.org/2004/07/GlobalCheck.UserAccountLib.Common}Enums.UserAccountType" minOccurs="0"/&gt;
 *         &lt;element name="Start" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="End" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="Format" type="{http://schemas.datacontract.org/2004/07/GlobalCheck.UserAccountLib.Common}Enums.Format" minOccurs="0"/&gt;
 *         &lt;element name="State" type="{http://schemas.datacontract.org/2004/07/GlobalCheck.UserAccountLib.Common}Enums.State" minOccurs="0"/&gt;
 *         &lt;element name="CreatedByID" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/&gt;
 *         &lt;element name="CreatedByAccountName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TotalAccount" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="ProgressCnt" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserAccountExtract", propOrder = {
    "id",
    "orgID",
    "name",
    "type",
    "start",
    "end",
    "format",
    "state",
    "createdByID",
    "createdByAccountName",
    "totalAccount",
    "progressCnt"
})
public class UserAccountExtractType {

    @XmlElement(name = "ID")
    protected String id;
    @XmlElement(name = "OrgID")
    protected String orgID;
    @XmlElementRef(name = "Name", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> name;
    @XmlElement(name = "Type")
    @XmlSchemaType(name = "string")
    protected EnumsUserAccountType type;
    @XmlElementRef(name = "Start", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<Date> start;
    @XmlElementRef(name = "End", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<Date> end;
    @XmlElement(name = "Format")
    @XmlSchemaType(name = "string")
    protected EnumsFormat format;
    @XmlElement(name = "State")
    @XmlSchemaType(name = "string")
    protected EnumsState state;
    @XmlElement(name = "CreatedByID")
    protected String createdByID;
    @XmlElementRef(name = "CreatedByAccountName", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> createdByAccountName;
    @XmlElement(name = "TotalAccount")
    protected Integer totalAccount;
    @XmlElement(name = "ProgressCnt")
    protected Integer progressCnt;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setID(String value) {
        this.id = value;
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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link EnumsUserAccountType }
     *     
     */
    public EnumsUserAccountType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnumsUserAccountType }
     *     
     */
    public void setType(EnumsUserAccountType value) {
        this.type = value;
    }

    /**
     * Gets the value of the start property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Date }{@code >}
     *     
     */
    public JAXBElement<Date> getStart() {
        return start;
    }

    /**
     * Sets the value of the start property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Date }{@code >}
     *     
     */
    public void setStart(JAXBElement<Date> value) {
        this.start = value;
    }

    /**
     * Gets the value of the end property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Date }{@code >}
     *     
     */
    public JAXBElement<Date> getEnd() {
        return end;
    }

    /**
     * Sets the value of the end property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Date }{@code >}
     *     
     */
    public void setEnd(JAXBElement<Date> value) {
        this.end = value;
    }

    /**
     * Gets the value of the format property.
     * 
     * @return
     *     possible object is
     *     {@link EnumsFormat }
     *     
     */
    public EnumsFormat getFormat() {
        return format;
    }

    /**
     * Sets the value of the format property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnumsFormat }
     *     
     */
    public void setFormat(EnumsFormat value) {
        this.format = value;
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link EnumsState }
     *     
     */
    public EnumsState getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnumsState }
     *     
     */
    public void setState(EnumsState value) {
        this.state = value;
    }

    /**
     * Gets the value of the createdByID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreatedByID() {
        return createdByID;
    }

    /**
     * Sets the value of the createdByID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreatedByID(String value) {
        this.createdByID = value;
    }

    /**
     * Gets the value of the createdByAccountName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCreatedByAccountName() {
        return createdByAccountName;
    }

    /**
     * Sets the value of the createdByAccountName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCreatedByAccountName(JAXBElement<String> value) {
        this.createdByAccountName = value;
    }

    /**
     * Gets the value of the totalAccount property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTotalAccount() {
        return totalAccount;
    }

    /**
     * Sets the value of the totalAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTotalAccount(Integer value) {
        this.totalAccount = value;
    }

    /**
     * Gets the value of the progressCnt property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getProgressCnt() {
        return progressCnt;
    }

    /**
     * Sets the value of the progressCnt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setProgressCnt(Integer value) {
        this.progressCnt = value;
    }

}
