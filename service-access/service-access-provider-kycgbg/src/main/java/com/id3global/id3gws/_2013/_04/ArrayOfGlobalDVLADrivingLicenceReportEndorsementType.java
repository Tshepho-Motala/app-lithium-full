
package com.id3global.id3gws._2013._04;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfGlobalDVLADrivingLicenceReportEndorsement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfGlobalDVLADrivingLicenceReportEndorsement"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="GlobalDVLADrivingLicenceReportEndorsement" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalDVLADrivingLicenceReportEndorsement" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfGlobalDVLADrivingLicenceReportEndorsement", propOrder = {
    "globalDVLADrivingLicenceReportEndorsement"
})
public class ArrayOfGlobalDVLADrivingLicenceReportEndorsementType {

    @XmlElement(name = "GlobalDVLADrivingLicenceReportEndorsement", nillable = true)
    protected List<GlobalDVLADrivingLicenceReportEndorsementType> globalDVLADrivingLicenceReportEndorsement;

    /**
     * Gets the value of the globalDVLADrivingLicenceReportEndorsement property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the globalDVLADrivingLicenceReportEndorsement property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGlobalDVLADrivingLicenceReportEndorsement().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GlobalDVLADrivingLicenceReportEndorsementType }
     * 
     * 
     */
    public List<GlobalDVLADrivingLicenceReportEndorsementType> getGlobalDVLADrivingLicenceReportEndorsement() {
        if (globalDVLADrivingLicenceReportEndorsement == null) {
            globalDVLADrivingLicenceReportEndorsement = new ArrayList<GlobalDVLADrivingLicenceReportEndorsementType>();
        }
        return this.globalDVLADrivingLicenceReportEndorsement;
    }

}
