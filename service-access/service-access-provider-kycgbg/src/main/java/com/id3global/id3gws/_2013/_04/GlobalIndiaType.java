
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalIndia complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalIndia"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PAN" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalIndiaPAN" minOccurs="0"/&gt;
 *         &lt;element name="DrivingLicence" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalIndiaDrivingLicence" minOccurs="0"/&gt;
 *         &lt;element name="Epic" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalIndiaEpic" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalIndia", propOrder = {
    "pan",
    "drivingLicence",
    "epic"
})
public class GlobalIndiaType {

    @XmlElementRef(name = "PAN", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalIndiaPANType> pan;
    @XmlElementRef(name = "DrivingLicence", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalIndiaDrivingLicenceType> drivingLicence;
    @XmlElementRef(name = "Epic", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalIndiaEpicType> epic;

    /**
     * Gets the value of the pan property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalIndiaPANType }{@code >}
     *     
     */
    public JAXBElement<GlobalIndiaPANType> getPAN() {
        return pan;
    }

    /**
     * Sets the value of the pan property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalIndiaPANType }{@code >}
     *     
     */
    public void setPAN(JAXBElement<GlobalIndiaPANType> value) {
        this.pan = value;
    }

    /**
     * Gets the value of the drivingLicence property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalIndiaDrivingLicenceType }{@code >}
     *     
     */
    public JAXBElement<GlobalIndiaDrivingLicenceType> getDrivingLicence() {
        return drivingLicence;
    }

    /**
     * Sets the value of the drivingLicence property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalIndiaDrivingLicenceType }{@code >}
     *     
     */
    public void setDrivingLicence(JAXBElement<GlobalIndiaDrivingLicenceType> value) {
        this.drivingLicence = value;
    }

    /**
     * Gets the value of the epic property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalIndiaEpicType }{@code >}
     *     
     */
    public JAXBElement<GlobalIndiaEpicType> getEpic() {
        return epic;
    }

    /**
     * Sets the value of the epic property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalIndiaEpicType }{@code >}
     *     
     */
    public void setEpic(JAXBElement<GlobalIndiaEpicType> value) {
        this.epic = value;
    }

}
