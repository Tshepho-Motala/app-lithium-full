
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalProfileDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalProfileDetails"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.id3global.com/ID3gWS/2013/04}GlobalProfileVersion"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DecisionBanding" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalDecisionBanding" minOccurs="0"/&gt;
 *         &lt;element name="Items" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalItem" minOccurs="0"/&gt;
 *         &lt;element name="PreAuthenticationRules" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalPreAuthentication" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalProfileDetails", propOrder = {
    "decisionBanding",
    "items",
    "preAuthenticationRules"
})
public class GlobalProfileDetailsType
    extends GlobalProfileVersionType
{

    @XmlElementRef(name = "DecisionBanding", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalDecisionBandingType> decisionBanding;
    @XmlElementRef(name = "Items", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalItemType> items;
    @XmlElementRef(name = "PreAuthenticationRules", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalPreAuthenticationType> preAuthenticationRules;

    /**
     * Gets the value of the decisionBanding property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalDecisionBandingType }{@code >}
     *     
     */
    public JAXBElement<GlobalDecisionBandingType> getDecisionBanding() {
        return decisionBanding;
    }

    /**
     * Sets the value of the decisionBanding property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalDecisionBandingType }{@code >}
     *     
     */
    public void setDecisionBanding(JAXBElement<GlobalDecisionBandingType> value) {
        this.decisionBanding = value;
    }

    /**
     * Gets the value of the items property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalItemType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalItemType> getItems() {
        return items;
    }

    /**
     * Sets the value of the items property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalItemType }{@code >}
     *     
     */
    public void setItems(JAXBElement<ArrayOfGlobalItemType> value) {
        this.items = value;
    }

    /**
     * Gets the value of the preAuthenticationRules property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalPreAuthenticationType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalPreAuthenticationType> getPreAuthenticationRules() {
        return preAuthenticationRules;
    }

    /**
     * Sets the value of the preAuthenticationRules property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalPreAuthenticationType }{@code >}
     *     
     */
    public void setPreAuthenticationRules(JAXBElement<ArrayOfGlobalPreAuthenticationType> value) {
        this.preAuthenticationRules = value;
    }

}
