
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalScoring complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalScoring"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Comment" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalWeighting" minOccurs="0"/&gt;
 *         &lt;element name="Match" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalWeighting" minOccurs="0"/&gt;
 *         &lt;element name="Warning" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalWeighting" minOccurs="0"/&gt;
 *         &lt;element name="Mismatch" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalWeighting" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalScoring", propOrder = {
    "comment",
    "match",
    "warning",
    "mismatch"
})
public class GlobalScoringType {

    @XmlElementRef(name = "Comment", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalWeightingType> comment;
    @XmlElementRef(name = "Match", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalWeightingType> match;
    @XmlElementRef(name = "Warning", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalWeightingType> warning;
    @XmlElementRef(name = "Mismatch", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalWeightingType> mismatch;

    /**
     * Gets the value of the comment property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalWeightingType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalWeightingType> getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalWeightingType }{@code >}
     *     
     */
    public void setComment(JAXBElement<ArrayOfGlobalWeightingType> value) {
        this.comment = value;
    }

    /**
     * Gets the value of the match property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalWeightingType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalWeightingType> getMatch() {
        return match;
    }

    /**
     * Sets the value of the match property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalWeightingType }{@code >}
     *     
     */
    public void setMatch(JAXBElement<ArrayOfGlobalWeightingType> value) {
        this.match = value;
    }

    /**
     * Gets the value of the warning property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalWeightingType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalWeightingType> getWarning() {
        return warning;
    }

    /**
     * Sets the value of the warning property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalWeightingType }{@code >}
     *     
     */
    public void setWarning(JAXBElement<ArrayOfGlobalWeightingType> value) {
        this.warning = value;
    }

    /**
     * Gets the value of the mismatch property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalWeightingType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalWeightingType> getMismatch() {
        return mismatch;
    }

    /**
     * Sets the value of the mismatch property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalWeightingType }{@code >}
     *     
     */
    public void setMismatch(JAXBElement<ArrayOfGlobalWeightingType> value) {
        this.mismatch = value;
    }

}
