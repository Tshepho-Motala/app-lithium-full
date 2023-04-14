package lithium.service.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.transaction.Transactional;

import lithium.service.domain.client.EcosystemRelationshipTypes;
import lithium.service.domain.data.entities.EcosystemRelationshipType;
import lithium.service.domain.data.repositories.EcosystemRelationshipTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import lithium.service.client.provider.ProviderConfig;
import lithium.service.domain.data.entities.Domain;
import lithium.service.domain.data.entities.DomainProviderLink;
import lithium.service.domain.data.entities.DomainRole;
import lithium.service.domain.data.entities.Provider;
import lithium.service.domain.data.entities.ProviderProperty;
import lithium.service.domain.data.entities.ProviderType;
import lithium.service.domain.data.entities.Role;
import lithium.service.domain.data.repositories.DomainProviderLinkRepository;
import lithium.service.domain.data.repositories.DomainRepository;
import lithium.service.domain.data.repositories.DomainRoleRepository;
import lithium.service.domain.data.repositories.ProviderPropertyRepository;
import lithium.service.domain.data.repositories.ProviderRepository;
import lithium.service.domain.data.repositories.ProviderTypeRepository;
import lithium.service.domain.services.DomainCurrencyService;

@Configuration
public class ServiceDomainInit {
	@Autowired
	private DomainRepository domainRepository;
	@Autowired
	private DomainRoleRepository domainRoleRepository;
	@Autowired
	private ProviderRepository providerRepository;
	@Autowired
	private ProviderPropertyRepository providerPropertyRepository;
	@Autowired
	private ProviderTypeRepository providerTypeRepository;
	@Autowired
	protected DomainProviderLinkRepository domainProviderLinkRepo;
	@Autowired
	private DomainCurrencyService domainCurrencyService;
	@Autowired
  private EcosystemRelationshipTypeRepository ecosystemRelationshipTypeRepository;
	
	public HashMap<ProviderConfig.ProviderType, ProviderType> initProviderTypes() {
		HashMap<ProviderConfig.ProviderType, ProviderType> ptHash = new HashMap<>();
		for (ProviderConfig.ProviderType pt : ProviderConfig.ProviderType.values()) {
			ptHash.put(pt, createProviderType(pt.type().toUpperCase()));
		}
		
		return ptHash;
	}

	public void initEcosystemRelationshipTypes() {
	  for (EcosystemRelationshipTypes type: EcosystemRelationshipTypes.values()) {
	    ecosystemRelationshipTypeRepository.findOrCreateByCode(type.key(), EcosystemRelationshipType::new);
    }
  }
	
