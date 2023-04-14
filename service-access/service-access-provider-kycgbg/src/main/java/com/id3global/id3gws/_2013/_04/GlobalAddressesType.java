
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalAddresses complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalAddresses"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CurrentAddress" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalAddress" minOccurs="0"/&gt;
 *         &lt;element name="PreviousAddress1" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalAddress" minOccurs="0"/&gt;
 *         &lt;element name="PreviousAddress2" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalAddress" minOccurs="0"/&gt;
 *         &lt;element name="PreviousAddress3" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalAddress" minOccurs="0"/&gt;
 *         &lt;element name="HistoricAddresses" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalAddress" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalAddresses", propOrder = {
    "currentAddress",
    "previousAddress1",
    "previousAddress2",
    "previousAddress3",
    "historicAddresses"
})
public class GlobalAddressesType {

    @XmlElementRef(name = "CurrentAddress", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalAddressType> currentAddress;
    @XmlElementRef(name = "PreviousAddress1", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalAddressType> previousAddress1;
    @XmlElementRef(name = "PreviousAddress2", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalAddressType> previousAddress2;
    @XmlElementRef(name = "PreviousAddress3", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalAddressType> previousAddress3;
    @XmlElementRef(name = "HistoricAddresses", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalAddressType> historicAddresses;

    /**
     * Gets the value of the currentAddress property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalAddressType }{@code >}
     *     
     */
    public JAXBElement<GlobalAddressType> getCurrentAddress() {
        return currentAddress;
    }

    /**
     * Sets the value of the currentAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalAddressType }{@code >}
     *     
     */
    public void setCurrentAddress(JAXBElement<GlobalAddressType> value) {
        this.currentAddress = value;
    }

    /**
     * Gets the value of the previousAddress1 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalAddressType }{@code >}
     *     
     */
    public JAXBElement<GlobalAddressType> getPreviousAddress1() {
        return previousAddress1;
    }

    /**
     * Sets the value of the previousAddress1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalAddressType }{@code >}
     *     
     */
    public void setPreviousAddress1(JAXBElement<GlobalAddressType> value) {
        this.previousAddress1 = value;
    }

    /**
     * Gets the value of the previousAddress2 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalAddressType }{@code >}
     *     
     */
    public JAXBElement<GlobalAddressType> getPreviousAddress2() {
        return previousAddress2;
    }

    /**
     * Sets the value of the previousAddress2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalAddressType }{@code >}
     *     
     */
    public void setPreviousAddress2(JAXBElement<GlobalAddressType> value) {
        this.previousAddress2 = value;
    }

    /**
     * Gets the value of the previousAddress3 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalAddressType }{@code >}
     *     
     */
    public JAXBElement<GlobalAddressType> getPreviousAddress3() {
        return previousAddress3;
    }

    /**
     * Sets the value of the previousAddress3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalAddressType }{@code >}
     *     
     */
    public void setPreviousAddress3(JAXBElement<GlobalAddressType> value) {
        this.previousAddress3 = value;
    }

    /**
     * Gets the value of the historicAddresses property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalAddressType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalAddressType> getHistoricAddresses() {
        return historicAddresses;
    }

    /**
     * Sets the value of the historicAddresses property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalAddressType }{@code >}
     *     
     */
    public void setHistoricAddresses(JAXBElement<ArrayOfGlobalAddressType> value) {
        this.historicAddresses = value;
    }

}
