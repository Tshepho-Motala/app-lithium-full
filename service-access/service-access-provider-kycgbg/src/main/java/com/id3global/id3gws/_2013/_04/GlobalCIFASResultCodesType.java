
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalCIFASResultCodes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalCIFASResultCodes"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.id3global.com/ID3gWS/2013/04}GlobalItemCheckResultCodes"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CIFASMatches" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalCIFASMatch" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalCIFASResultCodes", propOrder = {
    "cifasMatches"
})
public class GlobalCIFASResultCodesType
    extends GlobalItemCheckResultCodesType
{

    @XmlElementRef(name = "CIFASMatches", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalCIFASMatchType> cifasMatches;

    /**
     * Gets the value of the cifasMatches property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalCIFASMatchType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalCIFASMatchType> getCIFASMatches() {
        return cifasMatches;
    }

    /**
     * Sets the value of the cifasMatches property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalCIFASMatchType }{@code >}
     *     
     */
    public void setCIFASMatches(JAXBElement<ArrayOfGlobalCIFASMatchType> value) {
        this.cifasMatches = value;
    }

}
