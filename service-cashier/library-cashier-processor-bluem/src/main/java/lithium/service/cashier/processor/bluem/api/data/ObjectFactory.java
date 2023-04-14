
package lithium.service.cashier.processor.bluem.api.data;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the lithium.service.cashier.processor.bluem.api.data package. 
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

    private final static QName _EPaymentInterface_QNAME = new QName("", "EPaymentInterface");
    private final static QName _DynamicDataComplexTypeDynamicElement_QNAME = new QName("", "DynamicElement");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: lithium.service.cashier.processor.bluem.api.data
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EPaymentInterfaceType }
     * 
     */
    public EPaymentInterfaceType createEPaymentInterfaceType() {
        return new EPaymentInterfaceType();
    }

    /**
     * Create an instance of {@link DebtorWalletComplexType }
     * 
     */
    public DebtorWalletComplexType createDebtorWalletComplexType() {
        return new DebtorWalletComplexType();
    }

    /**
     * Create an instance of {@link VisaMasterDetailsComplexType }
     * 
     */
    public VisaMasterDetailsComplexType createVisaMasterDetailsComplexType() {
        return new VisaMasterDetailsComplexType();
    }

    /**
     * Create an instance of {@link DebtorReturnURLComplexType }
     * 
     */
    public DebtorReturnURLComplexType createDebtorReturnURLComplexType() {
        return new DebtorReturnURLComplexType();
    }

    /**
     * Create an instance of {@link DebtorCreditCardComplexType }
     * 
     */
    public DebtorCreditCardComplexType createDebtorCreditCardComplexType() {
        return new DebtorCreditCardComplexType();
    }

    /**
     * Create an instance of {@link EPaymentTransactionRequestType }
     * 
     */
    public EPaymentTransactionRequestType createEPaymentTransactionRequestType() {
        return new EPaymentTransactionRequestType();
    }

    /**
     * Create an instance of {@link PayPalComplexType }
     * 
     */
    public PayPalComplexType createPayPalComplexType() {
        return new PayPalComplexType();
    }

    /**
     * Create an instance of {@link PayPalDetailsComplexType }
     * 
     */
    public PayPalDetailsComplexType createPayPalDetailsComplexType() {
        return new PayPalDetailsComplexType();
    }

    /**
     * Create an instance of {@link CreditCardExpirationDateComplexType }
     * 
     */
    public CreditCardExpirationDateComplexType createCreditCardExpirationDateComplexType() {
        return new CreditCardExpirationDateComplexType();
    }

    /**
     * Create an instance of {@link DynamicElementComplexType }
     * 
     */
    public DynamicElementComplexType createDynamicElementComplexType() {
        return new DynamicElementComplexType();
    }

    /**
     * Create an instance of {@link DebtorAdditionalDataComplexType }
     * 
     */
    public DebtorAdditionalDataComplexType createDebtorAdditionalDataComplexType() {
        return new DebtorAdditionalDataComplexType();
    }

    /**
     * Create an instance of {@link EPaymentBatchResponseType }
     * 
     */
    public EPaymentBatchResponseType createEPaymentBatchResponseType() {
        return new EPaymentBatchResponseType();
    }

    /**
     * Create an instance of {@link EPaymentTransactionResponseType }
     * 
     */
    public EPaymentTransactionResponseType createEPaymentTransactionResponseType() {
        return new EPaymentTransactionResponseType();
    }

    /**
     * Create an instance of {@link PaymentErrorResponseType }
     * 
     */
    public PaymentErrorResponseType createPaymentErrorResponseType() {
        return new PaymentErrorResponseType();
    }

    /**
     * Create an instance of {@link DocDataComplexType }
     * 
     */
    public DocDataComplexType createDocDataComplexType() {
        return new DocDataComplexType();
    }

    /**
     * Create an instance of {@link EPaymentStatusUpdateType }
     * 
     */
    public EPaymentStatusUpdateType createEPaymentStatusUpdateType() {
        return new EPaymentStatusUpdateType();
    }

    /**
     * Create an instance of {@link AmountOptionComplexType }
     * 
     */
    public AmountOptionComplexType createAmountOptionComplexType() {
        return new AmountOptionComplexType();
    }

    /**
     * Create an instance of {@link IDealDetailsComplexType }
     * 
     */
    public IDealDetailsComplexType createIDealDetailsComplexType() {
        return new IDealDetailsComplexType();
    }

    /**
     * Create an instance of {@link ErrorComplexType }
     * 
     */
    public ErrorComplexType createErrorComplexType() {
        return new ErrorComplexType();
    }

    /**
     * Create an instance of {@link DocComplexType }
     * 
     */
    public DocComplexType createDocComplexType() {
        return new DocComplexType();
    }

    /**
     * Create an instance of {@link DynamicDataComplexType }
     * 
     */
    public DynamicDataComplexType createDynamicDataComplexType() {
        return new DynamicDataComplexType();
    }

    /**
     * Create an instance of {@link EPaymentStatusRequestType }
     * 
     */
    public EPaymentStatusRequestType createEPaymentStatusRequestType() {
        return new EPaymentStatusRequestType();
    }

    /**
     * Create an instance of {@link AmountArrayComplexType }
     * 
     */
    public AmountArrayComplexType createAmountArrayComplexType() {
        return new AmountArrayComplexType();
    }

    /**
     * Create an instance of {@link IDealComplexType }
     * 
     */
    public IDealComplexType createIDealComplexType() {
        return new IDealComplexType();
    }

    /**
     * Create an instance of {@link PaymentMethodDetailsComplexType }
     * 
     */
    public PaymentMethodDetailsComplexType createPaymentMethodDetailsComplexType() {
        return new PaymentMethodDetailsComplexType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EPaymentInterfaceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "EPaymentInterface")
    public JAXBElement<EPaymentInterfaceType> createEPaymentInterface(EPaymentInterfaceType value) {
        return new JAXBElement<EPaymentInterfaceType>(_EPaymentInterface_QNAME, EPaymentInterfaceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DynamicElementComplexType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "DynamicElement", scope = DynamicDataComplexType.class)
    public JAXBElement<DynamicElementComplexType> createDynamicDataComplexTypeDynamicElement(DynamicElementComplexType value) {
        return new JAXBElement<DynamicElementComplexType>(_DynamicDataComplexTypeDynamicElement_QNAME, DynamicElementComplexType.class, DynamicDataComplexType.class, value);
    }

}
