
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfunsignedInt;


/**
 * <p>Java class for GlobalCaseDocumentResultParameters complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalCaseDocumentResultParameters"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.id3global.com/ID3gWS/2013/04}GlobalCaseResultParameters"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RequireValidationTypes" type="{http://schemas.microsoft.com/2003/10/Serialization/Arrays}ArrayOfunsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="MatchItemPassKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MatchItemWarningKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MatchItemFailKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalCaseDocumentResultParameters", propOrder = {
    "requireValidationTypes",
    "matchItemPassKey",
    "matchItemWarningKey",
    "matchItemFailKey"
})
public class GlobalCaseDocumentResultParametersType
    extends GlobalCaseResultParametersType
{

    @XmlElementRef(name = "RequireValidationTypes", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfunsignedInt> requireValidationTypes;
    @XmlElementRef(name = "MatchItemPassKey", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> matchItemPassKey;
    @XmlElementRef(name = "MatchItemWarningKey", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> matchItemWarningKey;
    @XmlElementRef(name = "MatchItemFailKey", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> matchItemFailKey;

    /**
     * Gets the value of the requireValidationTypes property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfunsignedInt }{@code >}
     *     
     */
    public JAXBElement<ArrayOfunsignedInt> getRequireValidationTypes() {
        return requireValidationTypes;
    }

    /**
     * Sets the value of the requireValidationTypes property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfunsignedInt }{@code >}
     *     
     */
    public void setRequireValidationTypes(JAXBElement<ArrayOfunsignedInt> value) {
        this.requireValidationTypes = value;
    }

    /**
     * Gets the value of the matchItemPassKey property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMatchItemPassKey() {
        return matchItemPassKey;
    }

    /**
     * Sets the value of the matchItemPassKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMatchItemPassKey(JAXBElement<String> value) {
        this.matchItemPassKey = value;
    }

    /**
     * Gets the value of the matchItemWarningKey property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMatchItemWarningKey() {
        return matchItemWarningKey;
    }

    /**
     * Sets the value of the matchItemWarningKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMatchItemWarningKey(JAXBElement<String> value) {
        this.matchItemWarningKey = value;
    }

    /**
     * Gets the value of the matchItemFailKey property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMatchItemFailKey() {
        return matchItemFailKey;
    }

    /**
     * Sets the value of the matchItemFailKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMatchItemFailKey(JAXBElement<String> value) {
        this.matchItemFailKey = value;
    }

}
