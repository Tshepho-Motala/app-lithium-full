
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
 *         &lt;element name="DocImage" type="{http://schemas.microsoft.com/Message}StreamBody"/&gt;
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
    "docImage"
})
@XmlRootElement(name = "UploadAndProcess")
public class UploadAndProcessElement {

    @XmlElement(name = "DocImage", required = true)
    protected byte[] docImage;

    /**
     * Gets the value of the docImage property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getDocImage() {
        return docImage;
    }

    /**
     * Sets the value of the docImage property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setDocImage(byte[] value) {
        this.docImage = value;
    }

}
