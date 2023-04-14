
package com.id3global.id3gws._2013._04;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="GetReportImageResult" type="{http://schemas.microsoft.com/Message}StreamBody"/&gt;
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
    "getReportImageResult"
})
@XmlRootElement(name = "GetReportImageResponse")
public class GetReportImageResponseElement {

    @XmlElement(name = "GetReportImageResult", required = true)
    protected byte[] getReportImageResult;

    /**
     * Gets the value of the getReportImageResult property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getGetReportImageResult() {
        return getReportImageResult;
    }

    /**
     * Sets the value of the getReportImageResult property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setGetReportImageResult(byte[] value) {
        this.getReportImageResult = value;
    }

}
