
package com.id3global.id3gws._2013._04;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ImageChecks complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ImageChecks"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Blur" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Dark" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Dpi" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Entropy" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Size" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Overall" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="DocumentHasMrz" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="MrzVisible" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Coloured" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Expired" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="MrzBlur" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Reduced" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Obliquity" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="MrzReflection" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Reflection" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ImageChecks", propOrder = {
    "blur",
    "dark",
    "dpi",
    "entropy",
    "size",
    "overall",
    "documentHasMrz",
    "mrzVisible",
    "coloured",
    "expired",
    "mrzBlur",
    "reduced",
    "obliquity",
    "mrzReflection",
    "reflection"
})
public class ImageChecksType {

    @XmlElement(name = "Blur")
    protected Boolean blur;
    @XmlElement(name = "Dark")
    protected Boolean dark;
    @XmlElement(name = "Dpi")
    protected Boolean dpi;
    @XmlElement(name = "Entropy")
    protected Boolean entropy;
    @XmlElement(name = "Size")
    protected Boolean size;
    @XmlElement(name = "Overall")
    protected Boolean overall;
    @XmlElement(name = "DocumentHasMrz")
    protected Boolean documentHasMrz;
    @XmlElement(name = "MrzVisible")
    protected Boolean mrzVisible;
    @XmlElement(name = "Coloured")
    protected Boolean coloured;
    @XmlElement(name = "Expired")
    protected Boolean expired;
    @XmlElement(name = "MrzBlur")
    protected Boolean mrzBlur;
    @XmlElement(name = "Reduced")
    protected Boolean reduced;
    @XmlElement(name = "Obliquity")
    protected Boolean obliquity;
    @XmlElement(name = "MrzReflection")
    protected Boolean mrzReflection;
    @XmlElement(name = "Reflection")
    protected Boolean reflection;

    /**
     * Gets the value of the blur property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBlur() {
        return blur;
    }

    /**
     * Sets the value of the blur property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBlur(Boolean value) {
        this.blur = value;
    }

    /**
     * Gets the value of the dark property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDark() {
        return dark;
    }

    /**
     * Sets the value of the dark property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDark(Boolean value) {
        this.dark = value;
    }

    /**
     * Gets the value of the dpi property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDpi() {
        return dpi;
    }

    /**
     * Sets the value of the dpi property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDpi(Boolean value) {
        this.dpi = value;
    }

    /**
     * Gets the value of the entropy property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEntropy() {
        return entropy;
    }

    /**
     * Sets the value of the entropy property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEntropy(Boolean value) {
        this.entropy = value;
    }

    /**
     * Gets the value of the size property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSize() {
        return size;
    }

    /**
     * Sets the value of the size property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSize(Boolean value) {
        this.size = value;
    }

    /**
     * Gets the value of the overall property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOverall() {
        return overall;
    }

    /**
     * Sets the value of the overall property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOverall(Boolean value) {
        this.overall = value;
    }

    /**
     * Gets the value of the documentHasMrz property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDocumentHasMrz() {
        return documentHasMrz;
    }

    /**
     * Sets the value of the documentHasMrz property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDocumentHasMrz(Boolean value) {
        this.documentHasMrz = value;
    }

    /**
     * Gets the value of the mrzVisible property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMrzVisible() {
        return mrzVisible;
    }

    /**
     * Sets the value of the mrzVisible property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMrzVisible(Boolean value) {
        this.mrzVisible = value;
    }

    /**
     * Gets the value of the coloured property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isColoured() {
        return coloured;
    }

    /**
     * Sets the value of the coloured property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setColoured(Boolean value) {
        this.coloured = value;
    }

    /**
     * Gets the value of the expired property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExpired() {
        return expired;
    }

    /**
     * Sets the value of the expired property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExpired(Boolean value) {
        this.expired = value;
    }

    /**
     * Gets the value of the mrzBlur property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMrzBlur() {
        return mrzBlur;
    }

    /**
     * Sets the value of the mrzBlur property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMrzBlur(Boolean value) {
        this.mrzBlur = value;
    }

    /**
     * Gets the value of the reduced property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReduced() {
        return reduced;
    }

    /**
     * Sets the value of the reduced property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReduced(Boolean value) {
        this.reduced = value;
    }

    /**
     * Gets the value of the obliquity property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isObliquity() {
        return obliquity;
    }

    /**
     * Sets the value of the obliquity property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setObliquity(Boolean value) {
        this.obliquity = value;
    }

    /**
     * Gets the value of the mrzReflection property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMrzReflection() {
        return mrzReflection;
    }

    /**
     * Sets the value of the mrzReflection property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMrzReflection(Boolean value) {
        this.mrzReflection = value;
    }

    /**
     * Gets the value of the reflection property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReflection() {
        return reflection;
    }

    /**
     * Sets the value of the reflection property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReflection(Boolean value) {
        this.reflection = value;
    }

}
