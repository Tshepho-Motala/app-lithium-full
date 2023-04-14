package lithium.service.user.mass.action;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class UserMassActionModuleInfo extends ModuleInfoAdapter {

    public UserMassActionModuleInfo() {
        super();
        Category category = Category.builder().name("User Mass Action Operations").description("These are all the roles relevant to managing user mass action.").build();
        addRole(Role.builder().category(category).name("Mass Player Update View").role("MASS_PLAYER_UPDATE_VIEW").description("Mass player update view.").build());
        addRole(Role.builder().category(category).name("Mass Player Update - Regular Actions").role("MASS_PLAYER_UPDATE_REGULAR").description("Perform regular mass player update actions.").build());
        addRole(Role.builder().category(category).name("Mass Player Update - Financial Actions").role("MASS_PLAYER_UPDATE_FINANCIAL").description("Perform financial mass player update actions.").build());
    }

    @Override
    public void configureHttpSecurity(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/backoffice/{domainName}/{uploadType}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'MASS_BONUS_ALLOCATION_VIEW', 'MASS_PLAYER_UPDATE_*')");
    }
}