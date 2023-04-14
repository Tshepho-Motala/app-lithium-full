package lithium.server.security.converters;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.server.security.services.TokenService;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.DomainClient;
import lithium.service.domain.client.DomainRoleClient;
import lithium.service.domain.client.DomainSettings;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.domain.client.objects.DomainRole;
import lithium.service.user.client.objects.User;
import lithium.tokens.IO;
import lithium.tokens.JWTDomain;
import lithium.tokens.JWTRole;
import lithium.tokens.JWTUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Data
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LithiumTokenEnhancer implements TokenEnhancer {

	private CachingDomainClientService cachingServices;
	private LithiumServiceClientFactory services;

	private String jwtUser(User user) throws Exception {
		StopWatch sw = new StopWatch("jwtUser");
		List<JWTDomain> jwtDomains = new ArrayList<>();
		sw.start("domain");
		DomainClient domainClient = services.target(DomainClient.class, true);
		DomainRoleClient domainRoleClient = services.target(DomainRoleClient.class, true);
		Response<List<Domain>> children = domainClient.children(user.getDomain().getName());
		sw.stop();
		log.debug("===================DOMAIN===================");
		children.getData().forEach(domain -> {
			JWTDomain jwtDomain = JWTDomain.builder()
//				.id(domain.getId())
				.name(domain.getName())
				.parent(domain.getSuperName())
				.displayName(domain.getDisplayName())
				.playerDomain(domain.getPlayers())
				.build();

			sw.start("domainRoleClient.list("+domain.getName()+")");

			Response<Iterable<DomainRole>> response = domainRoleClient.list(domain.getName());
			sw.stop();
			Iterable<DomainRole> list = response.getData();
			StreamSupport.stream(list.spliterator(), false)
					.filter(dr -> {
						if (dr.getEnabled()) {
							return true;
						}
						return false;
					})
					.forEach(dr -> {
						log.debug("role :: " + dr.getRole().getRole() + " domain:" + dr.getDomain().getName());
						JWTRole jwtRole = JWTRole.builder()
//				.id(dr.getRole().getId())
								.name(dr.getRole().getRole())
								.selfApplied(true)
								.descending(false)
								.build();
						jwtDomain.addRole(jwtRole);
					});
			jwtDomains.add(jwtDomain);
		});
		log.debug("===================USER===================");
		if (user.getGroups() != null)
			user.getGroups().stream()
					.filter(g -> {
						if ((!g.getDeleted()) && (g.getEnabled())) {
							return true;
						}
						return false;
					})
					.forEach(g -> {
						log.debug("Group : " + g);
						g.getGrds().forEach(grd -> {
							log.debug("grd : " + grd.getRole().getRole()
									+ " desc : " + grd.getDescending()
									+ " self : " + grd.getSelfApplied()
									+ " domain: " + grd.getDomain().getName()
									+ " parent: " + ((grd.getDomain().getParent() != null) ? grd.getDomain().getParent().getName() : "N/A")
							);
							JWTDomain jwtDomain = jwtDomains.stream()
									.filter(d -> {
										if (d.getName().equalsIgnoreCase(grd.getDomain().getName())) {
											return true;
										}
										return false;
									})
									.findFirst()
									.orElse(null);

							if (jwtDomain == null) {
								jwtDomain = JWTDomain.builder()
//					.id(g.getDomain().getId())
										.name(grd.getDomain().getName())
										.displayName(grd.getDomain().getDisplayName())
										.playerDomain(grd.getDomain().getPlayers())
										.build();
								jwtDomains.add(jwtDomain);
							}

							JWTRole jwtRole = JWTRole.builder()
									.name(grd.getRole().getRole())
									.selfApplied(grd.getSelfApplied())
									.descending(grd.getDescending())
									.build();

							if (!jwtDomain.hasRole(jwtRole.getName())) {
								jwtDomain.addRole(jwtRole);
							} else {
								JWTRole jwtRole2 = jwtDomain.findRole(jwtRole.getName());
								BeanUtils.copyProperties(jwtRole, jwtRole2);
								jwtDomain.replaceRole(jwtRole, jwtRole2);
							}
						});
					});
		log.debug("===================JWTUser===================");
		Long sessionId = (user.getSession() != null) ? user.getSession().getId() : user.getLastLogin().getId();
		JWTUser jwtUser = JWTUser.builder()
				.id(user.getId())
				.username(user.getUsername())
//			.enabled(user.isEnabled())
				.apiToken(user.getApiToken())
				.firstName(user.getFirstName())
				.lastName(user.getLastName())
				.email(user.getEmail())
				.domainId(user.getDomain().getId())
				.domainName(user.getDomain().getName())
				.domainDisplayName(user.getDomain().getDisplayName())
				.playerDomain(user.getDomain().getPlayers())
				.domains(jwtDomains)
				.shortGuid(user.getShortGuid())
				.sessionId(sessionId)
				.guid(user.guid())
				.build();
		ObjectMapper om = new ObjectMapper();
		om.setSerializationInclusion(Include.NON_NULL);
		String valueAsString = om.writeValueAsString(jwtUser);

		log.debug("jwtUser : "+valueAsString);
		log.debug("jwtUser : "+jwtUser);

		if (sw.getTotalTimeSeconds() > 1) {
			log.warn(sw.prettyPrint());
		} else {
			log.trace(sw.prettyPrint());
		}
		return valueAsString;
	}

	private String doCompress(String payload) throws IOException {
		String compressed = IO.compressString(payload);
		log.debug("Compressing jwtUser :: Size Before : " + payload.length() + " || Size After : " + compressed.length());
		return compressed;
	}

	/**
	 * The enhancements done here will affect the access_token.
	 * For enhancements to the response object, see lithium.server.oauth2.controllers.AccessTokenController#responseObjEnhancements().
	 * For enhancements to both, you will need to include your key/value on both maps.
	 *
	 * Nothing should go into the token that, if changed during a session, will cause significant risk to business
	 * if the old value in the token is used since token will not update when those values change in the system.
	 *
	 * @param accessToken
	 * @param authentication
	 * @return
	 */
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		StopWatch sw = new StopWatch();
		log.debug("Principal :: " + authentication.getPrincipal());
		if (authentication.getPrincipal() instanceof User) {
			sw.start("enhance");
			final User user = (User) authentication.getPrincipal();
			final Map<String, Object> additionalInfo = new HashMap<>();
			sw.stop();

			try {
				//These have to be included in all tokens
				sw.start("required");
				additionalInfo.put("userId", user.getId());
				additionalInfo.put("username", user.getUsername());
				additionalInfo.put("userGuid", user.guid());
				additionalInfo.put("domainName", user.getDomain().getName());
				if (user.getSession() != null && user.getSession().getId() != null) {
					additionalInfo.put("sessionId", user.getSession().getId());
				} else {
					if (user.getLastLogin() != null && user.getLastLogin().getId() != null) {
						additionalInfo.put("sessionId", user.getLastLogin().getId());
					}
				}
				sw.stop();

				if (!cachingServices.allowMinimalToken(user.getDomain().getName())) {
					try {
						sw.start("jwtUser");
						String jwtUser = jwtUser(user);
						sw.stop();
						sw.start("doCompress");
						additionalInfo.put("jwtUser", doCompress(jwtUser));
						sw.stop();
						sw.start("more additionalInfo");
						additionalInfo.put("email", user.getEmail());
						additionalInfo.put("shortGuid", user.getShortGuid());
						additionalInfo.put("firstName", user.getFirstName());
						additionalInfo.put("lastName", user.getLastName());
						additionalInfo.put("registrationDate", user.getCreatedDate());
						log.trace("user.getSession(): " + user.getSession());
						log.trace("user.getLastLogin(): " + user.getLastLogin());
						if (user.getSession() != null && user.getSession().getSessionKey() != null) {
							additionalInfo.put("sessionKey", user.getSession().getSessionKey());
							if (user.getLastLogin() != null) {
								additionalInfo.put("lastLogin", user.getLastLogin().getDate());
								additionalInfo.put("lastIP", user.getLastLogin().getIpAddress());
							}
						} else {
							if (user.getLastLogin() != null) {
								additionalInfo.put("sessionKey", user.getLastLogin().getSessionKey());
							}
						}
						sw.stop();

						sw.start("end");
					} catch (Exception e) {
						log.error("Could not process Domains.", e);
						additionalInfo.put("jwtUser", "");
					}
					sw.stop();
				}

				//Create the token with the additional information
				((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);

				sw.start("doDomainEnhancements");
				doDomainEnhancements(user, accessToken);
				sw.stop();
			} catch (Status550ServiceDomainClientException e) {
				log.error("Could not process domain settings.", e);
				additionalInfo.put("jwtUser", "");
			}

			if (sw.getTotalTimeSeconds() > 1) {
				log.warn(sw.prettyPrint());
			} else {
				log.trace(sw.prettyPrint());
			}
		}
		return accessToken;
	}

	private void doDomainEnhancements(User user, OAuth2AccessToken accessToken) {
		try {
			Domain domain = (Domain) TokenService.getFromThread(TokenService.TL_DATA_DOMAIN);

			// Session Timeout
			Optional<String> sessionTimeoutSetting = domain.findDomainSettingByName(DomainSettings.SESSION_TIMEOUT.key());
			if (sessionTimeoutSetting.isPresent()) {
				Long domainTimeout = Long.parseLong(sessionTimeoutSetting.get());
				((DefaultOAuth2AccessToken) accessToken).setExpiration(new Date((domainTimeout * 1000)
					+ System.currentTimeMillis()));
			}
		} catch (Exception e) {
			log.error("Failed to enhance token with domain properties [user="+user+", accessToken="+accessToken+"]");
		}
	}
}