	public void init() throws Exception {
		HashMap<ProviderConfig.ProviderType, ProviderType> ptHash = initProviderTypes();
		
		Domain defaultDomain = createDomain(
			"default",
			Collections.emptyList(),
			null,
			"Root Domain", "The root of all other domains, for internal use to manage all customer domains", "http://www.playsafesa.com", false
		);
		
		Domain epnDomain = createDomain(
			"epn",
			Collections.emptyList(),
			defaultDomain,
			"Equity Poker Network", "The Equity Poker Network of brands", "http://www.equitypokernetwork.com", false
		);
		
//		Domain ffpDomain = 
		createDomain(
			"ffp",
			Collections.emptyList(),
			epnDomain,
			"Full Flush Poker", null, "http://www.fullflushpoker.com", true
		);
//		Domain intDomain = 
		createDomain(
			"int",
			Collections.emptyList(),
			epnDomain,
			"Integer Poker", null, "http://www.integerpoker.com", true
		);
		
		Domain toftDomain = createDomain(
			"toft",
			Collections.emptyList(),
			defaultDomain,
			"Toft Software", null, "http://www.toftsoftware.com", false
		);
		
		Domain luckyBetzDomain = createDomain(
			"luckybetz",
			Collections.emptyList(),
			toftDomain,
			"Lucky Betz Casino", null, "http://www.luckybetz.com", true
		);
		
//		Provider fakeProvider = createProvider(defaultDomain, 2, ptHash.get(ProviderConfig.ProviderType.USER), "service-user-provider-fake", "user-provider-fake", true);
//		Provider method1ProviderBol = createProvider(defaultDomain, 4, ptHash.get(ProviderConfig.ProviderType.USER), "service-user-provider-method1", "user-provider-method1-betonline", true);
	//	Provider method1ProviderSb = createProvider(defaultDomain, 5, ptHash.get(ProviderConfig.ProviderType.USER), "service-user-provider-method1", "user-provider-method1-sportsbetting", true);
//		Provider ldapProvider = createProvider(defaultDomain, 3, ptHash.get(ProviderConfig.ProviderType.USER), "service-user-provider-ldap", "user-provider-ldap-pssa", true);
	//	Provider openLdapProvider = createProvider(defaultDomain, 6, ptHash.get(ProviderConfig.ProviderType.USER), "service-user-provider-ldap", "user-provider-ldap-forumsys", true);
//		createProvider(defaultDomain, 1, ptHash.get(ProviderConfig.ProviderType.USER), "service-user-provider-internal", "user-provider-internal", true);
		
//		Provider fakeProviderAuth = createProvider(defaultDomain, 2, ptHash.get(ProviderConfig.ProviderType.AUTH), "service-auth-provider-fake", "auth-provider-fake", true);
//		Provider method1ProviderBolAuth = createProvider(defaultDomain, 4, ptHash.get(ProviderConfig.ProviderType.AUTH), "service-auth-provider-method1", "auth-provider-method1-betonline", true);
	//	Provider method1ProviderSbAuth = createProvider(defaultDomain, 5, ptHash.get(ProviderConfig.ProviderType.AUTH), "service-user-provider-method1", "user-provider-method1-sportsbetting", true);
//		Provider ldapProviderAuth = createProvider(defaultDomain, 3, ptHash.get(ProviderConfig.ProviderType.AUTH), "service-auth-provider-ldap", "auth-provider-ldap-pssa", true);
	//	Provider openLdapProviderAuth = createProvider(defaultDomain, 6, ptHash.get(ProviderConfig.ProviderType.AUTH), "service-user-provider-ldap", "user-provider-ldap-forumsys", true);
//		createProvider(defaultDomain, 1, ptHash.get(ProviderConfig.ProviderType.AUTH), "service-auth-provider-internal", "auth-provider-internal", true);
		
		//Provider betsoftProvider = createProvider(defaultDomain, 2, ptHash.get(ProviderConfig.ProviderType.CASINO), "service-casino-provider-betsoft", "service-casino-provider-betsoft", false);
		Provider rivalProvider = createProvider(defaultDomain, 1, ptHash.get(ProviderConfig.ProviderType.CASINO), "service-casino-provider-rival", "service-casino-provider-rival", true);
		Provider livedealerProvider = createProvider(defaultDomain, 1, ptHash.get(ProviderConfig.ProviderType.CASINO), "service-casino-provider-livedealer", "service-casino-provider-livedealer", true);
		Provider nucleusProvider = createProvider(defaultDomain, 2, ptHash.get(ProviderConfig.ProviderType.CASINO), "service-casino-provider-nucleus", "service-casino-provider-nucleus", true);
		
		Provider mnetProvider = createProvider(defaultDomain, 1, ptHash.get(ProviderConfig.ProviderType.CASHIER), "service-cashier-provider-mercadonet", "service-cashier-provider-mercadonet", true);
		
		//createProviderLink(toftDomain, betsoftProvider, false);
		//createProviderLink(luckyBetzDomain, betsoftProvider, false);
		
		createProviderLink(toftDomain, nucleusProvider, false);
		createProviderLink(luckyBetzDomain, nucleusProvider, false);
		
		createProviderLink(toftDomain, rivalProvider, false);
		createProviderLink(luckyBetzDomain, rivalProvider, false);
		
		createProviderLink(toftDomain, livedealerProvider, false);
		createProviderLink(luckyBetzDomain, livedealerProvider, false);
		
		createProviderLink(toftDomain, mnetProvider, false);
		createProviderLink(luckyBetzDomain, mnetProvider, false);
		
		createProviderProperty(rivalProvider, "baseUrl", "https://demo.casinocontroller.com/beta");
		createProviderProperty(rivalProvider, "hashPassword", "y2xCHCCFaIEFuY7TG4Fb1HgRvVVhnvRh");
		createProviderProperty(rivalProvider, "apikey", "Lsa2cErLqJoYGDW");
		createProviderProperty(rivalProvider, "imageUrl", "http://game-assets.game-assets-master.cloud.playsafesa.com/rival");
		createProviderProperty(rivalProvider, "gameListUrl", "http://game-assets.game-assets-master.cloud.playsafesa.com/rival/gamelist.json");
		createProviderProperty(rivalProvider, "currency", "USD");
		
		createProviderProperty(livedealerProvider, "baseUrl", "http://test.golivedealer.com");
		createProviderProperty(livedealerProvider, "clientUser", "LUCKYBETZ");
		createProviderProperty(livedealerProvider, "clientPassword", "dd4e81d25b06d0c4b4ff19fb81273a60");
		createProviderProperty(livedealerProvider, "apikey", "Wkcm4rMkfk5YPDm");
		createProviderProperty(livedealerProvider, "imageUrl", "http://game-assets.game-assets-master.cloud.playsafesa.com/livedealer");
		createProviderProperty(livedealerProvider, "gameListUrl", "http://game-assets.game-assets-master.cloud.playsafesa.com/livedealer/gamelist.json");
		createProviderProperty(livedealerProvider, "currency", "USD");

		
//		createProviderProperty(betsoftProvider, "baseUrl", "https://lobby-ffp.discreetgaming.com");
//		createProviderProperty(betsoftProvider, "hashPassword", "hashpass");
//		createProviderProperty(betsoftProvider, "bankId", "936");
//		createProviderProperty(betsoftProvider, "apikey", "apikeyhere");
//		createProviderProperty(betsoftProvider, "imageUrl", "http://game-assets.game-assets-master.cloud.playsafesa.com/betsoft");
//		createProviderProperty(betsoftProvider, "currency", "USD");
		
		createProviderProperty(nucleusProvider, "baseUrl", "http://toft-ng-copy.nucleusgaming.com");
		createProviderProperty(nucleusProvider, "hashPassword", "tfhguyfg29F3qA8");
		createProviderProperty(nucleusProvider, "bankId", "1940");
		createProviderProperty(nucleusProvider, "apikey", "LHszKnhAWOCYEdI");
		createProviderProperty(nucleusProvider, "imageUrl", "http://game-assets.game-assets-master.cloud.playsafesa.com/nucleus");
		createProviderProperty(nucleusProvider, "currency", "USD");
		
		createProviderProperty(mnetProvider, "baseUrl", "http://10.0.0.169");
		createProviderProperty(mnetProvider, "currency", "USD");
		createProviderProperty(mnetProvider, "apikey", "MYiaEciAWDEFRLF");
		createProviderProperty(mnetProvider, "instanceId", "1");
		createProviderProperty(mnetProvider, "skinId", "89");

		
//		createProviderProperty(fakeProvider, "test1", "value1");
//		createProviderProperty(method1ProviderBol, "client", "betonline");
//		createProviderProperty(method1ProviderBol, "login.url", "http://qa.playsafesa.com:8080/bo-services-example-impl/rest/login");
//		createProviderProperty(method1ProviderSb, "client", "sportsbetting");
//		createProviderProperty(method1ProviderSb, "login.url", "http://qa.playsafesa.com:8080/bo-services-example-impl/rest/login");
//		createProviderProperty(ldapProvider, "url", "ldap://10.0.15.233:389");
//		createProviderProperty(ldapProvider, "base", "CN=users,DC=playsafesa,DC=com");
//		createProviderProperty(ldapProvider, "userDn", "CN=Administrator,CN=Users,DC=playsafesa,DC=com");
//		createProviderProperty(ldapProvider, "password", "India225");
//		createProviderProperty(openLdapProvider, "url", "ldap://ldap.forumsys.com:389");
//		createProviderProperty(openLdapProvider, "base", "ou=scientists,dc=example,dc=com");
//		createProviderProperty(openLdapProvider, "userDn", "cn=read-only-admin,dc=example,dc=com");
//		createProviderProperty(openLdapProvider, "password", "password");
		
//		createProviderProperty(fakeProviderAuth, "test1", "value1");
//		createProviderProperty(method1ProviderBolAuth, "client", "betonline");
//		createProviderProperty(method1ProviderBolAuth, "login.url", "http://qa.playsafesa.com:8080/bo-services-example-impl/rest/login");
//		createProviderProperty(method1ProviderSbAuth, "client", "sportsbetting");
//		createProviderProperty(method1ProviderSbAuth, "login.url", "http://qa.playsafesa.com:8080/bo-services-example-impl/rest/login");
//		createProviderProperty(ldapProviderAuth, "url", "ldap://10.0.15.233:389");
//		createProviderProperty(ldapProviderAuth, "base", "CN=users,DC=playsafesa,DC=com");
//		createProviderProperty(ldapProviderAuth, "userDn", "CN=Administrator,CN=Users,DC=playsafesa,DC=com");
//		createProviderProperty(ldapProviderAuth, "password", "India225");
//		createProviderProperty(openLdapProviderAuth, "url", "ldap://ldap.forumsys.com:389");
//		createProviderProperty(openLdapProviderAuth, "base", "ou=scientists,dc=example,dc=com");
//		createProviderProperty(openLdapProviderAuth, "userDn", "cn=read-only-admin,dc=example,dc=com");
//		createProviderProperty(openLdapProviderAuth, "password", "password");
	}
	
