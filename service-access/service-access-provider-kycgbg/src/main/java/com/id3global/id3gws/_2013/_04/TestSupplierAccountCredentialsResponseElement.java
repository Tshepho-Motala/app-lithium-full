
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
 *         &lt;element name="TestSupplierAccountCredentialsResult" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalSupplierAccount" minOccurs="0"/&gt;
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
    "testSupplierAccountCredentialsResult"
})
@XmlRootElement(name = "TestSupplierAccountCredentialsResponse")
public class TestSupplierAccountCredentialsResponseElement {

    @XmlElementRef(name = "TestSupplierAccountCredentialsResult", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalSupplierAccountType> testSupplierAccountCredentialsResult;

    /**
     * Gets the value of the testSupplierAccountCredentialsResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalSupplierAccountType }{@code >}
     *     
     */
    public JAXBElement<GlobalSupplierAccountType> getTestSupplierAccountCredentialsResult() {
        return testSupplierAccountCredentialsResult;
    }

    /**
     * Sets the value of the testSupplierAccountCredentialsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalSupplierAccountType }{@code >}
     *     
     */
    public void setTestSupplierAccountCredentialsResult(JAXBElement<GlobalSupplierAccountType> value) {
        this.testSupplierAccountCredentialsResult = value;
    }

}
