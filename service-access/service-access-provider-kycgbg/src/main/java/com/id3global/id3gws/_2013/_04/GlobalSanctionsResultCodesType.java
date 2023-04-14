
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalSanctionsResultCodes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalSanctionsResultCodes"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.id3global.com/ID3gWS/2013/04}GlobalItemCheckResultCodes"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SanctionsMatches" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalSanctionsMatch" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalSanctionsResultCodes", propOrder = {
    "sanctionsMatches"
})
public class GlobalSanctionsResultCodesType
    extends GlobalItemCheckResultCodesType
{

    @XmlElementRef(name = "SanctionsMatches", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalSanctionsMatchType> sanctionsMatches;

    /**
     * Gets the value of the sanctionsMatches property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalSanctionsMatchType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalSanctionsMatchType> getSanctionsMatches() {
        return sanctionsMatches;
    }

    /**
     * Sets the value of the sanctionsMatches property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalSanctionsMatchType }{@code >}
     *     
     */
    public void setSanctionsMatches(JAXBElement<ArrayOfGlobalSanctionsMatchType> value) {
        this.sanctionsMatches = value;
    }

}
