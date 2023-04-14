
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalExternalDataResultParameters complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalExternalDataResultParameters"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.id3global.com/ID3gWS/2013/04}GlobalCaseResultParameters"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="OverallMatchItemKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Parameters" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalExternalDataResultParameter" minOccurs="0"/&gt;
 *         &lt;element name="ExternalDataIds" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalKeyValuePairOfstringint" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalExternalDataResultParameters", propOrder = {
    "overallMatchItemKey",
    "parameters",
    "externalDataIds"
})
public class GlobalExternalDataResultParametersType
    extends GlobalCaseResultParametersType
{

    @XmlElementRef(name = "OverallMatchItemKey", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> overallMatchItemKey;
    @XmlElementRef(name = "Parameters", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalExternalDataResultParameterType> parameters;
    @XmlElementRef(name = "ExternalDataIds", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalKeyValuePairOfstringintType> externalDataIds;

    /**
     * Gets the value of the overallMatchItemKey property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getOverallMatchItemKey() {
        return overallMatchItemKey;
    }

    /**
     * Sets the value of the overallMatchItemKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setOverallMatchItemKey(JAXBElement<String> value) {
        this.overallMatchItemKey = value;
    }

    /**
     * Gets the value of the parameters property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalExternalDataResultParameterType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalExternalDataResultParameterType> getParameters() {
        return parameters;
    }

    /**
     * Sets the value of the parameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalExternalDataResultParameterType }{@code >}
     *     
     */
    public void setParameters(JAXBElement<ArrayOfGlobalExternalDataResultParameterType> value) {
        this.parameters = value;
    }

    /**
     * Gets the value of the externalDataIds property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalKeyValuePairOfstringintType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalKeyValuePairOfstringintType> getExternalDataIds() {
        return externalDataIds;
    }

    /**
     * Sets the value of the externalDataIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalKeyValuePairOfstringintType }{@code >}
     *     
     */
    public void setExternalDataIds(JAXBElement<ArrayOfGlobalKeyValuePairOfstringintType> value) {
        this.externalDataIds = value;
    }

}
