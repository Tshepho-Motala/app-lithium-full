package lithium.service.accountinghistory;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import lithium.modules.ModuleInfoAdapter;
import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
public class ServiceAccountingHistoryModuleInfo extends ModuleInfoAdapter {
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		// @formatter:off
		http.authorizeRequests().antMatchers("/admin/transactions/table").access("@lithiumSecurity.hasRoleInTree(authentication, 'PLAYER_ACCOUNTING_SPORTSBOOK_HISTORY_VIEW','PLAYER_ACCOUNTING_HISTORY_VIEW','GLOBAL_ACCOUNTING_VIEW')");
		http.authorizeRequests().antMatchers("/backoffice/balance-movement/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'PLAYER_ACCOUNTING_SPORTSBOOK_HISTORY_VIEW','PLAYER_ACCOUNTING_HISTORY_VIEW','GLOBAL_ACCOUNTING_VIEW')");

		// @formatter:on
	}

}
