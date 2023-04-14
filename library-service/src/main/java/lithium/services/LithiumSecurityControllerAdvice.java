package lithium.services;

import java.security.Principal;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.WebRequest;

import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class LithiumSecurityControllerAdvice {
	
	@Autowired TokenStore tokenStore;

	@ModelAttribute
	public LithiumTokenUtil tokenUtilPerRequest(WebRequest request, Principal principal) {
		if (request == null) return null;
		if (!(principal instanceof OAuth2Authentication)) return null;
		
		OAuth2Authentication authentication = (OAuth2Authentication) principal;
		String token = ((OAuth2AuthenticationDetails) authentication.getDetails()).getTokenValue();
		
		try {
			LithiumTokenUtil tokenUtil = LithiumTokenUtil.builder(tokenStore, principal).build();

			//TODO: GM-2534 - Store locale code in JWT - This needs to be expanded alot, using only browser is not enough.
			Locale locale = (request.getLocale()!=null)?request.getLocale(): Locale.US;
			log.trace("Locale (from Request: "+request.getLocale()+") (default: "+Locale.US+")");
			if (tokenUtil.getLocale()==null) tokenUtil.setLocale(locale);

			log.debug("Principal: " + tokenUtil.toString());
			return tokenUtil;
		} catch (Exception e) {
			log.error("Unable to parse token: " + principal + " " + token);
		}
		return null;
	}
	
}
