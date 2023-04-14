
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalCreditHeaderResultCodes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalCreditHeaderResultCodes"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.id3global.com/ID3gWS/2013/04}GlobalItemCheckResultCodes"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CreditMatches" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalCreditMatch" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalCreditHeaderResultCodes", propOrder = {
    "creditMatches"
})
public class GlobalCreditHeaderResultCodesType
    extends GlobalItemCheckResultCodesType
{

    @XmlElementRef(name = "CreditMatches", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalCreditMatchType> creditMatches;

    /**
     * Gets the value of the creditMatches property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalCreditMatchType }{@code >}
     *     
     */
    public JAXBElement<GlobalCreditMatchType> getCreditMatches() {
        return creditMatches;
    }

    /**
     * Sets the value of the creditMatches property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalCreditMatchType }{@code >}
     *     
     */
    public void setCreditMatches(JAXBElement<GlobalCreditMatchType> value) {
        this.creditMatches = value;
    }

}
