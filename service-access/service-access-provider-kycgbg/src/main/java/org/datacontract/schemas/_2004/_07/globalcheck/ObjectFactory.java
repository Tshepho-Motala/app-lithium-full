
package org.datacontract.schemas._2004._07.globalcheck;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.datacontract.schemas._2004._07.globalcheck package. 
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

    private final static QName _ArrayOfGlobalStatus_QNAME = new QName("http://schemas.datacontract.org/2004/07/GlobalCheck.DataLib", "ArrayOfGlobalStatus");
    private final static QName _GlobalStatus_QNAME = new QName("http://schemas.datacontract.org/2004/07/GlobalCheck.DataLib", "GlobalStatus");
    private final static QName _GlobalStatusName_QNAME = new QName("http://schemas.datacontract.org/2004/07/GlobalCheck.DataLib", "Name");
    private final static QName _GlobalStatusRequestText_QNAME = new QName("http://schemas.datacontract.org/2004/07/GlobalCheck.DataLib", "RequestText");
    private final static QName _GlobalStatusResponseText_QNAME = new QName("http://schemas.datacontract.org/2004/07/GlobalCheck.DataLib", "ResponseText");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.datacontract.schemas._2004._07.globalcheck
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ArrayOfGlobalStatus }
     * 
     */
    public ArrayOfGlobalStatus createArrayOfGlobalStatus() {
        return new ArrayOfGlobalStatus();
    }

    /**
     * Create an instance of {@link GlobalStatus }
     * 
     */
    public GlobalStatus createGlobalStatus() {
        return new GlobalStatus();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfGlobalStatus }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ArrayOfGlobalStatus }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/GlobalCheck.DataLib", name = "ArrayOfGlobalStatus")
    public JAXBElement<ArrayOfGlobalStatus> createArrayOfGlobalStatus(ArrayOfGlobalStatus value) {
        return new JAXBElement<ArrayOfGlobalStatus>(_ArrayOfGlobalStatus_QNAME, ArrayOfGlobalStatus.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GlobalStatus }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GlobalStatus }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/GlobalCheck.DataLib", name = "GlobalStatus")
    public JAXBElement<GlobalStatus> createGlobalStatus(GlobalStatus value) {
        return new JAXBElement<GlobalStatus>(_GlobalStatus_QNAME, GlobalStatus.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/GlobalCheck.DataLib", name = "Name", scope = GlobalStatus.class)
    public JAXBElement<String> createGlobalStatusName(String value) {
        return new JAXBElement<String>(_GlobalStatusName_QNAME, String.class, GlobalStatus.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/GlobalCheck.DataLib", name = "RequestText", scope = GlobalStatus.class)
    public JAXBElement<String> createGlobalStatusRequestText(String value) {
        return new JAXBElement<String>(_GlobalStatusRequestText_QNAME, String.class, GlobalStatus.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/GlobalCheck.DataLib", name = "ResponseText", scope = GlobalStatus.class)
    public JAXBElement<String> createGlobalStatusResponseText(String value) {
        return new JAXBElement<String>(_GlobalStatusResponseText_QNAME, String.class, GlobalStatus.class, value);
    }

}
