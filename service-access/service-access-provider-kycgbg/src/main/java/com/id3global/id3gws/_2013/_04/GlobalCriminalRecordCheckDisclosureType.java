
package com.id3global.id3gws._2013._04;

import java.util.Date;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter1;


/**
 * <p>Java class for GlobalCriminalRecordCheckDisclosure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalCriminalRecordCheckDisclosure"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DisclosureDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="Reference" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AdditionalInformation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Status" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="CriminalRecordCheckDisclosureOffences" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalCriminalRecordCheckDisclosureOffence" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalCriminalRecordCheckDisclosure", propOrder = {
    "disclosureDate",
    "reference",
    "additionalInformation",
    "status",
    "criminalRecordCheckDisclosureOffences"
})
public class GlobalCriminalRecordCheckDisclosureType {

    @XmlElement(name = "DisclosureDate", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date disclosureDate;
    @XmlElementRef(name = "Reference", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> reference;
    @XmlElementRef(name = "AdditionalInformation", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> additionalInformation;
    @XmlElement(name = "Status")
    protected Integer status;
    @XmlElementRef(name = "CriminalRecordCheckDisclosureOffences", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalCriminalRecordCheckDisclosureOffenceType> criminalRecordCheckDisclosureOffences;

    /**
     * Gets the value of the disclosureDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getDisclosureDate() {
        return disclosureDate;
    }

    /**
     * Sets the value of the disclosureDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisclosureDate(Date value) {
        this.disclosureDate = value;
    }

    /**
     * Gets the value of the reference property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getReference() {
        return reference;
    }

    /**
     * Sets the value of the reference property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setReference(JAXBElement<String> value) {
        this.reference = value;
    }

    /**
     * Gets the value of the additionalInformation property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAdditionalInformation() {
        return additionalInformation;
    }

    /**
     * Sets the value of the additionalInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAdditionalInformation(JAXBElement<String> value) {
        this.additionalInformation = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setStatus(Integer value) {
        this.status = value;
    }

    /**
     * Gets the value of the criminalRecordCheckDisclosureOffences property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalCriminalRecordCheckDisclosureOffenceType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalCriminalRecordCheckDisclosureOffenceType> getCriminalRecordCheckDisclosureOffences() {
        return criminalRecordCheckDisclosureOffences;
    }

    /**
     * Sets the value of the criminalRecordCheckDisclosureOffences property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalCriminalRecordCheckDisclosureOffenceType }{@code >}
     *     
     */
    public void setCriminalRecordCheckDisclosureOffences(JAXBElement<ArrayOfGlobalCriminalRecordCheckDisclosureOffenceType> value) {
        this.criminalRecordCheckDisclosureOffences = value;
    }

}
