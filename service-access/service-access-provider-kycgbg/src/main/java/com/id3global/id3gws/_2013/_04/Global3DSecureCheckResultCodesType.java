
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Global3DSecureCheckResultCodes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Global3DSecureCheckResultCodes"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.id3global.com/ID3gWS/2013/04}GlobalItemCheckResultCodes"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="MerchantData" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ThreeDSecureACSUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ThreeDSecureRequest" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Global3DSecureCheckResultCodes", propOrder = {
    "merchantData",
    "threeDSecureACSUrl",
    "threeDSecureRequest"
})
public class Global3DSecureCheckResultCodesType
    extends GlobalItemCheckResultCodesType
{

    @XmlElementRef(name = "MerchantData", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> merchantData;
    @XmlElementRef(name = "ThreeDSecureACSUrl", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> threeDSecureACSUrl;
    @XmlElementRef(name = "ThreeDSecureRequest", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> threeDSecureRequest;

    /**
     * Gets the value of the merchantData property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMerchantData() {
        return merchantData;
    }

    /**
     * Sets the value of the merchantData property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMerchantData(JAXBElement<String> value) {
        this.merchantData = value;
    }

    /**
     * Gets the value of the threeDSecureACSUrl property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getThreeDSecureACSUrl() {
        return threeDSecureACSUrl;
    }

    /**
     * Sets the value of the threeDSecureACSUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setThreeDSecureACSUrl(JAXBElement<String> value) {
        this.threeDSecureACSUrl = value;
    }

    /**
     * Gets the value of the threeDSecureRequest property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getThreeDSecureRequest() {
        return threeDSecureRequest;
    }

    /**
     * Sets the value of the threeDSecureRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setThreeDSecureRequest(JAXBElement<String> value) {
        this.threeDSecureRequest = value;
    }

}
