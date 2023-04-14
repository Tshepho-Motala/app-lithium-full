package lithium.service.promo;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;

@Component
public class ServicePromotionsModuleInfo extends ModuleInfoAdapter {
	public ServicePromotionsModuleInfo() {
		super();
		
		roles();
	}
	
	private void roles() {
		Category missionsCategory = Category.builder().name("Promotions Operations").description("Operations related to promotions.").build();
		addRole(Role.builder().category(missionsCategory).name("Promotions View").role("PROMOTIONS_VIEW").description("View Promotions").build());
		addRole(Role.builder().category(missionsCategory).name("Promotions Edit").role("PROMOTIONS_EDIT").description("Edit Promotions").build());
		
		Category userMissionsCategory = Category.builder().name("User Promotions Operations").description("Operations related to user promotions.").build();
		addRole(Role.builder().category(userMissionsCategory).name("User Promotions").role("USER_PROMOTIONS_VIEW").description("View User Promotions").build());
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		super.configureHttpSecurity(http);

		http.authorizeRequests().antMatchers("/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers("/promotions/user").authenticated();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/{domainName}/provider/list-activity-by-category/{category}").permitAll();

		http.authorizeRequests().antMatchers("/backoffice/promotions/table").access("@lithiumSecurity.hasRole(authentication, 'PROMOTIONS_VIEW', 'PROMOTIONS_EDIT')");
		http.authorizeRequests().antMatchers("/backoffice/promotions/create").access("@lithiumSecurity.hasRole(authentication, 'PROMOTIONS_EDIT')");
		http.authorizeRequests().antMatchers("/backoffice/promotions/rule/operations").access("@lithiumSecurity.hasRole(authentication, 'PROMOTIONS_VIEW')");
		http.authorizeRequests().antMatchers("/backoffice/promotions/get-promotions-with-events-in-period").access("@lithiumSecurity.hasRole(authentication, 'PROMOTIONS_VIEW')");
		http.authorizeRequests().antMatchers("/backoffice/promotions/get-disabled-promotions-between-dates").access("@lithiumSecurity.hasRole(authentication, 'PROMOTIONS_VIEW')");


		http.authorizeRequests().antMatchers(HttpMethod.GET,"/backoffice/promotion/v1/**").access("@lithiumSecurity.hasRole(authentication, 'PROMOTIONS_VIEW')");
		http.authorizeRequests().antMatchers("/backoffice/promotion/v1/create-draft").access("@lithiumSecurity.hasRole(authentication, 'PROMOTIONS_EDIT')");
		http.authorizeRequests().antMatchers("/backoffice/promotion/v1/edit-draft").access("@lithiumSecurity.hasRole(authentication, 'PROMOTIONS_EDIT')");
		http.authorizeRequests().antMatchers("/backoffice/promotion/v1/mark-draft-final/**").access("@lithiumSecurity.hasRole(authentication, 'PROMOTIONS_EDIT')");
		http.authorizeRequests().antMatchers("/backoffice/promotion/v1/toggle-enabled/{id}").access("@lithiumSecurity.hasRole(authentication, 'PROMOTIONS_EDIT')");
		http.authorizeRequests().antMatchers("/backoffice/promotion/v1/delete/{id}").access("@lithiumSecurity.hasRole(authentication, 'PROMOTIONS_EDIT')");
		http.authorizeRequests().antMatchers("/backoffice/promotion/v1/get-disabled-promotions-between-dates").access("@lithiumSecurity.hasRole(authentication, 'PROMOTIONS_VIEW')");


		http.authorizeRequests().antMatchers("/backoffice/promotion/{id}").access("@lithiumSecurity.hasRole(authentication, 'PROMOTIONS_VIEW', 'PROMOTIONS_EDIT')");
		http.authorizeRequests().antMatchers("/backoffice/promotion/{id}/revision/{missionRevisionId}").access("@lithiumSecurity.hasRole(authentication, 'PROMOTIONS_VIEW', 'PROMOTIONS_EDIT')");
		http.authorizeRequests().antMatchers("/backoffice/promotion/{id}/revisions").access("@lithiumSecurity.hasRole(authentication, 'PROMOTIONS_VIEW', 'PROMOTIONS_EDIT')");
		http.authorizeRequests().antMatchers("/backoffice/promotion/{id}/modify").access("@lithiumSecurity.hasRole(authentication, 'PROMOTIONS_EDIT')");
		http.authorizeRequests().antMatchers("/backoffice/promotion/{id}/modifyAndSaveCurrent").access("@lithiumSecurity.hasRole(authentication, 'PROMOTIONS_EDIT')");
		http.authorizeRequests().antMatchers("/backoffice/promotion/{id}/addChallenge").access("@lithiumSecurity.hasRole(authentication, 'PROMOTIONS_EDIT')");
		http.authorizeRequests().antMatchers("/backoffice/promotion/{id}/removeChallenge/{challengeId}").access("@lithiumSecurity.hasRole(authentication, 'PROMOTIONS_EDIT')");
		http.authorizeRequests().antMatchers("/backoffice/promotion/{id}/modifyChallenge/{challengeId}").access("@lithiumSecurity.hasRole(authentication, 'PROMOTIONS_EDIT')");
//		http.authorizeRequests().antMatchers("/admin/promotion/{id}/challenge/{challengeId}/getIcon").access("@lithiumSecurity.hasDomainRole(authentication, 'MISSIONS_VIEW', 'MISSIONS_EDIT')");
		http.authorizeRequests().antMatchers("/backoffice/promotion/{id}/challenge/{challengeId}/addChallengeRule").access("@lithiumSecurity.hasRole(authentication, 'PROMOTIONS_EDIT')");
		http.authorizeRequests().antMatchers("/backoffice/promotion/{id}/challenge/{challengeId}/removeChallengeRule/{challengeRuleId}").access("@lithiumSecurity.hasRole(authentication, 'PROMOTIONS_EDIT')");
		http.authorizeRequests().antMatchers("/backoffice/promotion/{id}/challenge/{challengeId}/modifyChallengeRule/{challengeRuleId}").access("@lithiumSecurity.hasRole(authentication, 'PROMOTIONS_EDIT')");
		http.authorizeRequests().antMatchers("/backoffice/promotion/{id}/add-grouped-challenges").access("@lithiumSecurity.hasRole(authentication, 'PROMOTIONS_EDIT')");

		http.authorizeRequests().antMatchers("/backoffice/{domainName}/provider/**").access("@lithiumSecurity.hasRole(authentication, 'PROMOTIONS_VIEW')");

		http.authorizeRequests().antMatchers(HttpMethod.POST,"/backoffice/promotion/{id}/user-categories/add").access("@lithiumSecurity.hasRole(authentication, 'PROMOTIONS_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.DELETE,"/backoffice/promotion/{id}/user-categories/{userCategory}").access("@lithiumSecurity.hasRole(authentication, 'PROMOTIONS_EDIT')");
		
		http.authorizeRequests().antMatchers("/backoffice/user-promotions/**").access("@lithiumSecurity.hasRole(authentication, 'USER_PROMOTIONS_VIEW', 'PLAYER_PROMOTIONS_VIEW')");

		http.authorizeRequests().antMatchers(HttpMethod.GET,"/backoffice/promotion/{id}/challenge/{challengeId}/getIcon").permitAll();

		http.authorizeRequests().antMatchers("/backoffice/{domainName}/provider/{providerUrl}/{field}").authenticated();

		http.authorizeRequests().antMatchers("/external/find-promotions").permitAll();
		http.authorizeRequests().antMatchers("/external/add-players").permitAll();

		http.authorizeRequests().antMatchers("/frontend/player/**").authenticated();
	}
}
