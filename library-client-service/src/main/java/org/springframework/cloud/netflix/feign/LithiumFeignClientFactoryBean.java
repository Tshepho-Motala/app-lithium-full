package org.springframework.cloud.netflix.feign;

import feign.Feign.Builder;
import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lithium.exceptions.ServiceExceptionErrorDecoder;
import lithium.systemauth.SystemAuthService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClientFactoryBean;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.http.HttpHeaders;

import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

@Data
@EqualsAndHashCode(callSuper=true)
@Slf4j
public class LithiumFeignClientFactoryBean extends FeignClientFactoryBean {
	
	private boolean systemAuth;
	
	@Autowired SystemAuthService systemAuthService;

	@Override
	protected Builder feign(FeignContext context) {
		Builder builder = super.feign(context);
		builder.errorDecoder(new ServiceExceptionErrorDecoder(getType()));
		Map<String, RequestInterceptor> requestInterceptors = context.getInstances(
				getName(), RequestInterceptor.class);

		if (systemAuth) {
			RequestInterceptor systemAuthInterceptor = new RequestInterceptor() {
				
				@Override
				public void apply(RequestTemplate requestTemplate) {
					try {
						String systemToken = systemAuthService.getTokenValue();
						log.trace("SystemAuthInterceptor " + requestTemplate.method()+" "+requestTemplate.url()+" "+requestTemplate.queryLine());
						replaceAuthorizationWith(systemToken).accept(requestTemplate);
					} catch (TimeoutException te) { throw new RuntimeException(te); }
				}
			};
			requestInterceptors.put("systemAuthInterceptor", systemAuthInterceptor);
		}
		
		requestInterceptors.put("loggingRequestInterceptor", new RequestInterceptor() {
			@Override
			public void apply(RequestTemplate template) {
				log.debug("Feign Request: " + template.method() + " " + getType().toString() + " " + template.url());
			}
		});
		
		if (requestInterceptors != null) {
			builder.requestInterceptors(requestInterceptors.values());
		}

		// Hack to increase read timeout on the summary migration system to system http requests
		// I've tried various properties from documentation but could not achieve the same
		if (getType().toString()
				.contentEquals("interface lithium.service.accounting.client.SystemSummaryReconciliationClient")) {
			log.debug("Detected interface lithium.service.accounting.client.SystemSummaryReconciliationClient,"
					+ " overriding connectTimeoutMillis and readTimeoutMillis");
			builder.options(new Request.Options(10000, 120000));
		}

		return builder;
	}

	private static Consumer<RequestTemplate> replaceAuthorizationWith(String token){
		return requestTemplate -> {
			requestTemplate.removeHeader(HttpHeaders.AUTHORIZATION);
			requestTemplate.header(HttpHeaders.AUTHORIZATION, "bearer " + token);
		};
	}
}
