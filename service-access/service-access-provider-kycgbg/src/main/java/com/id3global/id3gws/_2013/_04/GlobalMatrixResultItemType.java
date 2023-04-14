
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalMatrixResultItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalMatrixResultItem"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="MatchType" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalMatrixMatchType" minOccurs="0"/&gt;
 *         &lt;element name="Reason" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalMatrixMatchReason" minOccurs="0"/&gt;
 *         &lt;element name="Key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="CellKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="CaptionResourceKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DescriptionResourceKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="RecommendResourceKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="NotesResourceKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalMatrixResultItem", propOrder = {
    "matchType",
    "reason",
    "key",
    "cellKey",
    "captionResourceKey",
    "descriptionResourceKey",
    "recommendResourceKey",
    "notesResourceKey"
})
public class GlobalMatrixResultItemType {

    @XmlElement(name = "MatchType")
    @XmlSchemaType(name = "string")
    protected GlobalMatrixMatchTypeType matchType;
    @XmlElement(name = "Reason")
    @XmlSchemaType(name = "string")
    protected GlobalMatrixMatchReasonType reason;
    @XmlElementRef(name = "Key", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> key;
    @XmlElementRef(name = "CellKey", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> cellKey;
    @XmlElementRef(name = "CaptionResourceKey", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> captionResourceKey;
    @XmlElementRef(name = "DescriptionResourceKey", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> descriptionResourceKey;
    @XmlElementRef(name = "RecommendResourceKey", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> recommendResourceKey;
    @XmlElementRef(name = "NotesResourceKey", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> notesResourceKey;

    /**
     * Gets the value of the matchType property.
     * 
     * @return
     *     possible object is
     *     {@link GlobalMatrixMatchTypeType }
     *     
     */
    public GlobalMatrixMatchTypeType getMatchType() {
        return matchType;
    }

    /**
     * Sets the value of the matchType property.
     * 
     * @param value
     *     allowed object is
     *     {@link GlobalMatrixMatchTypeType }
     *     
     */
    public void setMatchType(GlobalMatrixMatchTypeType value) {
        this.matchType = value;
    }

    /**
     * Gets the value of the reason property.
     * 
     * @return
     *     possible object is
     *     {@link GlobalMatrixMatchReasonType }
     *     
     */
    public GlobalMatrixMatchReasonType getReason() {
        return reason;
    }

    /**
     * Sets the value of the reason property.
     * 
     * @param value
     *     allowed object is
     *     {@link GlobalMatrixMatchReasonType }
     *     
     */
    public void setReason(GlobalMatrixMatchReasonType value) {
        this.reason = value;
    }

    /**
     * Gets the value of the key property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getKey() {
        return key;
    }

    /**
     * Sets the value of the key property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setKey(JAXBElement<String> value) {
        this.key = value;
    }

    /**
     * Gets the value of the cellKey property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCellKey() {
        return cellKey;
    }

    /**
     * Sets the value of the cellKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCellKey(JAXBElement<String> value) {
        this.cellKey = value;
    }

    /**
     * Gets the value of the captionResourceKey property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCaptionResourceKey() {
        return captionResourceKey;
    }

    /**
     * Sets the value of the captionResourceKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCaptionResourceKey(JAXBElement<String> value) {
        this.captionResourceKey = value;
    }

    /**
     * Gets the value of the descriptionResourceKey property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDescriptionResourceKey() {
        return descriptionResourceKey;
    }

    /**
     * Sets the value of the descriptionResourceKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDescriptionResourceKey(JAXBElement<String> value) {
        this.descriptionResourceKey = value;
    }

    /**
     * Gets the value of the recommendResourceKey property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRecommendResourceKey() {
        return recommendResourceKey;
    }

    /**
     * Sets the value of the recommendResourceKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRecommendResourceKey(JAXBElement<String> value) {
        this.recommendResourceKey = value;
    }

    /**
     * Gets the value of the notesResourceKey property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getNotesResourceKey() {
        return notesResourceKey;
    }

    /**
     * Sets the value of the notesResourceKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setNotesResourceKey(JAXBElement<String> value) {
        this.notesResourceKey = value;
    }

}
