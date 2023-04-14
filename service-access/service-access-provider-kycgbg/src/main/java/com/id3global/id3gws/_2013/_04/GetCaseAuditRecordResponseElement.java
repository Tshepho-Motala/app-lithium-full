
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="GetCaseAuditRecordResult" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalCaseState" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getCaseAuditRecordResult"
})
@XmlRootElement(name = "GetCaseAuditRecordResponse")
public class GetCaseAuditRecordResponseElement {

    @XmlElementRef(name = "GetCaseAuditRecordResult", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalCaseStateType> getCaseAuditRecordResult;

    /**
     * Gets the value of the getCaseAuditRecordResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalCaseStateType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalCaseStateType> getGetCaseAuditRecordResult() {
        return getCaseAuditRecordResult;
    }

    /**
     * Sets the value of the getCaseAuditRecordResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalCaseStateType }{@code >}
     *     
     */
    public void setGetCaseAuditRecordResult(JAXBElement<ArrayOfGlobalCaseStateType> value) {
        this.getCaseAuditRecordResult = value;
    }

}
