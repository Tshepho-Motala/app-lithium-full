package lithium.service.sms;

import java.util.LinkedHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;
import lithium.service.sms.services.SMSService;

@Component
public class ServiceSMSModuleInfo extends ModuleInfoAdapter {
	@Autowired SMSService smsService;
	
	@PostConstruct
	public void init() throws Exception {
		Category queueCategory = Category.builder().name("SMS Queue Operations").description("These are all the roles relevant to managing the sms queue.").build();
		addRole(Role.builder().category(queueCategory).name("SMS Queue View").role("SMS_QUEUE_VIEW").description("View the SMS Queue").build());
		Category templateCategory = Category.builder().name("SMS Template Operations").description("Operations related to sms templates").build();
		addRole(Role.builder().category(templateCategory).name("SMS Templates View").role("SMS_TEMPLATES_VIEW").description("View sms templates").build());
		Category configCategory = Category.builder().name("SMS Config Operations").description("Operations related to SMS configuration").build();
		addRole(Role.builder().category(configCategory).name("SMS Config").role("SMS_CONFIG").description("Manage SMS configuration").build());
	}
	
	ServiceSMSModuleInfo() {
		super();
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/{domainName}/smstemplates/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'SMS_TEMPLATES_VIEW')")
			.antMatchers("/smstemplate/{id}/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'SMS_TEMPLATES_VIEW')")
			.antMatchers("/defaultsmstemplates/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'SMS_TEMPLATES_VIEW')")
			.antMatchers("/sms/saveForPlayerWithText").access("@lithiumSecurity.hasRoleInTree(authentication, 'PLAYER_VIEW', 'PLAYER_EDIT')")
			.antMatchers("/sms/findOne/{id}").access("@lithiumSecurity.hasRoleInTree(authentication, 'SMS_QUEUE_VIEW', 'PLAYER_SMS_HISTORY_VIEW')")
			.antMatchers("/sms/findByUser/table").access("@lithiumSecurity.hasRoleInTree(authentication, 'SMS_QUEUE_VIEW', 'PLAYER_SMS_HISTORY_VIEW')")
			.antMatchers("/sms/findByDomain/table").access("@lithiumSecurity.hasRoleInTree(authentication, 'SMS_QUEUE_VIEW', 'PLAYER_SMS_HISTORY_VIEW')")
			.antMatchers("/domainProvider/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'SMS_CONFIG')")
			.antMatchers("/sms/provider/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'SMS_CONFIG')")
			.antMatchers("/internal/callback").access("@lithiumSecurity.authenticatedSystem(authentication)")
		;
	}
}
