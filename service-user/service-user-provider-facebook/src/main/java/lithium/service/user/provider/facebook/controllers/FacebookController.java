package lithium.service.user.provider.facebook.controllers;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.Provider;
import lithium.service.user.provider.facebook.user.FBConnection;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class FacebookController {
	@Getter
	@Value("${spring.application.name}")
	private String moduleName;
	
	@Autowired FBConnection fbConnection;
	@Autowired LithiumServiceClientFactory serviceFactory;
	
	@GetMapping(path = "/login")
	public RedirectView login(
		@RequestParam("domain") String domainName,
		@RequestParam(name="returnAddr", required=false) String returnAddr,
		WebRequest request,
		RedirectAttributes attributes
	) throws Exception {
		String url = "https://www.facebook.com/v3.3/dialog/oauth"
			+"?client_id=453118391906446"
			+"&state="+domainName
			+"&redirect_uri="+URLEncoder.encode("http://localhost:9000/service-user/users/auth?provider=service-user-provider-facebook&domain=default&username=&password=&ipAddress=&userAgent=", "UTF-8");
		log.info("url : "+url);
		
		return new RedirectView(url);
	}
	
	public Provider providerAuth(String domainName) throws Exception {
		return provider(domainName, ProviderType.AUTH);
	}
	
	private Provider provider(String domainName, ProviderType providerType) throws Exception {
		ProviderClient providerClient = serviceFactory.target(ProviderClient.class, true);
		Response<Iterable<Provider>> response = providerClient.listByDomainAndType(domainName, providerType.type());
		List<Provider> providers = new ArrayList<>();
		
		if (response.isSuccessful()) {
			response.getData().forEach(providers::add);
			providers.removeIf(p -> (p.getEnabled() == false || !p.getUrl().equalsIgnoreCase(moduleName)));
			providers.sort(Comparator.comparingInt(Provider::getPriority));
			if (providers.size() != 1) throw new Exception("Provider Setup Issue.");
			return providers.get(0);
		}
		throw new Exception("No provider found.");
	}
	
	@GetMapping(path="/auth")
	public RedirectView auth(
		@RequestParam(name="code", required=true) String code,
		@RequestParam(name="state", required=true) String state
	) throws Exception {
		Provider provider = providerAuth(state);
		String appId = provider.getPropertyValue("appId");
		String appSecret = provider.getPropertyValue("appSecret");
		
		String accessToken = fbConnection.getAccessToken(code, appId, appSecret);
		log.info("accessToken : "+accessToken);
		Facebook facebook = new FacebookTemplate(accessToken, "lithium", "453118391906446");
		String [] fields = { "id", "email",  "first_name", "last_name" };
		User profile = facebook.fetchObject("me", User.class, fields);
		
		log.info("fbProfileData :: "+profile);
		
		return new RedirectView("http://localhost:9000/service-user-provider-facebook/users/auth?domain="+state);
	}
}
