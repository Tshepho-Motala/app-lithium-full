
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalConditionResultCodes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalConditionResultCodes"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="Comment" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalConditionResultCode" minOccurs="0"/&gt;
 *         &lt;element name="Match" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalConditionResultCode" minOccurs="0"/&gt;
 *         &lt;element name="Mismatch" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalConditionResultCode" minOccurs="0"/&gt;
 *         &lt;element name="Warning" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalConditionResultCode" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalConditionResultCodes", propOrder = {
    "id",
    "comment",
    "match",
    "mismatch",
    "warning"
})
public class GlobalConditionResultCodesType {

    @XmlElement(name = "ID")
    @XmlSchemaType(name = "unsignedInt")
    protected Long id;
    @XmlElementRef(name = "Comment", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalConditionResultCodeType> comment;
    @XmlElementRef(name = "Match", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalConditionResultCodeType> match;
    @XmlElementRef(name = "Mismatch", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalConditionResultCodeType> mismatch;
    @XmlElementRef(name = "Warning", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalConditionResultCodeType> warning;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setID(Long value) {
        this.id = value;
    }

    /**
     * Gets the value of the comment property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalConditionResultCodeType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalConditionResultCodeType> getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalConditionResultCodeType }{@code >}
     *     
     */
    public void setComment(JAXBElement<ArrayOfGlobalConditionResultCodeType> value) {
        this.comment = value;
    }

    /**
     * Gets the value of the match property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalConditionResultCodeType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalConditionResultCodeType> getMatch() {
        return match;
    }

    /**
     * Sets the value of the match property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalConditionResultCodeType }{@code >}
     *     
     */
    public void setMatch(JAXBElement<ArrayOfGlobalConditionResultCodeType> value) {
        this.match = value;
    }

    /**
     * Gets the value of the mismatch property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalConditionResultCodeType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalConditionResultCodeType> getMismatch() {
        return mismatch;
    }

    /**
     * Sets the value of the mismatch property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalConditionResultCodeType }{@code >}
     *     
     */
    public void setMismatch(JAXBElement<ArrayOfGlobalConditionResultCodeType> value) {
        this.mismatch = value;
    }

    /**
     * Gets the value of the warning property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalConditionResultCodeType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalConditionResultCodeType> getWarning() {
        return warning;
    }

    /**
     * Sets the value of the warning property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalConditionResultCodeType }{@code >}
     *     
     */
    public void setWarning(JAXBElement<ArrayOfGlobalConditionResultCodeType> value) {
        this.warning = value;
    }

}
