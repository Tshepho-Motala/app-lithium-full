
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalItem"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AllConditionsMustBeMet" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="ResultCodesConditions" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalConditionResultCodes" minOccurs="0"/&gt;
 *         &lt;element name="ScoreConditions" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalConditionScore" minOccurs="0"/&gt;
 *         &lt;element name="DecisionBandConditions" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalConditionDecision" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalItem", propOrder = {
    "id",
    "name",
    "description",
    "allConditionsMustBeMet",
    "resultCodesConditions",
    "scoreConditions",
    "decisionBandConditions"
})
@XmlSeeAlso({
    GlobalItemCheckType.class,
    GlobalBreakpointType.class
})
public class GlobalItemType {

    @XmlElement(name = "ID")
    @XmlSchemaType(name = "unsignedInt")
    protected Long id;
    @XmlElementRef(name = "Name", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> name;
    @XmlElementRef(name = "Description", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> description;
    @XmlElement(name = "AllConditionsMustBeMet")
    protected Boolean allConditionsMustBeMet;
    @XmlElementRef(name = "ResultCodesConditions", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalConditionResultCodesType> resultCodesConditions;
    @XmlElementRef(name = "ScoreConditions", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalConditionScoreType> scoreConditions;
    @XmlElementRef(name = "DecisionBandConditions", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalConditionDecisionType> decisionBandConditions;

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
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDescription(JAXBElement<String> value) {
        this.description = value;
    }

    /**
     * Gets the value of the allConditionsMustBeMet property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAllConditionsMustBeMet() {
        return allConditionsMustBeMet;
    }

    /**
     * Sets the value of the allConditionsMustBeMet property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAllConditionsMustBeMet(Boolean value) {
        this.allConditionsMustBeMet = value;
    }

    /**
     * Gets the value of the resultCodesConditions property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalConditionResultCodesType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalConditionResultCodesType> getResultCodesConditions() {
        return resultCodesConditions;
    }

    /**
     * Sets the value of the resultCodesConditions property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalConditionResultCodesType }{@code >}
     *     
     */
    public void setResultCodesConditions(JAXBElement<ArrayOfGlobalConditionResultCodesType> value) {
        this.resultCodesConditions = value;
    }

    /**
     * Gets the value of the scoreConditions property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalConditionScoreType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalConditionScoreType> getScoreConditions() {
        return scoreConditions;
    }

    /**
     * Sets the value of the scoreConditions property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalConditionScoreType }{@code >}
     *     
     */
    public void setScoreConditions(JAXBElement<ArrayOfGlobalConditionScoreType> value) {
        this.scoreConditions = value;
    }

    /**
     * Gets the value of the decisionBandConditions property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalConditionDecisionType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalConditionDecisionType> getDecisionBandConditions() {
        return decisionBandConditions;
    }

    /**
     * Sets the value of the decisionBandConditions property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalConditionDecisionType }{@code >}
     *     
     */
    public void setDecisionBandConditions(JAXBElement<ArrayOfGlobalConditionDecisionType> value) {
        this.decisionBandConditions = value;
    }

}
