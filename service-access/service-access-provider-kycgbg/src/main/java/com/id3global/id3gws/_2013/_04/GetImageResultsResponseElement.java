
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
 *         &lt;element name="GetImageResultsResult" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalDIVData" minOccurs="0"/&gt;
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
    "getImageResultsResult"
})
@XmlRootElement(name = "GetImageResultsResponse")
public class GetImageResultsResponseElement {

    @XmlElementRef(name = "GetImageResultsResult", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalDIVDataType> getImageResultsResult;

    /**
     * Gets the value of the getImageResultsResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalDIVDataType }{@code >}
     *     
     */
    public JAXBElement<GlobalDIVDataType> getGetImageResultsResult() {
        return getImageResultsResult;
    }

    /**
     * Sets the value of the getImageResultsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalDIVDataType }{@code >}
     *     
     */
    public void setGetImageResultsResult(JAXBElement<GlobalDIVDataType> value) {
        this.getImageResultsResult = value;
    }

}
