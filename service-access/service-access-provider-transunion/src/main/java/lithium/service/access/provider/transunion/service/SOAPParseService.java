package lithium.service.access.provider.transunion.service;

import lithium.service.access.provider.transunion.shema.response.fault.FaultResponse;
import lithium.service.access.provider.transunion.shema.response.fault.MainTUResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.dom.DOMSource;
import java.io.StringReader;


@Service
@Slf4j
public class SOAPParseService {

    @Autowired
    Jaxb2Marshaller jaxb2Marshaller;

    @SuppressWarnings("unchecked")
    public <T> T unmarshallSuccess(SOAPMessage soapMessage, Class<T> type)
            throws XmlMappingException, SOAPException {
        T jaxbElement = (T) jaxb2Marshaller.unmarshal(
                new DOMSource(soapMessage.getSOAPBody().extractContentAsDocument()));
        return jaxbElement;
    }

    public FaultResponse unmarshallFault(String soapMessage)
            throws XmlMappingException, SOAPException {

        StringReader reader = new StringReader(soapMessage);
        FaultResponse response = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(MainTUResponse.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            MainTUResponse result = (MainTUResponse) jaxbUnmarshaller.unmarshal(reader);
            if (result.getEnvelopeBody() != null && result.getEnvelopeBody().getFaultResponse()!=null) {
                return result.getEnvelopeBody().getFaultResponse();
            }
        } catch (JAXBException e) {
            log.debug("Transunion can't unmarshall response in Fault format" + e.getMessage());
        }
        return response;
    }
}
