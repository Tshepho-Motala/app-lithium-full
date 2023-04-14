
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalBankingDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalBankingDetails"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="BankAccount" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalBankAccount" minOccurs="0"/&gt;
 *         &lt;element name="CreditDebitCard" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalCreditDebitCard" minOccurs="0"/&gt;
 *         &lt;element name="China" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalChinaBank" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalBankingDetails", propOrder = {
    "bankAccount",
    "creditDebitCard",
    "china"
})
public class GlobalBankingDetailsType {

    @XmlElementRef(name = "BankAccount", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalBankAccountType> bankAccount;
    @XmlElementRef(name = "CreditDebitCard", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalCreditDebitCardType> creditDebitCard;
    @XmlElementRef(name = "China", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalChinaBankType> china;

    /**
     * Gets the value of the bankAccount property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalBankAccountType }{@code >}
     *     
     */
    public JAXBElement<GlobalBankAccountType> getBankAccount() {
        return bankAccount;
    }

    /**
     * Sets the value of the bankAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalBankAccountType }{@code >}
     *     
     */
    public void setBankAccount(JAXBElement<GlobalBankAccountType> value) {
        this.bankAccount = value;
    }

    /**
     * Gets the value of the creditDebitCard property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalCreditDebitCardType }{@code >}
     *     
     */
    public JAXBElement<GlobalCreditDebitCardType> getCreditDebitCard() {
        return creditDebitCard;
    }

    /**
     * Sets the value of the creditDebitCard property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalCreditDebitCardType }{@code >}
     *     
     */
    public void setCreditDebitCard(JAXBElement<GlobalCreditDebitCardType> value) {
        this.creditDebitCard = value;
    }

    /**
     * Gets the value of the china property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalChinaBankType }{@code >}
     *     
     */
    public JAXBElement<GlobalChinaBankType> getChina() {
        return china;
    }

    /**
     * Sets the value of the china property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalChinaBankType }{@code >}
     *     
     */
    public void setChina(JAXBElement<GlobalChinaBankType> value) {
        this.china = value;
    }

}
