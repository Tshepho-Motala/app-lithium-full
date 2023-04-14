package lithium.service.notifications;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;

@Component
public class NotificationsModuleInfo extends ModuleInfoAdapter {
	public NotificationsModuleInfo() {
		super();
		
		roles();
	}
	
	public void roles() {
		Category notificationsCategory = Category.builder().name("Notifications Operations").description("Operations related to notifications.").build();
		addRole(Role.builder().category(notificationsCategory).name("Notifications View").role("NOTIFICATIONS_VIEW").description("View Notifications").build());
		addRole(Role.builder().category(notificationsCategory).name("Notifications Edit").role("NOTIFICATIONS_EDIT").description("Edit Notifications").build());
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		super.configureHttpSecurity(http);
		
		http.authorizeRequests().antMatchers("/inbox/**").authenticated();
		
		http.authorizeRequests().antMatchers("/admin/channel/all").access("@lithiumSecurity.hasRole(authentication, 'NOTIFICATIONS_VIEW', 'NOTIFICATIONS_EDIT', 'PLAYER_NOTIFICATIONS_VIEW')");
		
		http.authorizeRequests().antMatchers("/admin/inbox/table").access("@lithiumSecurity.hasRole(authentication, 'NOTIFICATIONS_VIEW', 'NOTIFICATIONS_EDIT', 'PLAYER_NOTIFICATIONS_VIEW')");
		http.authorizeRequests().antMatchers("/admin/inbox/{id}").access("@lithiumSecurity.hasRole(authentication, 'NOTIFICATIONS_VIEW', 'NOTIFICATIONS_EDIT', 'PLAYER_NOTIFICATIONS_VIEW')");
		
		http.authorizeRequests().antMatchers("/admin/notification/send").access("@lithiumSecurity.hasRole(authentication, 'NOTIFICATIONS_EDIT')");
		http.authorizeRequests().antMatchers("/admin/notification/findByDomainName").access("@lithiumSecurity.hasRole(authentication, 'NOTIFICATIONS_VIEW', 'NOTIFICATIONS_EDIT', 'PLAYER_NOTIFICATIONS_VIEW')");
		http.authorizeRequests().antMatchers("/admin/notification/findByDomainNameAndName").access("@lithiumSecurity.hasRole(authentication, 'NOTIFICATIONS_VIEW', 'NOTIFICATIONS_EDIT', 'PLAYER_NOTIFICATIONS_VIEW')");
		http.authorizeRequests().antMatchers("/admin/notification/table").access("@lithiumSecurity.hasRole(authentication, 'NOTIFICATIONS_VIEW', 'NOTIFICATIONS_EDIT', 'PLAYER_NOTIFICATIONS_VIEW')");
		http.authorizeRequests().antMatchers("/admin/notification/{id}").access("@lithiumSecurity.hasRole(authentication, 'NOTIFICATIONS_VIEW', 'NOTIFICATIONS_EDIT', 'PLAYER_NOTIFICATIONS_VIEW')");
		http.authorizeRequests().antMatchers("/admin/notification/create").access("@lithiumSecurity.hasRole(authentication, 'NOTIFICATIONS_EDIT')");
		http.authorizeRequests().antMatchers("/admin/notification/{id}/modify").access("@lithiumSecurity.hasRole(authentication, 'NOTIFICATIONS_EDIT')");
		http.authorizeRequests().antMatchers("/admin/notification/{id}/addChannel").access("@lithiumSecurity.hasRole(authentication, 'NOTIFICATIONS_EDIT')");
		http.authorizeRequests().antMatchers("/admin/notification/{id}/removeChannel/{notificationChannelId}").access("@lithiumSecurity.hasRole(authentication, 'NOTIFICATIONS_EDIT')");
		http.authorizeRequests().antMatchers("/admin/notification/{id}/modifyChannel/{notificationChannelId}").access("@lithiumSecurity.hasRole(authentication, 'NOTIFICATIONS_EDIT')");

		http.authorizeRequests().antMatchers("/backoffice/notification-types").access("@lithiumSecurity.hasRole(authentication, 'NOTIFICATIONS_EDIT')");

		http.authorizeRequests().antMatchers("/frontend/inbox/**").authenticated();

		http.authorizeRequests().antMatchers("/system/inbox/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
	}
}
