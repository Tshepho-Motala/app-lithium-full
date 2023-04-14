
package com.id3global.id3gws._2013._04;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GlobalDocumentSubmissionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GlobalDocumentSubmissionType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="None"/&gt;
 *     &lt;enumeration value="Author"/&gt;
 *     &lt;enumeration value="Applicant"/&gt;
 *     &lt;enumeration value="InPossesion"/&gt;
 *     &lt;enumeration value="PostOffice"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "GlobalDocumentSubmissionType")
@XmlEnum
public enum GlobalDocumentSubmissionTypeType {

    @XmlEnumValue("None")
    NONE("None"),
    @XmlEnumValue("Author")
    AUTHOR("Author"),
    @XmlEnumValue("Applicant")
    APPLICANT("Applicant"),
    @XmlEnumValue("InPossesion")
    IN_POSSESION("InPossesion"),
    @XmlEnumValue("PostOffice")
    POST_OFFICE("PostOffice");
    private final String value;

    GlobalDocumentSubmissionTypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GlobalDocumentSubmissionTypeType fromValue(String v) {
        for (GlobalDocumentSubmissionTypeType c: GlobalDocumentSubmissionTypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
