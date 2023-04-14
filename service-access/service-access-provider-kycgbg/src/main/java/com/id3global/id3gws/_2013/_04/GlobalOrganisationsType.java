
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalOrganisations complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalOrganisations"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Organisations" type="{http://www.id3global.com/ID3gWS/2013/04}ArrayOfGlobalOrganisation" minOccurs="0"/&gt;
 *         &lt;element name="PageSize" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="TotalPages" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="TotalOrganisations" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="TotalActive" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="TotalExpired" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalOrganisations", propOrder = {
    "organisations",
    "pageSize",
    "totalPages",
    "totalOrganisations",
    "totalActive",
    "totalExpired"
})
public class GlobalOrganisationsType {

    @XmlElementRef(name = "Organisations", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfGlobalOrganisationType> organisations;
    @XmlElement(name = "PageSize")
    @XmlSchemaType(name = "unsignedInt")
    protected Long pageSize;
    @XmlElement(name = "TotalPages")
    @XmlSchemaType(name = "unsignedInt")
    protected Long totalPages;
    @XmlElement(name = "TotalOrganisations")
    @XmlSchemaType(name = "unsignedInt")
    protected Long totalOrganisations;
    @XmlElement(name = "TotalActive")
    @XmlSchemaType(name = "unsignedInt")
    protected Long totalActive;
    @XmlElement(name = "TotalExpired")
    @XmlSchemaType(name = "unsignedInt")
    protected Long totalExpired;

    /**
     * Gets the value of the organisations property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalOrganisationType }{@code >}
     *     
     */
    public JAXBElement<ArrayOfGlobalOrganisationType> getOrganisations() {
        return organisations;
    }

    /**
     * Sets the value of the organisations property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfGlobalOrganisationType }{@code >}
     *     
     */
    public void setOrganisations(JAXBElement<ArrayOfGlobalOrganisationType> value) {
        this.organisations = value;
    }

    /**
     * Gets the value of the pageSize property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPageSize() {
        return pageSize;
    }

    /**
     * Sets the value of the pageSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPageSize(Long value) {
        this.pageSize = value;
    }

    /**
     * Gets the value of the totalPages property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTotalPages() {
        return totalPages;
    }

    /**
     * Sets the value of the totalPages property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTotalPages(Long value) {
        this.totalPages = value;
    }

    /**
     * Gets the value of the totalOrganisations property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTotalOrganisations() {
        return totalOrganisations;
    }

    /**
     * Sets the value of the totalOrganisations property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTotalOrganisations(Long value) {
        this.totalOrganisations = value;
    }

    /**
     * Gets the value of the totalActive property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTotalActive() {
        return totalActive;
    }

    /**
     * Sets the value of the totalActive property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTotalActive(Long value) {
        this.totalActive = value;
    }

    /**
     * Gets the value of the totalExpired property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTotalExpired() {
        return totalExpired;
    }

    /**
     * Sets the value of the totalExpired property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTotalExpired(Long value) {
        this.totalExpired = value;
    }

}
