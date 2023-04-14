package lithium.service.cashier;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class ServiceCashierModuleInfo extends ModuleInfoAdapter {

	ServiceCashierModuleInfo() {
		super();
		Category category = Category.builder().name("Cashier Operations").description("These are all the roles relevant to managing cashiers.").build();
		addRole(Role.builder().category(category).name("Cashier Start").role("CASHIER_START").description("Load the cashier page").build());
		addRole(Role.builder().category(category).name("Approve Pending Transactions").role("CASHIER_APPROVE").description("Approve pending deposit or withdrawal transactions").build());
		addRole(Role.builder().category(category).name("Cancel Pending Transactions").role("CASHIER_CANCEL").description("Cancel pending deposit or withdrawal transactions").build());
		addRole(Role.builder().category(category).name("Change Pending Transactions Status").role("CASHIER_STATUS_UPDATE").description("Change pending deposit or withdrawal transactions status").build());
		addRole(Role.builder().category(category).name("View Transactions").role("CASHIER_TRANSACTIONS").description("View cashier transactions").build());
		addRole(Role.builder().category(category).name("View Player Transactions").role("PLAYER_CASHIER_TRANSACTIONS").description("View player cashier transactions").build());
		addRole(Role.builder().category(category).name("Configure cashier").role("CASHIER_CONFIG").description("Configure cashier").build());
		addRole(Role.builder().category(category).name("Add cashier config").role("CASHIER_CONFIG_ADD").description("Add cashier config").build());
		addRole(Role.builder().category(category).name("Edit cashier config").role("CASHIER_CONFIG_EDIT").description("Edit cashier config").build());
		addRole(Role.builder().category(category).name("Delete cashier config").role("CASHIER_CONFIG_DELETE").description("Delete cashier config").build());
		addRole(Role.builder().category(category).name("View cashier config").role("CASHIER_CONFIG_VIEW").description("View cashier config").build());
		addRole(Role.builder().category(category).name("View cashier").role("CASHIER_VIEW").description("View cashier related items").build());
		addRole(Role.builder().category(category).name("Add Manual Transactions").role("CASHIER_TRANSACTIONS_MANUAL_ADD").description("Add manual cashier transactions").build());
		addRole(Role.builder().category(category).name("Edit Payment Method Status").role("CASHIER_PAYMENT_METHOD_STATUS_EDIT").description("Edit payment method status").build());
		addRole(Role.builder().category(category).name("Direct Withdrawal").role("CASHIER_DIRECT_WITHDRAWAL").description("Make direct withdrawal for players.").build());
		addRole(Role.builder().category(category).name("Cancel after Approval").role("CASHIER_CANCEL_AFTER_APPROVE").description("Cancel Withdrawal after Approval.").build());
		addRole(Role.builder().category(category).name("View bank account lookup").role("CASHIER_BANK_ACCOUNT_LOOKUP").description("Lookup bank account beneficiary details").build());
		addRole(Role.builder().category(category).name("Developer tools").role("CASHIER_DEV_TOOLS").description("Developers tools").build());
		addRole(Role.builder().category(category).name("Player withdrawal hold/reprocessing").role("CASHIER_WITHDRAWAL_HOLD").description("Make withdrawal hold/reprocessing for players.").build());
		addRole(Role.builder().category(category).name("Manual cashier adjustment operations").role("MANUAL_CASHIER_ADJUSTMENT").description("Allow manual cashier adjustment operations").build());

		Category autoWithdrawalRulesetsCategory = Category.builder().name("Auto-Withdrawal Ruleset Operations").description("These are all the roles relevant to managing auto-withdrawal rulesets.").build();
		addRole(Role.builder().category(autoWithdrawalRulesetsCategory).name("View auto-withdrawal rulesets").role("AUTOWITHDRAWALS_RULESETS_VIEW").description("View auto-withdrawal rulesets").build());
		addRole(Role.builder().category(autoWithdrawalRulesetsCategory).name("Add auto-withdrawal rulesets").role("AUTOWITHDRAWALS_RULESETS_ADD").description("Add auto-withdrawal rulesets").build());
		addRole(Role.builder().category(autoWithdrawalRulesetsCategory).name("Edit auto-withdrawal rulesets").role("AUTOWITHDRAWALS_RULESETS_EDIT").description("Edit auto-withdrawal rulesets").build());
		addRole(Role.builder().category(autoWithdrawalRulesetsCategory).name("Delete auto-withdrawal rulesets").role("AUTOWITHDRAWALS_RULESETS_DELETE").description("Delete auto-withdrawal rulesets").build());
	}

	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		http.headers().frameOptions().disable();
		http.authorizeRequests().antMatchers("/cashier/startCashier").permitAll();
		http.authorizeRequests().antMatchers("/cashier/step/**").permitAll();
		http.authorizeRequests().antMatchers("/cashier/getUserInfo").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers("/cashier/transfer").access("@lithiumSecurity.authenticatedSystem(authentication)");
//		http.authorizeRequests().antMatchers("/cashier/m/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
//		http.authorizeRequests().antMatchers("/cashier/p/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
//		http.authorizeRequests().antMatchers("/cashier/dm/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
//		http.authorizeRequests().antMatchers("/cashier/dmp/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
//		http.authorizeRequests().antMatchers("/cashier/user/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
//		http.authorizeRequests().antMatchers("/cashier/profile/**").access("@lithiumSecurity.authenticatedSystem(authentication)");

		http.authorizeRequests().antMatchers("/cashier/m/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'CASHIER_TRANSACTIONS','PLAYER_CASHIER_TRANSACTIONS','CASHIER_CONFIG', 'CASHIER_CONFIG_VIEW', 'AUTOWITHDRAWALS_RULESETS_ADD','AUTOWITHDRAWALS_RULESETS_VIEW', 'PLAYER_CASHIER_TRANSACTIONS_VIEW')");
		http.authorizeRequests().antMatchers("/cashier/p/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'CASHIER_CONFIG', 'CASHIER_CONFIG_VIEW')");
		http.authorizeRequests().antMatchers("/cashier/dm/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'CASHIER_TRANSACTIONS','PLAYER_CASHIER_TRANSACTIONS','CASHIER_CONFIG', 'CASHIER_CONFIG_VIEW', 'PLAYER_CASHIER_TRANSACTIONS_VIEW')");
		http.authorizeRequests().antMatchers("/cashier/pmc/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'CASHIER_TRANSACTIONS','PLAYER_CASHIER_TRANSACTIONS','CASHIER_CONFIG', 'CASHIER_CONFIG_VIEW', 'PLAYER_CASHIER_TRANSACTIONS_VIEW')");
		http.authorizeRequests().antMatchers("/cashier/direct-withdrawal/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'CASHIER_TRANSACTIONS','PLAYER_CASHIER_TRANSACTIONS','CASHIER_CONFIG', 'CASHIER_CONFIG_VIEW', 'PLAYER_CASHIER_TRANSACTIONS_VIEW', 'CASHIER_DIRECT_WITHDRAWAL')");
		http.authorizeRequests().antMatchers("/cashier/manual-withdrawal/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'CASHIER_TRANSACTIONS','PLAYER_CASHIER_TRANSACTIONS','CASHIER_CONFIG', 'CASHIER_CONFIG_VIEW', 'PLAYER_CASHIER_TRANSACTIONS_VIEW', 'CASHIER_DIRECT_WITHDRAWAL')");

		http.authorizeRequests().antMatchers("/cashier/dmp/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'CASHIER_CONFIG', 'CASHIER_CONFIG_VIEW')");
		http.authorizeRequests().antMatchers("/cashier/user/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'CASHIER_CONFIG', 'CASHIER_CONFIG_VIEW')");
		http.authorizeRequests().antMatchers("/cashier/profile/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'CASHIER_CONFIG', 'CASHIER_CONFIG_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.GET,"/cashier/profile/{domainName}").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'CASHIER_CONFIG', 'CASHIER_CONFIG_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.POST,"/cashier/profile/{domainName}").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'CASHIER_CONFIG', 'CASHIER_CONFIG_ADD')");
		http.authorizeRequests().antMatchers(HttpMethod.POST,"/cashier/profile/{domainName}/{id}/delete").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'CASHIER_CONFIG', 'CASHIER_CONFIG_DELETE')");

		http.authorizeRequests().antMatchers(HttpMethod.POST, "/cashier/transaction/xls").access("@lithiumSecurity.hasRoleInTree(authentication, 'CASHIER_TRANSACTIONS','PLAYER_CASHIER_TRANSACTIONS', 'PLAYER_CASHIER_TRANSACTIONS_VIEW')");
		http.authorizeRequests().antMatchers("/cashier/transaction/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'CASHIER_TRANSACTIONS','PLAYER_CASHIER_TRANSACTIONS','CASHIER_CONFIG', 'CASHIER_CONFIG_VIEW', 'PLAYER_CASHIER_TRANSACTIONS_VIEW', 'PLAYER_VIEW')");
		http.authorizeRequests().antMatchers("/cashier/manual/transaction/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'CASHIER_TRANSACTIONS_MANUAL_ADD')");
		http.authorizeRequests().antMatchers("/cashier/manual/transaction/{domainName}/payment-methods/{id}/status-update").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'CASHIER_PAYMENT_METHOD_STATUS_EDIT')");

		http.authorizeRequests().antMatchers("/populate-transactions-payment-methods-job/**").authenticated();
		http.authorizeRequests().antMatchers("/internal/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers("/external/**").permitAll();

		http.authorizeRequests().antMatchers("/frontend/loading").permitAll();
		http.authorizeRequests().antMatchers("/frontend/loadingrefresh").permitAll();
		http.authorizeRequests().antMatchers("/frontend/opencashier").permitAll();
		http.authorizeRequests().antMatchers("/frontend/closepage").permitAll();
		http.authorizeRequests().antMatchers("/frontend/**").authenticated();
		http.authorizeRequests().antMatchers("/admin/{domain}/changestatus/**").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'CASHIER_APPROVE', 'CASHIER_CANCEL', 'CASHIER_CANCEL_AFTER_APPROVE',' CASHIER_STATUS_UPDATE')");

		http.authorizeRequests().antMatchers(HttpMethod.GET, "/admin/auto-withdrawal/ruleset/rule/fields").access("@lithiumSecurity.hasRoleInTree(authentication, 'AUTOWITHDRAWALS_RULESETS_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/admin/auto-withdrawal/ruleset/rule/operators").access("@lithiumSecurity.hasRoleInTree(authentication, 'AUTOWITHDRAWALS_RULESETS_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/admin/auto-withdrawal/ruleset/table").access("@lithiumSecurity.hasRoleInTree(authentication, 'AUTOWITHDRAWALS_RULESETS_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/admin/auto-withdrawal/ruleset/*").access("@lithiumSecurity.hasRoleInTree(authentication, 'AUTOWITHDRAWALS_RULESETS_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/admin/auto-withdrawal/ruleset/rule/{domainName}/init-data/*").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'AUTOWITHDRAWALS_RULESETS_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/admin/auto-withdrawal/ruleset/{domainName}/create").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'AUTOWITHDRAWALS_RULESETS_ADD')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/admin/auto-withdrawal/ruleset/{domainName}/update").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'AUTOWITHDRAWALS_RULESETS_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/admin/auto-withdrawal/ruleset/{domainName}/*/changename").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'AUTOWITHDRAWALS_RULESETS_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/admin/auto-withdrawal/ruleset/{domainName}/*/change-delay").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'AUTOWITHDRAWALS_RULESETS_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/admin/auto-withdrawal/ruleset/{domainName}/*/delete").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'AUTOWITHDRAWALS_RULESETS_DELETE')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/admin/auto-withdrawal/ruleset/{domainName}/*/toggle/enabled").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'AUTOWITHDRAWALS_RULESETS_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/admin/auto-withdrawal/ruleset/{domainName}/*/rule/add").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'AUTOWITHDRAWALS_RULESETS_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/admin/auto-withdrawal/ruleset/{domainName}/*/rule/*/delete").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'AUTOWITHDRAWALS_RULESETS_DELETE')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/admin/auto-withdrawal/ruleset/{domainName}/*/rule/*/update").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'AUTOWITHDRAWALS_RULESETS_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/admin/auto-withdrawal/ruleset/{domainName}/*/queueprocess").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'AUTOWITHDRAWALS_RULESETS_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/admin/auto-withdrawal/ruleset/*/changelogs").access("@lithiumSecurity.hasRoleInTree(authentication, 'AUTOWITHDRAWALS_RULESETS_VIEW')");

		http.authorizeRequests().antMatchers("/backoffice/cashier/bank-account-lookup/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'CASHIER_BANK_ACCOUNT_LOOKUP')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/cashier/banks").authenticated();
		http.authorizeRequests().antMatchers("/backoffice/dev-tools/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'CASHIER_DEV_TOOLS')");

		http.authorizeRequests().antMatchers("/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");

		http.authorizeRequests().antMatchers("/backoffice/auto-withdrawal/**").access("@lithiumSecurity.hasRoleInTree(authentication,'PLAYER_AUTO_WITHDRAWAL_EDIT')");

		http.authorizeRequests().antMatchers("/complete-placeholders/**").access("@lithiumSecurity.authenticatedSystem(authentication)");

        http.authorizeRequests().antMatchers("/backoffice/cashier/transaction-bulk-processing/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'CASHIER_APPROVE', 'CASHIER_WITHDRAWAL_HOLD', 'CASHIER_CANCEL')");

        http.authorizeRequests().antMatchers(HttpMethod.GET, "/data-migration-job/**").access("@lithiumSecurity.hasRole(authentication, 'ADMIN')");

		http.authorizeRequests().antMatchers("/backoffice/cashier/transaction-tags-list").access("@lithiumSecurity.hasRoleInTree(authentication, 'PLAYER_CASHIER_TRANSACTIONS_VIEW', 'CASHIER_TRANSACTIONS', 'PLAYER_CASHIER_TRANSACTIONS')");
        http.authorizeRequests().antMatchers("/player-transaction-statistics/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'CASHIER_TRANSACTIONS','PLAYER_CASHIER_TRANSACTIONS','CASHIER_CONFIG', 'CASHIER_CONFIG_VIEW', 'PLAYER_CASHIER_TRANSACTIONS_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/cashier/transaction/reverse").access("@lithiumSecurity.hasRoleInTree(authentication, #domainName, 'MANUAL_CASHIER_ADJUSTMENT')");

        http.authorizeRequests().antMatchers("/backoffice/cashier/transactions/{transactionId}/tags/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'PLAYER_CASHIER_TRANSACTIONS_VIEW', 'CASHIER_TRANSACTIONS', 'PLAYER_CASHIER_TRANSACTIONS')");
	}
}
