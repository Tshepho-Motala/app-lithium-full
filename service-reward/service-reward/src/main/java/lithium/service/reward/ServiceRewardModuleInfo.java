package lithium.service.reward;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import javax.ws.rs.HttpMethod;

@Component
public class ServiceRewardModuleInfo extends ModuleInfoAdapter {

  public ServiceRewardModuleInfo() {
    		Category category = Role.Category.builder().name("Reward Operations").description("These are all the roles relevant to managing rewards.").build();
    		addRole(Role.builder().category(category).name("Rewards Manage").role("REWARDS_MANAGE").description("Manage rewards.").build());
            addRole(Role.builder().category(category).name("Reward Grant").role("REWARD_GRANT").description("Gives the ability to grant a reward").build());
            addRole(Role.builder().category(category).name("View Player Reward History").role("PLAYER_REWARD_HISTORY_VIEW").description("Manage rewards.").build());
    addRole(Role.builder().category(category).name("Cancel Player Reward").role("PLAYER_REWARD_CANCEL").description("Manage rewards.").build());
  }

  @Override
  public void configureHttpSecurity(HttpSecurity http) throws Exception {
    // @formatter:off
    http.authorizeRequests().antMatchers("/frontend/player/**").authenticated();
    http.authorizeRequests().antMatchers( "/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
    http.authorizeRequests().antMatchers( "/backoffice/{domainName}/games/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'REWARDS_MANAGE')");
    http.authorizeRequests().antMatchers("/backoffice/{domainName}/rewards/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'REWARDS_MANAGE')");
    http.authorizeRequests().antMatchers("/backoffice/reward-types/**").access("@lithiumSecurity.hasRole(authentication, 'REWARDS_MANAGE')");
    http.authorizeRequests().antMatchers("/backoffice/reward-revisions/**").access("@lithiumSecurity.hasRole(authentication, 'REWARDS_MANAGE')");
    http.authorizeRequests().antMatchers("/backoffice/{domainName}/providers/**").access("@lithiumSecurity.hasRole(authentication, 'REWARDS_MANAGE')");
    http.authorizeRequests().antMatchers("/backoffice/{domainName}/reward/v1").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'REWARD_GRANT')");
    http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/{domainName}/rewards/player/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_REWARD_HISTORY_VIEW')");

    http.authorizeRequests().antMatchers("/backoffice/{domainName}/rewards/player/{playerRewardHistoryId}/cancel-reward").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_REWARD_CANCEL')");
    http.authorizeRequests().antMatchers("/backoffice/{domainName}/rewards/player/{playerRewardTypeHistoryId}/cancel-reward-type").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_REWARD_CANCEL')");
    // @formatter:on
  }
}