	private ProviderType createProviderType(String type) {
		ProviderType p = providerTypeRepository.findByName(type);
		if (p == null) {
			p = providerTypeRepository.save(ProviderType.builder().name(type).build());
		}
		return p;
	}

	private Provider createProvider(Domain domain, int priority, ProviderType type, String url, String name, Boolean enabled) {
		Provider p = providerRepository.findByNameAndDomainNameAndProviderTypeId(name, domain.getName(), type.getId());
		if (p == null) {
			p = providerRepository.save(Provider.builder()
					.providerType(type)
					.name(name)
					.domain(domain)
					.priority(priority)
					.url(url)
					.enabled(enabled)
					.build());
		}
		createProviderLink(domain, p, true);
		return p;
	}
	
	private DomainProviderLink createProviderLink(Domain domain, Provider provider, boolean ownerLink) {
		DomainProviderLink dpl = domainProviderLinkRepo.findByDomainNameAndProviderUrlAndProviderProviderTypeIdAndDeletedFalseAndEnabledTrue(domain.getName(), provider.getUrl(), provider.getProviderType().getId());
		if (dpl == null) {
			dpl = DomainProviderLink.builder()
				.domain(domain)
				.provider(provider)
				.enabled(true)
				.deleted(false)
				.ownerLink(ownerLink)
				.build();
			dpl = domainProviderLinkRepo.save(dpl);
		}
		return dpl;
	}
	
