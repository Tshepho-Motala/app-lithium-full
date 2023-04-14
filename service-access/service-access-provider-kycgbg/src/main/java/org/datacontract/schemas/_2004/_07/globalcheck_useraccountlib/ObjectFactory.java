
package org.datacontract.schemas._2004._07.globalcheck_useraccountlib;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.datacontract.schemas._2004._07.globalcheck_useraccountlib package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _EnumsUserAccountType_QNAME = new QName("http://schemas.datacontract.org/2004/07/GlobalCheck.UserAccountLib.Common", "Enums.UserAccountType");
    private final static QName _EnumsFormat_QNAME = new QName("http://schemas.datacontract.org/2004/07/GlobalCheck.UserAccountLib.Common", "Enums.Format");
    private final static QName _EnumsState_QNAME = new QName("http://schemas.datacontract.org/2004/07/GlobalCheck.UserAccountLib.Common", "Enums.State");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.datacontract.schemas._2004._07.globalcheck_useraccountlib
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EnumsUserAccountType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link EnumsUserAccountType }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/GlobalCheck.UserAccountLib.Common", name = "Enums.UserAccountType")
    public JAXBElement<EnumsUserAccountType> createEnumsUserAccountType(EnumsUserAccountType value) {
        return new JAXBElement<EnumsUserAccountType>(_EnumsUserAccountType_QNAME, EnumsUserAccountType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EnumsFormat }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link EnumsFormat }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/GlobalCheck.UserAccountLib.Common", name = "Enums.Format")
    public JAXBElement<EnumsFormat> createEnumsFormat(EnumsFormat value) {
        return new JAXBElement<EnumsFormat>(_EnumsFormat_QNAME, EnumsFormat.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EnumsState }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link EnumsState }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/GlobalCheck.UserAccountLib.Common", name = "Enums.State")
    public JAXBElement<EnumsState> createEnumsState(EnumsState value) {
        return new JAXBElement<EnumsState>(_EnumsState_QNAME, EnumsState.class, null, value);
    }

}
