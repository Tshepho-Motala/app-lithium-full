
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
 *         &lt;element name="CheckCredentialsResult" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalAccount" minOccurs="0"/&gt;
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
    "checkCredentialsResult"
})
@XmlRootElement(name = "CheckCredentialsResponse")
public class CheckCredentialsResponseElement {

    @XmlElementRef(name = "CheckCredentialsResult", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalAccountType> checkCredentialsResult;

    /**
     * Gets the value of the checkCredentialsResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalAccountType }{@code >}
     *     
     */
    public JAXBElement<GlobalAccountType> getCheckCredentialsResult() {
        return checkCredentialsResult;
    }

    /**
     * Sets the value of the checkCredentialsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalAccountType }{@code >}
     *     
     */
    public void setCheckCredentialsResult(JAXBElement<GlobalAccountType> value) {
        this.checkCredentialsResult = value;
    }

}
