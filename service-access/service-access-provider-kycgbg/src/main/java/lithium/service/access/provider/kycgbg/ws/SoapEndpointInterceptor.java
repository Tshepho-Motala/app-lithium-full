package lithium.service.access.provider.kycgbg.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;

import java.net.SocketTimeoutException;

@Slf4j
@Configurable
public class SoapEndpointInterceptor implements ClientInterceptor {
	
	@Override
	public boolean handleRequest(MessageContext messageContext) throws WebServiceClientException {
		WebServiceLoggingUtils.logRequestMessage(messageContext.getRequest());
		return true;
	}

	@Override
	public boolean handleResponse(MessageContext messageContext) throws WebServiceClientException {
		WebServiceLoggingUtils.logResponseMessage(messageContext.getResponse());
		return true;
	}

	@Override
	public boolean handleFault(MessageContext messageContext) throws WebServiceClientException {
		log.error("handleFault MessageContext :"+messageContext);
		return true;
	}

	@Override
	public void afterCompletion(MessageContext messageContext, Exception ex) throws WebServiceClientException {
		//log.error("afterCompletion MessageContext :"+messageContext, ex);
		if (ex instanceof SocketTimeoutException) {
			log.warn("SocketTimeoutException possibly increase timeouts to avoid this..");
		}
	}
	
	//EndpointInterceptor {
//	@Override
//	public boolean handleRequest(MessageContext messageContext, Object endpoint) throws Exception {
//		log.info("MessageContext :"+messageContext+", Object :"+endpoint);
//		return false;
//	}
//
//	@Override
//	public boolean handleResponse(MessageContext messageContext, Object endpoint) throws Exception {
//		log.info("MessageContext :"+messageContext+", Object :"+endpoint);
//		return false;
//	}
//
//	@Override
//	public boolean handleFault(MessageContext messageContext, Object endpoint) throws Exception {
//		log.info("MessageContext :"+messageContext+", Object :"+endpoint);
//		return false;
//	}
//
//	@Override
//	public void afterCompletion(MessageContext messageContext, Object endpoint, Exception ex) throws Exception {
//		log.error("MessageContext :"+messageContext+", Object :"+endpoint, ex);
//	}
}
