
package com.id3global.id3gws._2013._04;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element name="GetDocImageMinHeightResolutionResult" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
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
    "getDocImageMinHeightResolutionResult"
})
@XmlRootElement(name = "GetDocImageMinHeightResolutionResponse")
public class GetDocImageMinHeightResolutionResponseElement {

    @XmlElement(name = "GetDocImageMinHeightResolutionResult")
    @XmlSchemaType(name = "unsignedInt")
    protected Long getDocImageMinHeightResolutionResult;

    /**
     * Gets the value of the getDocImageMinHeightResolutionResult property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getGetDocImageMinHeightResolutionResult() {
        return getDocImageMinHeightResolutionResult;
    }

    /**
     * Sets the value of the getDocImageMinHeightResolutionResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setGetDocImageMinHeightResolutionResult(Long value) {
        this.getDocImageMinHeightResolutionResult = value;
    }

}
