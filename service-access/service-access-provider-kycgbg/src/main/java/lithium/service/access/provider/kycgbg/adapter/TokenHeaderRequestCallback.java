package lithium.service.access.provider.kycgbg.adapter;


import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
public class TokenHeaderRequestCallback implements WebServiceMessageCallback {

   

    private String username;
    private String password;

    public TokenHeaderRequestCallback(String username, String password){
        this.username = username;
        this.password = password;
    }
    
    @Override
    public void doWithMessage(WebServiceMessage message) throws IOException {

        try {
    		Assert.isInstanceOf(SoapMessage.class, message);

        	SoapMessage soapmessage = (SoapMessage) message;
    		soapmessage.setSoapAction("http://www.id3global.com/ID3gWS/2013/04/IGlobalAuthenticate/AuthenticateSP");
    		ByteArrayOutputStream out = new ByteArrayOutputStream();
    		soapmessage.writeTo(out);
    		String strMsg = new String(out.toByteArray());
            log.debug("SOAP Message prepared to sent " + strMsg);
    		
            SaajSoapMessage saajSoapMessage = (SaajSoapMessage)message;
            SOAPMessage soapMessage = saajSoapMessage.getSaajMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
            SOAPHeader soapHeader = soapEnvelope.getHeader();

            Name headerElementName = soapEnvelope.createName(
                    "Security",
                    "wsse",
                    "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"
            );
            SOAPHeaderElement soapHeaderElement = soapHeader.addHeaderElement(headerElementName);
            SOAPElement usernameTokenSOAPElement = soapHeaderElement.addChildElement("UsernameToken", "wsse");
            SOAPElement userNameSOAPElement = usernameTokenSOAPElement.addChildElement("Username", "wsse");
            log.debug(this.username);
            userNameSOAPElement.addTextNode(this.username);
            SOAPElement passwordSOAPElement = usernameTokenSOAPElement.addChildElement("Password", "wsse");
            log.debug(this.password);
            passwordSOAPElement.addTextNode(this.password);
            soapMessage.saveChanges();
     
        } catch (SOAPException soapException) {
            throw new RuntimeException("TokenHeaderRequestCallback123", soapException);
        }
    }
}