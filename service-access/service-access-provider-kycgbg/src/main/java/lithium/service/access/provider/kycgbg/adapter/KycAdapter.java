package lithium.service.access.provider.kycgbg.adapter;

import com.id3global.id3gws._2013._04.AuthenticateSPElement;
import com.id3global.id3gws._2013._04.AuthenticateSPResponseElement;
import com.id3global.id3gws._2013._04.CheckCredentialsElement;
import com.id3global.id3gws._2013._04.CheckCredentialsResponseElement;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import java.net.SocketTimeoutException;

@Data
@Slf4j
@Builder
public class KycAdapter { // extends WebServiceGatewaySupport {
	private WebServiceTemplate webServiceTemplate;

	public CheckCredentialsResponseElement checkCredentials(
		String url,
		CheckCredentialsElement checkCredentialsElement
	) throws SocketTimeoutException {
		log.debug("checkCredentials");
		return (CheckCredentialsResponseElement) webServiceTemplate.marshalSendAndReceive(
			url,
			checkCredentialsElement,
			new SoapActionCallback("http://www.id3global.com/ID3gWS/2013/04/IGlobalCredentials/CheckCredentials")
		);
		// if the soapactioncallback is ommitted. You get this error message: " The
		// message with Action '' cannot be processed at the receiver, due to a
		// ContractFilter mismatch at the EndpointDispatcher. This may be because of
		// either a contract mismatch (mismatched Actions between sender and receiver)
		// or a binding/security mismatch between the sender and the receiver. Check
		// that sender and receiver have the same contract and the same binding
		// (including security requirements, e.g. Message, Transport, None)."
	}
	
	public AuthenticateSPResponseElement authenticateSP(
		String url,
		AuthenticateSPElement authenticateSPElement,
		String username,
		String password
	) {
		return (AuthenticateSPResponseElement) webServiceTemplate.marshalSendAndReceive(
			url,
			authenticateSPElement,
			new TokenHeaderRequestCallback(username, password)
		);
	}
	
}