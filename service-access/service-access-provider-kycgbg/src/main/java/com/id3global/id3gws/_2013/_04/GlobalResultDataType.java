
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
 * <p>Java class for GlobalResultData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalResultData"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AuthenticationID" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/&gt;
 *         &lt;element name="Timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="CustomerRef" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ProfileID" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/&gt;
 *         &lt;element name="ProfileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ProfileVersion" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="ProfileRevision" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="ProfileState" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalProfileState" minOccurs="0"/&gt;
 *         &lt;element name="ResultCodes" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalItemCheckResultCodes" minOccurs="0"/&gt;
 *         &lt;element name="Score" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="BandText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="UserBreakpoint" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="NoRetry" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="ChainID" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/&gt;
 *         &lt;element name="Country" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalResultData", propOrder = {
    "authenticationID",
    "timestamp",
    "customerRef",
    "profileID",
    "profileName",
    "profileVersion",
    "profileRevision",
    "profileState",
    "resultCodes",
    "score",
    "bandText",
    "userBreakpoint",
    "noRetry",
    "chainID",
    "country"
})
public class GlobalResultDataType {

    @XmlElement(name = "AuthenticationID")
    protected String authenticationID;
    @XmlElement(name = "Timestamp", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date timestamp;
    @XmlElementRef(name = "CustomerRef", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> customerRef;
    @XmlElement(name = "ProfileID")
    protected String profileID;
    @XmlElementRef(name = "ProfileName", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> profileName;
    @XmlElement(name = "ProfileVersion")
    @XmlSchemaType(name = "unsignedInt")
    protected Long profileVersion;
    @XmlElement(name = "ProfileRevision")
    @XmlSchemaType(name = "unsignedInt")
    protected Long profileRevision;
    @XmlElement(name = "ProfileState")
    @XmlSchemaType(name = "string")
    protected GlobalProfileStateType profileState;
    @XmlElementRef(name = "ResultCodes", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalItemCheckResultCodesType> resultCodes;
    @XmlElementRef(name = "Score", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> score;
    @XmlElementRef(name = "BandText", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> bandText;
    @XmlElement(name = "UserBreakpoint")
    @XmlSchemaType(name = "unsignedInt")
    protected Long userBreakpoint;
    @XmlElement(name = "NoRetry")
    protected Boolean noRetry;
    @XmlElementRef(name = "ChainID", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> chainID;
    @XmlElementRef(name = "Country", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> country;

    /**
     * Gets the value of the authenticationID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthenticationID() {
        return authenticationID;
    }

    /**
     * Sets the value of the authenticationID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthenticationID(String value) {
        this.authenticationID = value;
    }

    /**
     * Gets the value of the timestamp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimestamp(Date value) {
        this.timestamp = value;
    }

    /**
     * Gets the value of the customerRef property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCustomerRef() {
        return customerRef;
    }

    /**
     * Sets the value of the customerRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCustomerRef(JAXBElement<String> value) {
        this.customerRef = value;
    }

    /**
     * Gets the value of the profileID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProfileID() {
        return profileID;
    }

    /**
     * Sets the value of the profileID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProfileID(String value) {
        this.profileID = value;
    }

    /**
     * Gets the value of the profileName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getProfileName() {
        return profileName;
    }

    /**
     * Sets the value of the profileName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setProfileName(JAXBElement<String> value) {
        this.profileName = value;
    }

    /**
     * Gets the value of the profileVersion property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getProfileVersion() {
        return profileVersion;
    }

    /**
     * Sets the value of the profileVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setProfileVersion(Long value) {
        this.profileVersion = value;
    }

    /**
     * Gets the value of the profileRevision property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getProfileRevision() {
        return profileRevision;
    }

    /**
     * Sets the value of the profileRevision property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setProfileRevision(Long value) {
        this.profileRevision = value;
    }

    /**
     * Gets the value of the profileState property.
     * 
     * @return
     *     possible object is
     *     {@link GlobalProfileStateType }
     *     
     */
    public GlobalProfileStateType getProfileState() {
        return profileState;
    }

    /**
     * Sets the value of the profileState property.
     * 
     * @param value
     *     allowed object is
     *     {@link GlobalProfileStateType }
     *     
     */
    public void setProfileState(GlobalProfileStateType value) {
        this.profileState = value;
    }

    /**
     * Gets the value of the resultCodes property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalItemCheckResultCodesType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalItemCheckResultCodesType> getResultCodes() {
        return resultCodes;
    }

    /**
     * Sets the value of the resultCodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalItemCheckResultCodesType }{@code >}
     *     
     */
    public void setResultCodes(JAXBElement<ArrayOfGlobalItemCheckResultCodesType> value) {
        this.resultCodes = value;
    }

    /**
     * Gets the value of the score property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getScore() {
        return score;
    }

    /**
     * Sets the value of the score property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setScore(JAXBElement<Integer> value) {
        this.score = value;
    }

    /**
     * Gets the value of the bandText property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getBandText() {
        return bandText;
    }

    /**
     * Sets the value of the bandText property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setBandText(JAXBElement<String> value) {
        this.bandText = value;
    }

    /**
     * Gets the value of the userBreakpoint property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getUserBreakpoint() {
        return userBreakpoint;
    }

    /**
     * Sets the value of the userBreakpoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setUserBreakpoint(Long value) {
        this.userBreakpoint = value;
    }

    /**
     * Gets the value of the noRetry property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNoRetry() {
        return noRetry;
    }

    /**
     * Sets the value of the noRetry property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNoRetry(Boolean value) {
        this.noRetry = value;
    }

    /**
     * Gets the value of the chainID property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getChainID() {
        return chainID;
    }

    /**
     * Sets the value of the chainID property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setChainID(JAXBElement<String> value) {
        this.chainID = value;
    }

    /**
     * Gets the value of the country property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCountry() {
        return country;
    }

    /**
     * Sets the value of the country property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCountry(JAXBElement<String> value) {
        this.country = value;
    }

}
