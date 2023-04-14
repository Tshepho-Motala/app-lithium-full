package lithium.service.accounting;


import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class ServiceAccountingModuleInfo extends ModuleInfoAdapter {

	/**
	 *
	 */
	public ServiceAccountingModuleInfo(){
		Role.Category accounting = Role.Category.builder().name("Accounting Transactions").description("Accounting Transactions").build();
		addRole(Role.builder().category(accounting).name("Player Accounting Sportsbook History View").role("PLAYER_ACCOUNTING_SPORTSBOOK_HISTORY_VIEW").description("View accounting sportsbook history for a player.").build());
		addRole(Role.builder().category(accounting).name("Player Accounting History View").role("PLAYER_ACCOUNTING_HISTORY_VIEW").description("View accounting history for a player.").build());
		addRole(Role.builder().category(accounting).name("Player Accounting View").role("PLAYER_ACCOUNTING_VIEW").description("View accounting for a player.").build());
		addRole(Role.builder().category(accounting).name("Global Accounting View").role("GLOBAL_ACCOUNTING_VIEW").description("View Global Accounting").build());
	}

	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		// @formatter:off
		http.authorizeRequests().antMatchers( "/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/accountcode/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/account/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/accounttype/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/balance/get/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_VIEW', 'PLAYER_EDIT', 'CASHIER_APPROVE', 'CASHIER_CANCEL', 'CASHIER_CANCEL_AFTER_APPROVE', 'CASHIER_STATUS_UPDATE')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/balance/getAllByOwnerGuid").access("@lithiumSecurity.hasRole(authentication, 'PLAYER_VIEW', 'PLAYER_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/balance/{domainName}/getAllByOwnerGuid").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_VIEW', 'PLAYER_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/balance/{domainName}/adjust/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_BALANCE_ADJUST')");
		http.authorizeRequests().antMatchers("/balance/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers("/balance/v2/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/period/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/transaction/find-external-transaction-id").authenticated();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/transaction/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/transaction/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/transactiontype/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/summary/domaintrantype/{domain}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'ACCOUNTING_SUMMARY_DOMAIN', 'DASHBOARD')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/summary/domainlabelvalue/{domain}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'ACCOUNTING_SUMMARY_DOMAIN', 'DASHBOARD')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/summary/trantype/{domain}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'ACCOUNTING_SUMMARY_USER', 'PLAYER_DASHBOARD_VIEW', 'PLAYER_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/summary/account/{domain}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'ACCOUNTING_SUMMARY_USER', 'PLAYER_DASHBOARD_VIEW', 'PLAYER_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/summary/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		//		nyone who is authenticated to pull any player's balances
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/player/**").authenticated();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/frontend/**").authenticated();
		http.authorizeRequests().antMatchers("/admin/transactions/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'PLAYER_ACCOUNTING_SPORTSBOOK_HISTORY_VIEW','PLAYER_ACCOUNTING_HISTORY_VIEW','GLOBAL_ACCOUNTING_VIEW')");
		http.authorizeRequests().antMatchers("/backoffice/balance-movement/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'PLAYER_ACCOUNTING_SPORTSBOOK_HISTORY_VIEW','PLAYER_ACCOUNTING_HISTORY_VIEW','GLOBAL_ACCOUNTING_VIEW')");

		// @formatter:on
	}

}
