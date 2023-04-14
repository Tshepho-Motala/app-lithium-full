package lithium.service.domain.client;

import lithium.exceptions.Status469InvalidInputException;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.exceptions.Status473DomainBettingDisabledException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.domain.client.objects.ecosystem.EcosystemDomainRelationship;
import lithium.service.domain.client.objects.ecosystem.EcosystemRelationshipType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CachingDomainClientService {
	@Autowired private CachingDomainClientService self;
	@Autowired private LithiumServiceClientFactory services;
	@Autowired private MessageSource messageSource;

	public String getDomainSetting(String domainName, DomainSettings domainSettings) {
		try {
			Optional<String> domainSettingByName = self.retrieveDomainFromDomainService(domainName)
					.findDomainSettingByName(domainSettings.key());
			return domainSettingByName.orElseGet(domainSettings::defaultValue);
		} catch (Status550ServiceDomainClientException e) {
			log.error("Could not retrieve domain details.", e);
		}
		return null;
	}

	public String domainLocale(String domainName) {
		try {
			Domain domain = self.retrieveDomainFromDomainService(domainName);
			return domain.getDefaultLocale();
		} catch (Status550ServiceDomainClientException e) {
			log.error("Could not retrieve domain details.", e);
		}
		return "en_US";
	}

	public void checkBettingEnabled(String domainName, String locale) throws Status550ServiceDomainClientException,
			Status473DomainBettingDisabledException {
		Domain domain = self.retrieveDomainFromDomainService(domainName);
		if (domain.getBettingEnabled() != null && !domain.getBettingEnabled())
			throw new Status473DomainBettingDisabledException(
					messageSource.getMessage("SERVICE_DOMAIN.DOMAIN.BETTING_DISABLED", null,
							Locale.forLanguageTag(locale)));
	}

	public String getDefaultDomainCurrency(final String domainName) throws Status550ServiceDomainClientException {
		Domain domain = self.retrieveDomainFromDomainService(domainName);
		return domain.getCurrency();
	}

	public String getDefaultDomainCurrencySymbol(final String domainName) throws Status550ServiceDomainClientException {
		Domain domain = self.retrieveDomainFromDomainService(domainName);
		return domain.getCurrencySymbol();
	}

	public boolean allowNegativeBalanceAdjustment(final String domainName) throws Status550ServiceDomainClientException {
		lithium.service.domain.client.objects.Domain domain = self.retrieveDomainFromDomainService(domainName);
		Optional<String> setting = domain.findDomainSettingByName(DomainSettings.ALLOW_NEGATIVE_BALANCE_ADJUSTMENT.key());
		return Boolean.parseBoolean(setting.orElseGet(DomainSettings.ALLOW_NEGATIVE_BALANCE_ADJUSTMENT::defaultValue));
	}

	public boolean cancelPendingWithdrawalsOnBetResettlement(final String domainName) throws Status550ServiceDomainClientException {
		lithium.service.domain.client.objects.Domain domain = self.retrieveDomainFromDomainService(domainName);
		Optional<String> setting = domain.findDomainSettingByName(DomainSettings.CANCEL_PENDING_WITHDRAWALS_ON_RESETTLEMENT.key());
		return Boolean.parseBoolean(setting.orElseGet(DomainSettings.CANCEL_PENDING_WITHDRAWALS_ON_RESETTLEMENT::defaultValue));
	}

	public String getCurrentDomainTermsAndConditionsVersion(final String domainName)
			throws Status550ServiceDomainClientException {
		lithium.service.domain.client.objects.Domain domain = self.retrieveDomainFromDomainService(domainName);
		Optional<String> setting = domain.findDomainSettingByName(DomainSettings.TERMS_AND_CONDITIONS_VERSION.key());
		return setting.orElseGet(DomainSettings.TERMS_AND_CONDITIONS_VERSION::defaultValue);
	}

	/**
	 * This function will check if the domain has a setting called "Allow Minimal Token"
	 *
	 * @param domainName
	 * @return
	 * @throws Status550ServiceDomainClientException
	 */
	public boolean allowMinimalToken(final String domainName) throws Status550ServiceDomainClientException {
		lithium.service.domain.client.objects.Domain domain = self.retrieveDomainFromDomainService(domainName);
		Optional<String> setting = domain.findDomainSettingByName(DomainSettings.ALLOW_MINIMAL_TOKEN.key());
		return Boolean.parseBoolean(setting.orElseGet(DomainSettings.ALLOW_MINIMAL_TOKEN::defaultValue));
	}

	/**
	 * This function will check if the domain has a setting called "Disable SMTP Sending"
	 *
	 * @param domainName
	 * @return
	 * @throws Status550ServiceDomainClientException
	 */
	public boolean disableSmtpSending(final String domainName) throws Status550ServiceDomainClientException {
		lithium.service.domain.client.objects.Domain domain = self.retrieveDomainFromDomainService(domainName);
		Optional<String> setting = domain.findDomainSettingByName(DomainSettings.DISABLE_SMTP_SENDING.key());
		return Boolean.parseBoolean(setting.orElseGet(DomainSettings.DISABLE_SMTP_SENDING::defaultValue));
	}

	/**
	 * This function will check if the domain has a setting called "Allow Login from Unknown Country"
	 *
	 * @param domainName
	 */
	public boolean allowLoginFromUnknownCountry(final String domainName) {
		try {
			Domain domain = self.retrieveDomainFromDomainService(domainName);
			Optional<String> setting = domain.findDomainSettingByName(
					DomainSettings.ALLOW_LOGIN_FROM_UNKNOWN_COUNTRY.key());
			return setting.map(Boolean::parseBoolean).orElseGet(() -> Boolean.parseBoolean(DomainSettings.ALLOW_LOGIN_FROM_UNKNOWN_COUNTRY.defaultValue()));
		} catch (Status550ServiceDomainClientException e) {
			log.error("Problem getting country flag setting on domain: " + domainName + " message:"
					+ ExceptionUtils.getMessage(e), e);
			return Boolean.parseBoolean(DomainSettings.ALLOW_LOGIN_FROM_UNKNOWN_COUNTRY.defaultValue());
		}
	}

	public String getDwhNotifyMailForUploadedDocuments(final String domainName)
			throws Status550ServiceDomainClientException {
		lithium.service.domain.client.objects.Domain domain = self.retrieveDomainFromDomainService(domainName);

		return domain.findDomainSettingByName(DomainSettings.UPLOADED_DOCUMENT_MAIL_DWH.key())
				.orElse(DomainSettings.UPLOADED_DOCUMENT_MAIL_DWH.defaultValue());

	}

	public Optional<Integer> getAgeOnlyVerifiedStatusLevel(final String domainName)
			throws Status550ServiceDomainClientException {
		lithium.service.domain.client.objects.Domain domain = self.retrieveDomainFromDomainService(domainName);

		return domain.findDomainSettingByName(DomainSettings.AGE_ONLY_VERIFIED_STATUS_LEVEL.key()).map(s ->
		{
			try {
				return Integer.parseInt(s);
			} catch (NumberFormatException e) {
				return null;
			}
		});
	}

	public List<String> getUploadDocumentVersion(final String domainName)
			throws Status550ServiceDomainClientException {
		lithium.service.domain.client.objects.Domain domain = self.retrieveDomainFromDomainService(domainName);

		String version = domain.findDomainSettingByName(DomainSettings.UPLOAD_DOCUMENT_VERSION.key())
				.orElse(DomainSettings.UPLOAD_DOCUMENT_VERSION.defaultValue());

		return Arrays.stream(version.toLowerCase().split(",")).map(String::trim).collect(Collectors.toList());
	}

	public boolean allowLiftingPlayerCasinoBlock(final String domainName) throws Status550ServiceDomainClientException {
		lithium.service.domain.client.objects.Domain domain = self.retrieveDomainFromDomainService(domainName);
		Optional<String> setting = domain.findDomainSettingByName(DomainSettings.ALLOW_LIFTING_PLAYER_CASINO_BLOCK.key());
		return Boolean.parseBoolean(setting.orElseGet(DomainSettings.ALLOW_LIFTING_PLAYER_CASINO_BLOCK::defaultValue));
	}

	@Cacheable(value="lithium.service.domain.data.findbyname",key="#root.args[0]", unless="#result == null")
	public lithium.service.domain.client.objects.Domain retrieveDomainFromDomainService(String domainName)
			throws Status550ServiceDomainClientException {
		log.debug("Retrieving domain " + domainName);
		Response<lithium.service.domain.client.objects.Domain> domain = getDomainClient().findByName(domainName);
		if (domain.isSuccessful() && domain.getData() != null) {
			log.info("Retrieved domain " + domain);
			return domain.getData();
		}
		throw new Status550ServiceDomainClientException("Unable to retrieve domain from domain service: " + domainName +
				domain);
	}

	/**
	 * Returns a cached list of domains that forms part of an ecosystem configuration and their roles within an ecosystem
	 * @param ecosystemName
	 * @return ArrayList<EcosystemDomainRelationship>
	 * @throws Status469InvalidInputException
	 * @throws Status550ServiceDomainClientException
	 */
	@Cacheable(value = "lithium.service.domain.ecosystem.domain-relationships.by-ecosystem-name", key="#root.args[0]",
			unless = "#result == null")
	public ArrayList<EcosystemDomainRelationship> listEcosystemDomainRelationshipsByEcosystemName(final String ecosystemName)
			throws Status469InvalidInputException, Status550ServiceDomainClientException {
		return getEcosystemClient().listEcosystemDomainRelationshipsByEcosystemName(ecosystemName);
	}

	@Cacheable(value = "lithium.service.domain.ecosystem.domain-relationships.by-domain-name", key="#root.args[0]",
			unless = "#result == null")
	public ArrayList<EcosystemDomainRelationship> listEcosystemDomainRelationshipsByDomainName(final String domainName)
			throws Status469InvalidInputException, Status550ServiceDomainClientException {
		return getEcosystemClient().listEcosystemDomainRelationshipsByDomainName(domainName);
	}

	@Cacheable(value = "lithium.service.domain.data.find-all-domains", key = "#root.methodName", unless = "#result == null")
	public Iterable<Domain> retrieveAllDomains()
			throws Status550ServiceDomainClientException {
		Response<Iterable<Domain>> allDomains = getDomainClient().findAllDomains();
		return allDomains.getData();
	}

	public List<String> retrieveEnabledDomains()
			throws Status550ServiceDomainClientException {

		Iterable<Domain> allDomains = self.retrieveAllDomains();

			log.info("Retrieved domains " + allDomains);
			List<String> domainNamesList = new ArrayList<>();
			allDomains.forEach(domain ->
			{
				if (domain.getEnabled() == true && domain.getDeleted() == false) {
					domainNamesList.add(domain.getName());
				}
			});
			return domainNamesList;
	}

	@Cacheable(value = "lithium.service.domain.ecosystem.ecosystem-name.by-domain-name", key="#root.args[0]",
			unless = "#result == null")
	public String getEcosystemNameByDomainName(final String domainName) throws Status550ServiceDomainClientException {
		ArrayList<EcosystemDomainRelationship> ecosystemDomainRelationship;
		try {
			ecosystemDomainRelationship = getEcosystemClient().listEcosystemDomainRelationshipsByDomainName(domainName);
		} catch (Status469InvalidInputException ex) {
			log.warn(ex.getMessage());
			return null;
		}
		Optional<String> ecosystemOptional =  ecosystemDomainRelationship.stream().map(x -> x.getEcosystem().getName()).findFirst();
		return ecosystemOptional.isPresent() ? ecosystemOptional.get() : null;
	}

	public ArrayList<String> listDomainNamesInEcosystemByEcosystemName(final String ecosystemName)
			throws
			Status469InvalidInputException,
			Status550ServiceDomainClientException {
		return self.listEcosystemDomainRelationshipsByEcosystemName(ecosystemName).stream()
				.map(dr -> dr.getDomain().getName())
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public ArrayList<String> listDomainNamesInEcosystemByDomainName(final String domainName)
			throws
			Status469InvalidInputException,
			Status550ServiceDomainClientException {
		return self.listEcosystemDomainRelationshipsByDomainName(domainName).stream()
				.map(dr -> dr.getDomain().getName())
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public Optional<String> findEcosystemRootByDomainName(final String domainName)
			throws
			Status469InvalidInputException,
			Status550ServiceDomainClientException {
		return self.listEcosystemDomainRelationshipsByDomainName(domainName).stream()
				.filter(dr -> dr.getRelationship().getCode().contentEquals(EcosystemRelationshipTypes.ECOSYSTEM_ROOT.key()))
				.map(dr -> dr.getDomain().getName()).findFirst();
	}

	public boolean isDomainNameOfEcosystemRootType(final String domainName) {
		try {
			return self.listEcosystemDomainRelationshipsByDomainName(domainName).stream()
					.filter(dr -> dr.getDomain().getName().equalsIgnoreCase(domainName))
					.filter(dr -> dr.getRelationship().getCode().contentEquals(EcosystemRelationshipTypes.ECOSYSTEM_ROOT.key()))
					.map(dr -> dr.getRelationship().getCode()).findFirst().isPresent();
		} catch (Status469InvalidInputException | Status550ServiceDomainClientException e) {
			return false;
		}
	}

	public Boolean isDomainNameOfEcosystemMutuallyExclusiveType(final String domainName) {
		try {
			return self.listEcosystemDomainRelationshipsByDomainName(domainName).stream()
					.filter(dr -> dr.getDomain().getName().equalsIgnoreCase(domainName))
					.filter(dr -> dr.getRelationship().getCode().contentEquals(EcosystemRelationshipTypes.ECOSYSTEM_MUTUALLY_EXCLUSIVE.key()))
					.map(dr -> dr.getRelationship().getCode()).findFirst().isPresent();
		} catch (Status469InvalidInputException | Status550ServiceDomainClientException e) {
			return false;
		}
	}

	public Optional<EcosystemDomainRelationship> findEnabledDomainsInEcosystem(final String ecosystemName,final String domainName) {
		try {
			return self.listEcosystemDomainRelationshipsByEcosystemName(ecosystemName).stream()
					.filter(edr -> edr.getDomain().getName().equals(domainName))
					.filter(EcosystemDomainRelationship::getEnabled).findFirst();
		}  catch (Status469InvalidInputException | Status550ServiceDomainClientException e){
			return Optional.empty();
		}
	}

	@Cacheable(value = "lithium.service.domain.ecosystem.domain-in-any-ecosystem", unless = "#result == null")
	public boolean isDomainInAnyEcosystem(final String domainName)
			throws
			Status550ServiceDomainClientException {
		return getEcosystemClient().isDomainInAnyEcosystem(domainName);
	}

	public boolean isProtectionOfCustomerFundsEnabled(final String domainName)
			throws Status550ServiceDomainClientException {
		lithium.service.domain.client.objects.Domain domain = self.retrieveDomainFromDomainService(domainName);
		Optional<String> setting = domain.findDomainSettingByName(
				DomainSettings.PROTECTION_OF_CUSTOMER_FUNDS_ENABLED.key());
		return Boolean.parseBoolean(setting.orElseGet(DomainSettings.PROTECTION_OF_CUSTOMER_FUNDS_ENABLED::defaultValue));
	}

	public String getCurrentDomainProtectionOfCustomerFundsVersion(final String domainName)
			throws Status550ServiceDomainClientException {
		lithium.service.domain.client.objects.Domain domain = self.retrieveDomainFromDomainService(domainName);
		Optional<String> setting = domain.findDomainSettingByName(
				DomainSettings.PROTECTION_OF_CUSTOMER_FUNDS_VERSION.key());
		return setting.orElseGet(DomainSettings.PROTECTION_OF_CUSTOMER_FUNDS_VERSION::defaultValue);
	}

	public DomainClient getDomainClient() throws Status550ServiceDomainClientException {
		try {
			return services.target(DomainClient.class, "service-domain", true);
		} catch (LithiumServiceClientFactoryException fe) {
			throw new Status550ServiceDomainClientException(fe);
		}
	}

	public EcosystemClient getEcosystemClient() throws Status550ServiceDomainClientException {
		try {
			return services.target(EcosystemClient.class, "service-domain", true);
		} catch (LithiumServiceClientFactoryException fe) {
			throw new Status550ServiceDomainClientException(fe);
		}
	}

	@Cacheable(value = "lithium.service.domain.ecosystem.domain-relationships.is-ecosystem-name", key="#root.args[0]")
	public boolean isEcosystemName(String ecosystemName) {
		try {
			ArrayList<EcosystemDomainRelationship> byEcosystemName = self.listEcosystemDomainRelationshipsByEcosystemName(ecosystemName);
			if (byEcosystemName == null || byEcosystemName.isEmpty()) { return false; }
			return byEcosystemName.stream()
					.anyMatch(edr -> edr.getEcosystem().getName().equalsIgnoreCase(ecosystemName));
		} catch (Status550ServiceDomainClientException | Status469InvalidInputException e) { return false; }
	}

	@Cacheable(value = "lithium.service.domain.data.is-domain-name", key="#root.args[0]")
	public boolean isDomainName(String domainName)  {
		try {
			Domain domain = self.retrieveDomainFromDomainService(domainName);
			return domain != null;
		} catch (Status550ServiceDomainClientException e) {return false;}
	}

	public List<String> listMutuallyExclusiveDomainsWithinAnEcosystem(String ecosystemName) {
		try {
			return self.listEcosystemDomainRelationshipsByEcosystemName(ecosystemName).stream()
					.filter(dr -> dr.getRelationship().getCode().contentEquals(EcosystemRelationshipTypes.ECOSYSTEM_MUTUALLY_EXCLUSIVE.key()))
					.map(dr -> dr.getDomain().getName())
					.collect(Collectors.toList());
		} catch (Status469InvalidInputException | Status550ServiceDomainClientException e) {
			return null;
		}
	}

	public Optional<EcosystemDomainRelationship> findEcosystemNameByEcosystemRootDomainName(String domainName) {
		try {
			return self.listEcosystemDomainRelationshipsByDomainName(domainName).stream()
					.filter(dr -> dr.getDomain().getName().equalsIgnoreCase(domainName))
					.filter(EcosystemDomainRelationship::getEnabled)
					.findFirst();
		} catch (Status469InvalidInputException | Status550ServiceDomainClientException e){
			return Optional.empty();
		}
	}

	@Cacheable(value = "lithium.service.domain.ecosystem.domain-ecosystem-relationship-type",
			key="#root.args[0]")
	public String findEcosystemRelationshipTypeByDomainName(String domainName) {

		if (!self.isDomainInAnyEcosystem(domainName)) {
			return null;
		}

		ArrayList<EcosystemDomainRelationship> ecosystemDomainRelationships =
				self.listEcosystemDomainRelationshipsByDomainName(domainName);

		Optional<String> ertCode = ecosystemDomainRelationships.stream()
				.filter(ert -> ert.getDomain().getName().equals(domainName))
				.map(EcosystemDomainRelationship::getRelationship)
				.map(EcosystemRelationshipType::getCode).findFirst();

		if (ecosystemDomainRelationships.isEmpty()) {
			return null;
		}

		return EcosystemRelationshipTypes.fromKey(ertCode.get()).key();
	}

	public String findEcosystemRelationshipTypeByDomainNameShort(String domainName) {
		String ert = self.findEcosystemRelationshipTypeByDomainName(domainName);
		if (ert == null) {
			return null;
		}
		StringBuilder r = new StringBuilder();
		String[] s = ert.split("_");
		for (var i = 1; i < s.length; i++) {
			r.append(s[i].charAt(0));
		}
		return r.toString().toLowerCase();
	}
}
