package lithium.service.accounting.provider.internal;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.accounting.provider.internal.data.entities.Currency;
import lithium.service.accounting.provider.internal.data.entities.TransactionType;
import lithium.service.accounting.provider.internal.data.entities.User;
import lithium.service.accounting.provider.internal.data.repositories.AccountRepository;
import lithium.service.accounting.provider.internal.data.repositories.CurrencyRepository;
import lithium.service.accounting.provider.internal.data.repositories.UserRepository;
import lithium.service.accounting.provider.internal.services.TransactionService;
import lithium.service.accounting.provider.internal.services.TransactionTypeService;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.util.Collection;

@Component
@Slf4j
public class ServiceAccountingProviderInternalModuleInfo extends ModuleInfoAdapter {

	@Autowired UserRepository owners;
	@Autowired AccountRepository accounts;
	@Autowired CurrencyRepository currencies;
	@Autowired TransactionService service;
	@Autowired TransactionTypeService transactionTypeService;

	public ServiceAccountingProviderInternalModuleInfo() {
		Category c = Category.builder().name("Accounting").description("Accounting access.").build();
		addRole(Role.builder().category(c).name("Domain Level Summary Data").role("ACCOUNTING_SUMMARY_DOMAIN").description("The ability to view summary information for an entire domain").build());
		addRole(Role.builder().category(c).name("User Level Summary Data").role("ACCOUNTING_SUMMARY_USER").description("The ability to view summary information for a user").build());

		Category domainCurrencies = Category.builder().name("Domain Currencies").description("Domain currencies access.").build();
		addRole(Role.builder().category(domainCurrencies).name("Domain Currencies").role("DOMAIN_CURRENCIES").description("Manage domain currencies").build());

		addProvider(ProviderConfig.builder()
			.name(getModuleName())
			.type(ProviderType.ACCOUNTING)
			.build()
		);
	}

