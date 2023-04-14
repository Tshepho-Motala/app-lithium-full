
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalDispatchResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalDispatchResult"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DVLADrivingLicenceReport" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalDVLADrivingLicenceReport" minOccurs="0"/&gt;
 *         &lt;element name="CriminalRecordCheckDisclosure" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalCriminalRecordCheckDisclosure" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalDispatchResult", propOrder = {
    "dvlaDrivingLicenceReport",
    "criminalRecordCheckDisclosure"
})
public class GlobalDispatchResultType {

    @XmlElementRef(name = "DVLADrivingLicenceReport", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalDVLADrivingLicenceReportType> dvlaDrivingLicenceReport;
    @XmlElementRef(name = "CriminalRecordCheckDisclosure", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalCriminalRecordCheckDisclosureType> criminalRecordCheckDisclosure;

    /**
     * Gets the value of the dvlaDrivingLicenceReport property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalDVLADrivingLicenceReportType }{@code >}
     *     
     */
    public JAXBElement<GlobalDVLADrivingLicenceReportType> getDVLADrivingLicenceReport() {
        return dvlaDrivingLicenceReport;
    }

    /**
     * Sets the value of the dvlaDrivingLicenceReport property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalDVLADrivingLicenceReportType }{@code >}
     *     
     */
    public void setDVLADrivingLicenceReport(JAXBElement<GlobalDVLADrivingLicenceReportType> value) {
        this.dvlaDrivingLicenceReport = value;
    }

    /**
     * Gets the value of the criminalRecordCheckDisclosure property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalCriminalRecordCheckDisclosureType }{@code >}
     *     
     */
    public JAXBElement<GlobalCriminalRecordCheckDisclosureType> getCriminalRecordCheckDisclosure() {
        return criminalRecordCheckDisclosure;
    }

    /**
     * Sets the value of the criminalRecordCheckDisclosure property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalCriminalRecordCheckDisclosureType }{@code >}
     *     
     */
    public void setCriminalRecordCheckDisclosure(JAXBElement<GlobalCriminalRecordCheckDisclosureType> value) {
        this.criminalRecordCheckDisclosure = value;
    }

}
