
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
 *         &lt;element name="GetItemCheckResultCodesResult" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalItemCheckResultCodes" minOccurs="0"/&gt;
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
    "getItemCheckResultCodesResult"
})
@XmlRootElement(name = "GetItemCheckResultCodesResponse")
public class GetItemCheckResultCodesResponseElement {

    @XmlElementRef(name = "GetItemCheckResultCodesResult", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalItemCheckResultCodesType> getItemCheckResultCodesResult;

    /**
     * Gets the value of the getItemCheckResultCodesResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalItemCheckResultCodesType }{@code >}
     *     
     */
    public JAXBElement<GlobalItemCheckResultCodesType> getGetItemCheckResultCodesResult() {
        return getItemCheckResultCodesResult;
    }

    /**
     * Sets the value of the getItemCheckResultCodesResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalItemCheckResultCodesType }{@code >}
     *     
     */
    public void setGetItemCheckResultCodesResult(JAXBElement<GlobalItemCheckResultCodesType> value) {
        this.getItemCheckResultCodesResult = value;
    }

}
