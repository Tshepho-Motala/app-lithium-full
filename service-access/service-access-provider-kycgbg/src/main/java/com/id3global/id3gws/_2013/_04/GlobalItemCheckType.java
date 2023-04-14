
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalItemCheck complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalItemCheck"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.id3global.com/ID3gWS/2013/04}GlobalItem"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AllowedProperties" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Properties" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalProperty" minOccurs="0"/&gt;
 *         &lt;element name="Scoring" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalScoring" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalItemCheck", propOrder = {
    "allowedProperties",
    "properties",
    "scoring"
})
public class GlobalItemCheckType
    extends GlobalItemType
{

    @XmlElement(name = "AllowedProperties")
    protected Boolean allowedProperties;
    @XmlElementRef(name = "Properties", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalPropertyType> properties;
    @XmlElementRef(name = "Scoring", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalScoringType> scoring;

    /**
     * Gets the value of the allowedProperties property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAllowedProperties() {
        return allowedProperties;
    }

    /**
     * Sets the value of the allowedProperties property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAllowedProperties(Boolean value) {
        this.allowedProperties = value;
    }

    /**
     * Gets the value of the properties property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalPropertyType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalPropertyType> getProperties() {
        return properties;
    }

    /**
     * Sets the value of the properties property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalPropertyType }{@code >}
     *     
     */
    public void setProperties(JAXBElement<ArrayOfGlobalPropertyType> value) {
        this.properties = value;
    }

    /**
     * Gets the value of the scoring property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalScoringType }{@code >}
     *     
     */
    public JAXBElement<GlobalScoringType> getScoring() {
        return scoring;
    }

    /**
     * Sets the value of the scoring property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalScoringType }{@code >}
     *     
     */
    public void setScoring(JAXBElement<GlobalScoringType> value) {
        this.scoring = value;
    }

}
