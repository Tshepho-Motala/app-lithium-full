package lithium.service.mail;

import javax.annotation.PostConstruct;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;

@Component
public class ServiceMailModuleInfo extends ModuleInfoAdapter {

    @PostConstruct
    public void init() {
        Category queueCategory = Category.builder().name("Mail Queue Operations").description("These are all the roles relevant to managing the mail queue.").build();
        addRole(Role.builder().category(queueCategory).name("Mail Queue View").role("MAIL_QUEUE_VIEW").description("View the Mail Queue").build());
        Category templateCategory = Category.builder().name("Mail Template Operations").description("Operations related to mail templates").build();
        addRole(Role.builder().category(templateCategory).name("Mail Templates View").role("EMAIL_TEMPLATES_VIEW").description("View email templates").build());
        addRole(Role.builder().category(templateCategory).name("Send player template").role("SEND_PLAYER_TEMPLATE").description("Send email template to player").build());

        Category configCategory = Category.builder().name("Mail Config Operations").description("Operations related to mail configuration").build();
        addRole(Role.builder().category(configCategory).name("Mail Config").role("MAIL_CONFIG").description("Manage mail configuration").build());
    }

    public ServiceMailModuleInfo() {
    }

    @Override
    public void configureHttpSecurity(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/{domainName}/emailtemplates/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'EMAIL_TEMPLATES_VIEW', 'SEND_PLAYER_TEMPLATE')")
                .antMatchers("/emailtemplate/{id}/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'EMAIL_TEMPLATES_VIEW')")
                .antMatchers("/defaultemailtemplates/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'EMAIL_TEMPLATES_VIEW')")
                .antMatchers("/mail/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'MAIL_QUEUE_VIEW', 'PLAYER_MAIL_HISTORY_VIEW')")
                .antMatchers("/mail/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)")
                .antMatchers("/domainProvider/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'MAIL_CONFIG')")
                .antMatchers("/mail/provider/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'MAIL_CONFIG')")
                .antMatchers("/internal/callback").access("@lithiumSecurity.authenticatedSystem(authentication)")
                .antMatchers("/frontend/verify-email").permitAll()
                .antMatchers("/quick-action-email/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'SEND_PLAYER_TEMPLATE')")
                .antMatchers(HttpMethod.GET, "/data-migration-job/**").access("@lithiumSecurity.authenticatedSystem(authentication)")
        ;
    }
}
