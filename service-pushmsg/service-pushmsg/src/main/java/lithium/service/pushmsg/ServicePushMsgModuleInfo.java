package lithium.service.pushmsg;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.pushmsg.services.PushMsgService;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ServicePushMsgModuleInfo extends ModuleInfoAdapter {
	@Autowired PushMsgService pushMsgService;
	
	@PostConstruct
	public void init() throws Exception {
		Category queueCategory = Category.builder().name("PushMsg Queue Operations").description("These are all the roles relevant to managing the pushmsg queue.").build();
		addRole(Role.builder().category(queueCategory).name("PushMsg Queue View").role("PUSHMSG_QUEUE_VIEW").description("View the PushMsg Queue").build());
		Category templateCategory = Category.builder().name("PushMsg Template Operations").description("Operations related to pushmsg templates").build();
		addRole(Role.builder().category(templateCategory).name("PushMsg Templates View").role("PUSHMSG_TEMPLATES_VIEW").description("View pushmsg templates").build());
		Category usersCategory = Category.builder().name("PushMsg Users Operations").description("Operations related to pushmsg users").build();
		addRole(Role.builder().category(usersCategory).name("PushMsg Users View").role("PUSHMSG_USERS_VIEW").description("View pushmsg users").build());
	}
	
	ServicePushMsgModuleInfo() {
		super();
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			// DomainProviderController
			.antMatchers("/domainProvider/**").access("@lithiumSecurity.hasRole(authentication, 'ADMIN')")
			// ProviderController
			.antMatchers("/pushmsg/provider").access("@lithiumSecurity.hasRole(authentication, 'ADMIN')")
			// PushMsgTemplateController
			.antMatchers("/pushmsgtemplate/{id}/**").access("@lithiumSecurity.hasRole(authentication, 'PUSHMSG_TEMPLATES_VIEW')")
			// PushMsgTemplatesController
			.antMatchers("/{domainName}/pushmsgtemplates/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PUSHMSG_TEMPLATES_VIEW')")
			// PushMsgUsersController
			// {domain}/pushmsgusers/unsubscribe used in megavegas app
			.antMatchers("/{domainName}/pushmsgusers/**").authenticated()
			// UserController
			// pushmsg/pushmsg/user/single used in megavegas app
			.antMatchers("/pushmsg/user/**").authenticated()
			// PushMsgController
			.antMatchers("/pushmsg/**").access("@lithiumSecurity.hasRole(authentication, 'PUSHMSG_QUEUE_VIEW')");
	}
}