package lithium.service.leaderboard;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;

@Component
public class LeaderboardModuleInfo extends ModuleInfoAdapter {
	public LeaderboardModuleInfo() {
		super();
		
		roles();
	}
	
	private void roles() {
		Category leaderboardsCategory = Category.builder().name("Leaderboards Operations").description("Operations related to leaderboards.").build();
		addRole(Role.builder().category(leaderboardsCategory).name("Leaderboards").role("LEADERBOARD").description("Manage Leaderboards").build());
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		super.configureHttpSecurity(http);
		
		http.authorizeRequests().antMatchers("/leaderboard/admin/**").access("@lithiumSecurity.hasRole(authentication, 'LEADERBOARD')");
	}
}