package lithium.service.product;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;

@Component
public class CatalogModuleInfo extends ModuleInfoAdapter {
	public CatalogModuleInfo() {
		super();
		
		roles();
	}
	
	private void roles() {
		Category enginesCategory = Category.builder().name("Products Operations").description("Operations related to products.").build();
		addRole(Role.builder().category(enginesCategory).name("Products").role("PRODUCTS").description("Manage Products").build());
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		super.configureHttpSecurity(http);
		
		// TODO: more fine grained roles if necessary ?
		http.authorizeRequests().antMatchers("/product/admin/**").access("@lithiumSecurity.hasRole(authentication, 'PRODUCTS')");
		http.authorizeRequests().antMatchers("/product/graphic/view/**").permitAll();
		http.authorizeRequests().antMatchers("/product/**").authenticated();
	}
}