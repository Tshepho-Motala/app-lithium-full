
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalNewZealand complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalNewZealand"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DrivingLicence" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalNewZealandDrivingLicence" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalNewZealand", propOrder = {
    "drivingLicence"
})
public class GlobalNewZealandType {

    @XmlElementRef(name = "DrivingLicence", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalNewZealandDrivingLicenceType> drivingLicence;

    /**
     * Gets the value of the drivingLicence property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalNewZealandDrivingLicenceType }{@code >}
     *     
     */
    public JAXBElement<GlobalNewZealandDrivingLicenceType> getDrivingLicence() {
        return drivingLicence;
    }

    /**
     * Sets the value of the drivingLicence property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalNewZealandDrivingLicenceType }{@code >}
     *     
     */
    public void setDrivingLicence(JAXBElement<GlobalNewZealandDrivingLicenceType> value) {
        this.drivingLicence = value;
    }

}
