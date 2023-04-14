
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalEmployment complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalEmployment"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ResidenceType" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalResidenceType" minOccurs="0"/&gt;
 *         &lt;element name="EmploymentStatus" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalEmploymentStatus" minOccurs="0"/&gt;
 *         &lt;element name="CurrentTime" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalCurrentTime" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalEmployment", propOrder = {
    "residenceType",
    "employmentStatus",
    "currentTime"
})
public class GlobalEmploymentType {

    @XmlElementRef(name = "ResidenceType", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalResidenceTypeType> residenceType;
    @XmlElementRef(name = "EmploymentStatus", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalEmploymentStatusType> employmentStatus;
    @XmlElementRef(name = "CurrentTime", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalCurrentTimeType> currentTime;

    /**
     * Gets the value of the residenceType property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalResidenceTypeType }{@code >}
     *     
     */
    public JAXBElement<GlobalResidenceTypeType> getResidenceType() {
        return residenceType;
    }

    /**
     * Sets the value of the residenceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalResidenceTypeType }{@code >}
     *     
     */
    public void setResidenceType(JAXBElement<GlobalResidenceTypeType> value) {
        this.residenceType = value;
    }

    /**
     * Gets the value of the employmentStatus property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalEmploymentStatusType }{@code >}
     *     
     */
    public JAXBElement<GlobalEmploymentStatusType> getEmploymentStatus() {
        return employmentStatus;
    }

    /**
     * Sets the value of the employmentStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalEmploymentStatusType }{@code >}
     *     
     */
    public void setEmploymentStatus(JAXBElement<GlobalEmploymentStatusType> value) {
        this.employmentStatus = value;
    }

    /**
     * Gets the value of the currentTime property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalCurrentTimeType }{@code >}
     *     
     */
    public JAXBElement<GlobalCurrentTimeType> getCurrentTime() {
        return currentTime;
    }

    /**
     * Sets the value of the currentTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalCurrentTimeType }{@code >}
     *     
     */
    public void setCurrentTime(JAXBElement<GlobalCurrentTimeType> value) {
        this.currentTime = value;
    }

}
