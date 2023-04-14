package lithium.service.limit;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class ServiceLimitModuleInfo extends ModuleInfoAdapter {

	public ServiceLimitModuleInfo() {
		//TODO: Add roles if required
		roles();
	}

	private void roles() {
		Category cooloffCategory = Category.builder().name("Cooling off Operations").description("Operations related to player cooling off.").build();
		addRole(Role.builder().category(cooloffCategory).name("Cooling off Add").role("COOLOFF_ADD").description("Add cooling off").build());
		addRole(Role.builder().category(cooloffCategory).name("Cooling off Clear").role("COOLOFF_CLEAR").description("Clear cooling off").build());

		//Category depLimitCategory = Category.builder().name("Player Deposit Limit Operations").description("Operations related to player deposit limits.").build();
		//addRole(Role.builder().category(depLimitCategory).name("Player Deposit Limit Edit").role("PLAYER_DEP_LIMIT_EDIT").description("Edit Player Deposit Limits").build());

		Category exclusionCategory = Category.builder().name("Exclusion Operations").description("Operations related to player exclusion.").build();
		addRole(Role.builder().category(exclusionCategory).name("Exclusion Add").role("EXCLUSION_ADD").description("Exclude players").build());
		addRole(Role.builder().category(exclusionCategory).name("Exclusion Revoke").role("EXCLUSION_REVOKE").description("Revoke player exclusion").build());

		Category restrictionsOperations = Category.builder().name("Restrictions Operations").description("These are all the roles relevant to managing restrictions.").build();
		addRole(Role.builder().category(restrictionsOperations).name("View restrictions").role("RESTRICTIONS_VIEW").description("View restrictions").build());
		addRole(Role.builder().category(restrictionsOperations).name("Add restrictions").role("RESTRICTIONS_ADD").description("Add restrictions").build());
		addRole(Role.builder().category(restrictionsOperations).name("Edit restrictions").role("RESTRICTIONS_EDIT").description("Edit restrictions").build());

		Category autoRestrictionsRulesetsCategory = Category.builder().name("Auto-Restriction Ruleset Operations").description("These are all the roles relevant to managing auto-restriction rulesets.").build();
		addRole(Role.builder().category(autoRestrictionsRulesetsCategory).name("View auto-restriction rulesets").role("AUTORESTRICTION_RULESETS_VIEW").description("View auto-restriction rulesets").build());
		addRole(Role.builder().category(autoRestrictionsRulesetsCategory).name("Add auto-restriction rulesets").role("AUTORESTRICTION_RULESETS_ADD").description("Add auto-restriction rulesets").build());
		addRole(Role.builder().category(autoRestrictionsRulesetsCategory).name("Edit auto-restriction rulesets").role("AUTORESTRICTION_RULESETS_EDIT").description("Edit auto-restriction rulesets").build());

		Category userRestrictionsOperations = Category.builder().name("User Restrictions Operations").description("These are all the roles relevant to managing user restrictions.").build();
		addRole(Role.builder().category(userRestrictionsOperations).name("Set user restrictions").role("USER_RESTRICTIONS_SET").description("Set user restrictions").build());
		addRole(Role.builder().category(userRestrictionsOperations).name("Lift user restrictions").role("USER_RESTRICTIONS_LIFT").description("Lift user restrictions").build());

		Category userRealityCheckOperations = Category.builder().name("User Reality Check Operations").description("These are all the roles relevant to managing user reality check.").build();
		addRole(Role.builder().category(userRealityCheckOperations).name("Add User Reality Check interval").role("USER_REALITYCHECK_ADD").description("Add user reality check").build());
		addRole(Role.builder().category(userRealityCheckOperations).name("View User Reality Check interval").role("USER_REALITYCHECK_VIEW").description("View user reality check").build());
		addRole(Role.builder().category(userRealityCheckOperations).name("Edit User Reality Check interval").role("USER_REALITYCHECK_EDIT").description("Edit user reality check").build());

		Category systemAccessOperations = Category.builder().name("Limit System Access Operations").description("These are all the roles relevant to limiting system access.").build();
		addRole(Role.builder().category(systemAccessOperations).name("View system access limits").role("SYSTEMACCESS_VIEW").description("View system access limits").build());
		addRole(Role.builder().category(systemAccessOperations).name("Edit system access limits").role("SYSTEMACCESS_EDIT").description("Edit system access limits").build());

		Category brandConfigurations = Category.builder().name("Configure brand specific settings").description("These are all the roles relevant to brand configurations.").build();
		addRole(Role.builder().category(brandConfigurations).name("View brand configuration").role("BRAND_CONFIG_VIEW").description("View brand configurations").build());
		addRole(Role.builder().category(brandConfigurations).name("Edit brand configuration").role("BRAND_CONFIG_EDIT").description("Edit brand configurations").build());
		addRole(Role.builder().category(brandConfigurations).name("Set brand configuration").role("BRAND_CONFIG_SET").description("Set brand configurations").build());
		addRole(Role.builder().category(brandConfigurations).name("Clear brand configuration").role("BRAND_CONFIG_CLEAR").description("Clear brand configurations").build());

		Category ecosystemOperations = Category.builder().name("Ecosystems operations").description("These are all the roles relevant to ecosystems.").build();
		addRole(Role.builder().category(ecosystemOperations).name("View ecosystems").role("ECOSYSTEMS_VIEW").description("View ecosystems").build());
		addRole(Role.builder().category(ecosystemOperations).name("Add ecosystems").role("ECOSYSTEMS_ADD").description("Add ecosystems").build());
		addRole(Role.builder().category(ecosystemOperations).name("Edit ecosystems").role("ECOSYSTEMS_EDIT").description("Edit ecosystems").build());

		Category playerTimeSlotOperations = Category.builder().name("Player Time Slot Operations").description("These are all the roles relevant to player timeslot operations.").build();
		addRole(Role.builder().category(playerTimeSlotOperations).name("View player time slot").role("PLAYER_TIME_SLOT_VIEW").description("View player time slot").build());
		addRole(Role.builder().category(playerTimeSlotOperations).name("Add player time slot").role("PLAYER_TIME_SLOT_ADD").description("Add player time slot").build());
		addRole(Role.builder().category(playerTimeSlotOperations).name("Edit player time slot").role("PLAYER_TIME_SLOT_EDIT").description("Edit player time slot").build());
		addRole(Role.builder().category(playerTimeSlotOperations).name("Delete player time slot").role("PLAYER_TIME_SLOT_REMOVE").description("Remove player time slot").build());
	}

	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/public/**").permitAll();
		http.authorizeRequests().antMatchers("/external/**").permitAll();

		http.authorizeRequests().antMatchers("/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers("/system/historic-self-exclusion-ingestion/record").access("@lithiumSecurity.authenticatedSystem(authentication)");


		http.authorizeRequests().antMatchers("/frontend/player-limit/v1/**").authenticated();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/player-limit/v1/{domainName}/net-loss-to-house").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'DOMAIN_VIEW', 'DOMAIN_EDIT', 'PLAYER_VIEW', 'PLAYER_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/player-limit/v1/{domainName}/find-domain-limit").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'DOMAIN_VIEW', 'DOMAIN_EDIT', 'PLAYER_VIEW', 'PLAYER_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/player-limit/v1/{domainName}/set-domain-limit").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'DOMAIN_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/backoffice/player-limit/v1/{domainName}/remove-domain-limit").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'DOMAIN_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/player-limit/v1/{domainName}/find-player-limit").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_VIEW', 'PLAYER_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/player-limit/v1/{domainName}/find-player-limit").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_VIEW', 'PLAYER_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/player-limit/v1/{domainName}/set-player-limit").access("@lithiumSecurity.hasDomainRole(authentication, #domainName,'PLAYER_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/backoffice/player-limit/v1/{domainName}/remove-player-limit").access("@lithiumSecurity.hasDomainRole(authentication, #domainName,'PLAYER_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/player-limit/v1/{domainName}/set-player-time-slot-limit").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_VIEW', 'PLAYER_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/player-limit/v1/{domainName}/get-player-time-slot-limit").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_VIEW', 'PLAYER_EDIT','PLAYER_TIME_SLOT_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/backoffice/player-limit/v1/{domainName}/remove-player-time-slot-limit").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_VIEW', 'PLAYER_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/player-limit/v1/{domainName}/check-player-time-slot-limit").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_VIEW', 'PLAYER_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/player-limit/v1/{domainName}/get-loss-limit-visibility").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_VIEW', 'PLAYER_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/player-limit/v1/{domainName}/set-loss-limit-visibility").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_VIEW', 'PLAYER_EDIT')");

		http.authorizeRequests().antMatchers("/frontend/player-limit/v2/**").authenticated();

		http.authorizeRequests().antMatchers("/frontend/cooloff/v1/**").authenticated();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/cooloff/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_VIEW', 'PLAYER_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/cooloff/{domainName}/set").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'COOLOFF_ADD')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/cooloff/{domainName}/clear").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'COOLOFF_CLEAR')");

		http.authorizeRequests().antMatchers("/frontend/depositlimit/v1/**").authenticated();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/depositlimit/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_VIEW', 'PLAYER_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/depositlimit/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_DEPOSIT_LIMIT_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/backoffice/depositlimit/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_DEPOSIT_LIMIT_EDIT')");

		http.authorizeRequests().antMatchers("/frontend/balance-limit/v1/**").authenticated();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/balance-limit/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_VIEW', 'PLAYER_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/balance-limit/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_BALANCE_LIMIT_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/backoffice/balance-limit/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_BALANCE_LIMIT_EDIT')");

		http.authorizeRequests().antMatchers("/frontend/exclusion/v2/**").authenticated();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/exclusion/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_VIEW', 'PLAYER_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/exclusion/{domainName}/set").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'EXCLUSION_ADD')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/exclusion/{domainName}/clear").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'EXCLUSION_REVOKE')");

		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/restrictions/create").access("@lithiumSecurity.hasRoleInTree(authentication, 'RESTRICTIONS_ADD')");
		http.authorizeRequests().antMatchers(HttpMethod.GET,"/backoffice/restrictions/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'RESTRICTIONS_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/restrictions/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'RESTRICTIONS_EDIT')");

		http.authorizeRequests().antMatchers(HttpMethod.GET,"/backoffice/auto-restriction/helper/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'AUTORESTRICTION_RULESETS_VIEW', 'AUTORESTRICTION_RULESETS_ADD', 'AUTORESTRICTION_RULESETS_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.GET,"/backoffice/auto-restriction/rulesets/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'AUTORESTRICTION_RULESETS_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/auto-restriction/ruleset/{domainName}/create").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'AUTORESTRICTION_RULESETS_ADD')");
		http.authorizeRequests().antMatchers(HttpMethod.GET,"/backoffice/auto-restriction/ruleset/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'AUTORESTRICTION_RULESETS_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/auto-restriction/ruleset/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'AUTORESTRICTION_RULESETS_EDIT')");

		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/user-restrictions/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_VIEW', 'PLAYER_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST,"/backoffice/user-restrictions/{domainName}/set/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'USER_RESTRICTIONS_SET')");
		http.authorizeRequests().antMatchers(HttpMethod.POST,"/backoffice/user-restrictions/{domainName}/lift/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'USER_RESTRICTIONS_LIFT')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/frontend/user-restrictions/**").authenticated();

        http.authorizeRequests().antMatchers(HttpMethod.GET,"/backoffice/{domainName}/domain-restrictions/list-limits").access("@lithiumSecurity.hasDomainRole(authentication,#domainName, 'RESTRICTIONS_VIEW', 'SYSTEMACCESS_VIEW', 'SYSTEMACCESS_EDIT')");
        http.authorizeRequests().antMatchers(HttpMethod.POST,"/backoffice/{domainName}/domain-restrictions/save-limit-system-access").access("@lithiumSecurity.hasDomainRole(authentication,#domainName, 'RESTRICTIONS_EDIT', 'SYSTEMACCESS_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.GET,"/backoffice/{domainName}/domain-restrictions/changelogs").access("@lithiumSecurity.hasDomainRole(authentication,#domainName, 'RESTRICTIONS_VIEW', 'SYSTEMACCESS_VIEW', 'SYSTEMACCESS_EDIT')");
		http.authorizeRequests().antMatchers("/backoffice/verification/status/all").authenticated();

		http.authorizeRequests().antMatchers("/frontend/reality-check/v1/**").authenticated();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/reality-check/v1/{domainName}/get/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName,'USER_REALITYCHECK_VIEW', 'PLAYER_RESPONSIBLE_GAMING_VIEW', 'PLAYER_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/reality-check/v1/{domainName}/getlistinmillis").authenticated();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/reality-check/v1/{domainName}/getlistinmins").authenticated();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/reality-check/v1/{domainName}/audit/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName,'USER_REALITYCHECK_VIEW', 'PLAYER_RESPONSIBLE_GAMING_VIEW', 'PLAYER_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/reality-check/v1/{domainName}/set/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName,'USER_REALITYCHECK_EDIT','USER_REALITYCHECK_ADD')");

		http.authorizeRequests().antMatchers("/backoffice/domain-age-limit/v1/**").authenticated();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/domain-age-limit/v1/find-age-limits/{domainName}").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'BRAND_CONFIG_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/domain-age-limit/v1/check-age-range/{age}/{domainName}").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'BRAND_CONFIG_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/domain-age-limit/v1/set-age-limit").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'BRAND_CONFIG_SET')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/domain-age-limit/v1/check-age-range").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'BRAND_CONFIG_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/domain-age-limit/v1/edit-age-limit").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'BRAND_CONFIG_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/domain-age-limit/v1/edit-age-limit-min-max").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'BRAND_CONFIG_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/domain-age-limit/v1/set-age-limit-group").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'BRAND_CONFIG_SET')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/domain-age-limit/v1/remove-domain-age-limit-group").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'BRAND_CONFIG_CLEAR')");
		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/backoffice/domain-age-limit/v1/remove-domain-age-limit/{id}").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'BRAND_CONFIG_CLEAR')");

		http.authorizeRequests().antMatchers("/frontend/limits-summary/v1/**").authenticated();
		http.authorizeRequests().antMatchers("/complete-placeholders/**").access("@lithiumSecurity.authenticatedSystem(authentication)");

		http.authorizeRequests().antMatchers(HttpMethod.GET,"/data-migration-job/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'ADMIN')");

	}
}
