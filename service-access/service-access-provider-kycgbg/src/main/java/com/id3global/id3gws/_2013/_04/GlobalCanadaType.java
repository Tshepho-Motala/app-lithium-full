
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalCanada complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalCanada"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SocialInsuranceNumber" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalCanadaSocialInsuranceNumber" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalCanada", propOrder = {
    "socialInsuranceNumber"
})
public class GlobalCanadaType {

    @XmlElementRef(name = "SocialInsuranceNumber", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalCanadaSocialInsuranceNumberType> socialInsuranceNumber;

    /**
     * Gets the value of the socialInsuranceNumber property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalCanadaSocialInsuranceNumberType }{@code >}
     *     
     */
    public JAXBElement<GlobalCanadaSocialInsuranceNumberType> getSocialInsuranceNumber() {
        return socialInsuranceNumber;
    }

    /**
     * Sets the value of the socialInsuranceNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalCanadaSocialInsuranceNumberType }{@code >}
     *     
     */
    public void setSocialInsuranceNumber(JAXBElement<GlobalCanadaSocialInsuranceNumberType> value) {
        this.socialInsuranceNumber = value;
    }

}
