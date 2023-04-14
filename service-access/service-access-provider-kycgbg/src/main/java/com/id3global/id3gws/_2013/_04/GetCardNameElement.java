
package com.id3global.id3gws._2013._04;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element name="CardType" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalCardType" minOccurs="0"/&gt;
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
    "cardType"
})
@XmlRootElement(name = "GetCardName")
public class GetCardNameElement {

    @XmlElement(name = "CardType")
    @XmlSchemaType(name = "string")
    protected GlobalCardTypeType cardType;

    /**
     * Gets the value of the cardType property.
     * 
     * @return
     *     possible object is
     *     {@link GlobalCardTypeType }
     *     
     */
    public GlobalCardTypeType getCardType() {
        return cardType;
    }

    /**
     * Sets the value of the cardType property.
     * 
     * @param value
     *     allowed object is
     *     {@link GlobalCardTypeType }
     *     
     */
    public void setCardType(GlobalCardTypeType value) {
        this.cardType = value;
    }

}
