
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalCaseDispatchParameters complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalCaseDispatchParameters"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.id3global.com/ID3gWS/2013/04}GlobalCaseResultParameters"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ServiceType" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalCaseDispatchServiceType" minOccurs="0"/&gt;
 *         &lt;element name="ServiceAgreementID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="RejectStatus" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="ResendStatus" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="ConsentStatus" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalCaseDispatchParameters", propOrder = {
    "serviceType",
    "serviceAgreementID",
    "rejectStatus",
    "resendStatus",
    "consentStatus"
})
public class GlobalCaseDispatchParametersType
    extends GlobalCaseResultParametersType
{

    @XmlElement(name = "ServiceType")
    @XmlSchemaType(name = "string")
    protected GlobalCaseDispatchServiceTypeType serviceType;
    @XmlElementRef(name = "ServiceAgreementID", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<String> serviceAgreementID;
    @XmlElementRef(name = "RejectStatus", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<Long> rejectStatus;
    @XmlElementRef(name = "ResendStatus", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<Long> resendStatus;
    @XmlElementRef(name = "ConsentStatus", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<Long> consentStatus;

    /**
     * Gets the value of the serviceType property.
     * 
     * @return
     *     possible object is
     *     {@link GlobalCaseDispatchServiceTypeType }
     *     
     */
    public GlobalCaseDispatchServiceTypeType getServiceType() {
        return serviceType;
    }

    /**
     * Sets the value of the serviceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link GlobalCaseDispatchServiceTypeType }
     *     
     */
    public void setServiceType(GlobalCaseDispatchServiceTypeType value) {
        this.serviceType = value;
    }

    /**
     * Gets the value of the serviceAgreementID property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getServiceAgreementID() {
        return serviceAgreementID;
    }

    /**
     * Sets the value of the serviceAgreementID property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setServiceAgreementID(JAXBElement<String> value) {
        this.serviceAgreementID = value;
    }

    /**
     * Gets the value of the rejectStatus property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public JAXBElement<Long> getRejectStatus() {
        return rejectStatus;
    }

    /**
     * Sets the value of the rejectStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public void setRejectStatus(JAXBElement<Long> value) {
        this.rejectStatus = value;
    }

    /**
     * Gets the value of the resendStatus property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public JAXBElement<Long> getResendStatus() {
        return resendStatus;
    }

    /**
     * Sets the value of the resendStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public void setResendStatus(JAXBElement<Long> value) {
        this.resendStatus = value;
    }

    /**
     * Gets the value of the consentStatus property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public JAXBElement<Long> getConsentStatus() {
        return consentStatus;
    }

    /**
     * Sets the value of the consentStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public void setConsentStatus(JAXBElement<Long> value) {
        this.consentStatus = value;
    }

}
