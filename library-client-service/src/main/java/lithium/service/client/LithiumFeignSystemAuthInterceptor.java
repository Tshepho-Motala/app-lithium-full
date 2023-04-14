package lithium.service.client;

import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lithium.systemauth.SystemAuthService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LithiumFeignSystemAuthInterceptor implements RequestInterceptor {
	
	@Autowired
	SystemAuthService systemAuthService;

	public void apply(RequestTemplate requestTemplate) {
		try {
			String token = systemAuthService.getTokenValue();
			log.debug("SystemAuthInterceptor " + requestTemplate.method()+" "+requestTemplate.url()+" "+requestTemplate.queryLine());
			requestTemplate.header("Authorization", "bearer " + token);
		} catch (TimeoutException te) { throw new RuntimeException(te); }
	}
	
}