	@PostConstruct
	public void init() throws Exception {
		User owner = owners.findByGuid("default/admin");
		if (owner == null) {
			owner = User.builder().guid("default/admin").build();
			owners.save(owner);
		}

		Collection<CurrencyUnit> availableCurrencies = Monetary.getCurrencies();
		for (CurrencyUnit currency: availableCurrencies) {
			Currency c = Currency.builder()
					.name(currency.getCurrencyCode())
					.code(currency.getCurrencyCode())
					.real(true)
					.build();
			if (currencies.findByCode(c.getCode()) == null) {
				log.info(c.toString() + " = " + currency);
				currencies.save(c);
			}
		}

		{
			TransactionType tt = transactionTypeService.findOrCreate("BALANCE_ADJUST");
			transactionTypeService.addAccount(tt, "PLAYER_BALANCE", true, true);
			transactionTypeService.addAccount(tt, "MANUAL_BALANCE_ADJUST", true, true);
			transactionTypeService.addLabel(tt, "comment", false, null, null);
		}

		{
			TransactionType tt = transactionTypeService.findOrCreate("MANUAL_BONUS_VIRTUAL");
			transactionTypeService.addAccount(tt, "PLAYER_BALANCE", true, true);
			transactionTypeService.addAccount(tt, "MANUAL_BONUS_VIRTUAL_ADJUST", true, true);
			transactionTypeService.addLabel(tt, "comment", false, null, null);
		}

		{
			TransactionType tt = transactionTypeService.findOrCreate("RAKE");
			transactionTypeService.addAccount(tt, "PLAYER_BALANCE", true, false);
			transactionTypeService.addAccount(tt, "RAKE", false, true);
			transactionTypeService.addLabel(tt, "handnumber", false, null, null);
			transactionTypeService.addLabel(tt, "gameguid", true, null, null);
			transactionTypeService.addLabel(tt, "gametype", true, null, null);
		}

		/*
		if (transactionTypeService.findByCode("PROCESSOR_DEPOSIT").getData() == null) {
		 	Long ttid = transactionTypeService.create("PROCESSOR_DEPOSIT").getData().getId();
			transactionTypeService.addAccount(ttid, "PLAYER_BALANCE", false, true);
			transactionTypeService.addAccount(ttid, "PROCESSOR", true, false);
			transactionTypeService.addLabel(ttid, "transaction_id", false);
		}

		if (transactionTypeService.findByCode("PROCESSOR_WITHDRAW").getData() == null) {
			Long ttid = transactionTypeService.create("PROCESSOR_WITHDRAW").getData().getId();
			transactionTypeService.addAccount(ttid, "PLAYER_BALANCE", true, false);
			transactionTypeService.addAccount(ttid, "PROCESSOR", false, true);
			transactionTypeService.addLabel(ttid, "transaction_id", false);
		}

		if (transactionTypeService.findByCode("PROCESSOR_TRANSFER").getData() == null) {
			Long ttid = transactionTypeService.create("PROCESSOR_TRANSFER").getData().getId();
			transactionTypeService.addAccount(ttid, "BANKACCOUNT", true, true);
			transactionTypeService.addAccount(ttid, "PROCESSOR", true, true);
			transactionTypeService.addLabel(ttid, "transaction_id", false);
		}
		*/
	}

	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		// @formatter:off
		http.authorizeRequests().antMatchers(HttpMethod.POST,"/system/accounts/rebalance").access("@lithiumSecurity.hasAllRolesForDomain(authentication, #domain, 'PLAYER_EDIT', 'PLAYER_BALANCE_ADJUST')");
		http.authorizeRequests().antMatchers("/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/accountcode/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/account/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/accounttype/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/balance/get/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_VIEW', 'PLAYER_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/balance/{domainName}/adjust/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_BALANCE_ADJUST')");
		http.authorizeRequests().antMatchers("/balance/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers("/balance/v2/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/period/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/transaction/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/historic-ingestion/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/transaction/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/transactiontype/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/summary/domaintrantype/{domain}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'ACCOUNTING_SUMMARY_DOMAIN', 'DASHBOARD')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/summary/domainaccountcode/{domain}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'ACCOUNTING_SUMMARY_DOMAIN', 'DASHBOARD')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/summary/trantype/{domain}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'ACCOUNTING_SUMMARY_USER', 'PLAYER_DASHBOARD_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/summary/account/{domain}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'ACCOUNTING_SUMMARY_USER', 'PLAYER_DASHBOARD_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/summary/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		//TODO this will allow anyone who is authenticated to pull any player's balances
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/player/**").authenticated();
		// The frontend path is system auth, because it needs to be called through service-accounting to determine RO status.
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/frontend/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/currencies/all").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/currencies/search/**").access("@lithiumSecurity.hasRole(authentication, 'DOMAIN_CURRENCIES')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/currencies/findByCode/{code}").access("@lithiumSecurity.hasRole(authentication, 'DOMAIN_CURRENCIES')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/currencies/domain/{domain}/list").authenticated();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/currencies/domain/{domain}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'DOMAIN_CURRENCIES')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/currencies/domain/{domain}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'DOMAIN_CURRENCIES')");
		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/currencies/domain/{domain}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'DOMAIN_CURRENCIES')");

		http.authorizeRequests().antMatchers("/admin/transactions/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'PLAYER_ACCOUNTING_SPORTSBOOK_HISTORY_VIEW','PLAYER_ACCOUNTING_HISTORY_VIEW','GLOBAL_ACCOUNTING_VIEW')");
		http.authorizeRequests().antMatchers("/backoffice/balance-movement/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'PLAYER_ACCOUNTING_SPORTSBOOK_HISTORY_VIEW','PLAYER_ACCOUNTING_HISTORY_VIEW','GLOBAL_ACCOUNTING_VIEW')");

//		Removed because it duplicates the /system/** specified above.
//		http.authorizeRequests().antMatchers("/system/accounting-api-internal/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		// @formatter:on
	}

}