	private ProviderProperty createProviderProperty(Provider provider, String propertyName, String propertyValue) {
		ProviderProperty p = providerPropertyRepository.findByProviderAndName(provider, propertyName);
		if (p == null) {
			p = providerPropertyRepository.save(
				ProviderProperty.builder()
				.provider(provider)
				.name(propertyName)
				.value(propertyValue)
				.build()
			);
		}
		return p;
	}
	
	@Transactional(rollbackOn=Exception.class)
	public Domain createDomain(
		String name, 
		List<Role> defaultRoles,
		Domain parent,
		String displayName,
		String description,
		String url,
		boolean players
	) throws Exception {
		Domain d = domainRepository.findByName(name);
		if (d == null) {
			d = domainRepository.save(
				Domain.builder()
					.name(name)
					.displayName(displayName)
					.description(description)
					.enabled(true)
					.deleted(false)
					.url(url)
					.players(players)
					.currency("USD")
					.currencySymbol("$")
					.defaultLocale("en_US")
					.parent(parent)
				.build()
			);
			for (Role role:defaultRoles) {
				domainRoleRepository.save(DomainRole.builder().domain(d).role(role).deleted(false).enabled(true).build());
			}
		}
		domainCurrencyService.syncDefaultCurrency(d);
		return d;
	}
}
