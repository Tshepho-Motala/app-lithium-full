
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="GetOrganisationDetailsResult" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalOrganisationDetails" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getOrganisationDetailsResult"
})
@XmlRootElement(name = "GetOrganisationDetailsResponse")
public class GetOrganisationDetailsResponseElement {

    @XmlElementRef(name = "GetOrganisationDetailsResult", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalOrganisationDetailsType> getOrganisationDetailsResult;

    /**
     * Gets the value of the getOrganisationDetailsResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalOrganisationDetailsType }{@code >}
     *     
     */
    public JAXBElement<GlobalOrganisationDetailsType> getGetOrganisationDetailsResult() {
        return getOrganisationDetailsResult;
    }

    /**
     * Sets the value of the getOrganisationDetailsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalOrganisationDetailsType }{@code >}
     *     
     */
    public void setGetOrganisationDetailsResult(JAXBElement<GlobalOrganisationDetailsType> value) {
        this.getOrganisationDetailsResult = value;
    }

}
