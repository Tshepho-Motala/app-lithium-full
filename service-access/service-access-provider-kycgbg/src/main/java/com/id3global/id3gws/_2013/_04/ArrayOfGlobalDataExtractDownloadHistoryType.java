
package com.id3global.id3gws._2013._04;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfGlobalDataExtractDownloadHistory complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfGlobalDataExtractDownloadHistory"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="GlobalDataExtractDownloadHistory" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalDataExtractDownloadHistory" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfGlobalDataExtractDownloadHistory", propOrder = {
    "globalDataExtractDownloadHistory"
})
public class ArrayOfGlobalDataExtractDownloadHistoryType {

    @XmlElement(name = "GlobalDataExtractDownloadHistory", nillable = true)
    protected List<GlobalDataExtractDownloadHistoryType> globalDataExtractDownloadHistory;

    /**
     * Gets the value of the globalDataExtractDownloadHistory property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the globalDataExtractDownloadHistory property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGlobalDataExtractDownloadHistory().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GlobalDataExtractDownloadHistoryType }
     * 
     * 
     */
    public List<GlobalDataExtractDownloadHistoryType> getGlobalDataExtractDownloadHistory() {
        if (globalDataExtractDownloadHistory == null) {
            globalDataExtractDownloadHistory = new ArrayList<GlobalDataExtractDownloadHistoryType>();
        }
        return this.globalDataExtractDownloadHistory;
    }

}
