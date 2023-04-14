
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
 *         &lt;element name="CopyProfileResult" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalProfileDetails" minOccurs="0"/&gt;
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
    "copyProfileResult"
})
@XmlRootElement(name = "CopyProfileResponse")
public class CopyProfileResponseElement {

    @XmlElementRef(name = "CopyProfileResult", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalProfileDetailsType> copyProfileResult;

    /**
     * Gets the value of the copyProfileResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalProfileDetailsType }{@code >}
     *     
     */
    public JAXBElement<GlobalProfileDetailsType> getCopyProfileResult() {
        return copyProfileResult;
    }

    /**
     * Sets the value of the copyProfileResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalProfileDetailsType }{@code >}
     *     
     */
    public void setCopyProfileResult(JAXBElement<GlobalProfileDetailsType> value) {
        this.copyProfileResult = value;
    }

}
