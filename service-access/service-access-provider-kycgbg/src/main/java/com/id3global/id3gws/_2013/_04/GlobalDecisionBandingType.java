
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfunsignedInt;


/**
 * <p>Java class for GlobalDecisionBanding complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalDecisionBanding"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ExceptionText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Bands" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalBand" minOccurs="0"/&gt;
 *         &lt;element name="ExceptionPass" type="{http://schemas.microsoft.com/2003/10/Serialization/Arrays}ArrayOfunsignedInt" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalDecisionBanding", propOrder = {
    "exceptionText",
    "bands",
    "exceptionPass"
})
public class GlobalDecisionBandingType {

    @XmlElementRef(name = "ExceptionText", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> exceptionText;
    @XmlElementRef(name = "Bands", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalBandType> bands;
    @XmlElementRef(name = "ExceptionPass", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfunsignedInt> exceptionPass;

    /**
     * Gets the value of the exceptionText property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getExceptionText() {
        return exceptionText;
    }

    /**
     * Sets the value of the exceptionText property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setExceptionText(JAXBElement<String> value) {
        this.exceptionText = value;
    }

    /**
     * Gets the value of the bands property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalBandType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalBandType> getBands() {
        return bands;
    }

    /**
     * Sets the value of the bands property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalBandType }{@code >}
     *     
     */
    public void setBands(JAXBElement<ArrayOfGlobalBandType> value) {
        this.bands = value;
    }

    /**
     * Gets the value of the exceptionPass property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfunsignedInt }{@code >}
     *     
     */
    public JAXBElement<ArrayOfunsignedInt> getExceptionPass() {
        return exceptionPass;
    }

    /**
     * Sets the value of the exceptionPass property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfunsignedInt }{@code >}
     *     
     */
    public void setExceptionPass(JAXBElement<ArrayOfunsignedInt> value) {
        this.exceptionPass = value;
    }

}
