
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
 *         &lt;element name="UploadAndProcessByProfilePropertiesResult" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalDIVData" minOccurs="0"/&gt;
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
    "uploadAndProcessByProfilePropertiesResult"
})
@XmlRootElement(name = "UploadAndProcessByProfilePropertiesResponse")
public class UploadAndProcessByProfilePropertiesResponseElement {

    @XmlElementRef(name = "UploadAndProcessByProfilePropertiesResult", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalDIVDataType> uploadAndProcessByProfilePropertiesResult;

    /**
     * Gets the value of the uploadAndProcessByProfilePropertiesResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalDIVDataType }{@code >}
     *     
     */
    public JAXBElement<GlobalDIVDataType> getUploadAndProcessByProfilePropertiesResult() {
        return uploadAndProcessByProfilePropertiesResult;
    }

    /**
     * Sets the value of the uploadAndProcessByProfilePropertiesResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalDIVDataType }{@code >}
     *     
     */
    public void setUploadAndProcessByProfilePropertiesResult(JAXBElement<GlobalDIVDataType> value) {
        this.uploadAndProcessByProfilePropertiesResult = value;
    }

}
