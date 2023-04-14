package lithium.service.user.provider.internal;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import lithium.service.user.provider.internal.data.entities.Role;
import lithium.service.user.provider.internal.data.entities.User;
import lithium.service.user.provider.internal.data.repositories.RoleRepository;
import lithium.service.user.provider.internal.data.repositories.UserRepository;

@Configuration
public class Init {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	
	@PostConstruct 
	public void init() {
		Role adminRole = createRole("Super User", "ADMIN", "A role with full access to all operations");
		Role chiefRole = createRole("Almost Super User", "CHIEF", "A role with access to all operations, except delete");
		Role editorRole = createRole("Average User", "EDITOR", "A role with access to view and edit operations");
		Role userRole = createRole("User", "USER", "A role with access to view only operations");
		
		createUser("default", "admin", "Gauteng", "Administrator", "Administrator", "admin@default.com", new HashSet<>(Arrays.asList(adminRole)));
		createUser("default", "johanvdb", "Gauteng", "Johan", "van den Berg", "johanvdb@default.com", new HashSet<>(Arrays.asList(chiefRole)));
		createUser("default", "riaans", "Gauteng", "Riaan", "Schoeman", "riaans@default.com", new HashSet<>(Arrays.asList(editorRole, userRole)));
		createUser("default", "chrisc", "Gauteng", "Chris", "Cornelissen", "chrisc@default.com", new HashSet<>(Arrays.asList(userRole)));
		
		createUser("epn", "epn", "Gauteng", "Player", "Administrator", "epn@default.com");
		createUser("epn", "lynn", "Gauteng", "Player", "Administrator", "epn@default.com");
		createUser("ffp", "ffp", "Gauteng", "ffp Player", "Viewer", "ffp@default.com");
		createUser("int", "int", "Gauteng", "int Player", "Viewer", "int@default.com");
	}
	
	private Role createRole(String name, String role, String description) {
		if (roleRepository.findByRole(role) == null) {
			roleRepository.save(Role.builder().role(role).name(name).description(description).build());
			return roleRepository.findByRole(role);
		}
		return null;
	}
	
	private void createUser(String domain, String username, String password, String firstname, String lastname, String email) {
		createUser(domain, username, password, firstname, lastname, email, null);
	}
	
	private void createUser(String domain, String username, String password, String firstname, String lastname, String email, Set<Role> roles) {
		if (userRepository.findByDomainAndUsernameIgnoreCase(domain, username) == null) {
			User user = User.builder()
					.domain(domain)
					.username(username)
					.password(password)
					.firstName(firstname)
					.lastName(lastname)
					.enabled(true)
					.deleted(false)
					.email(email)
					.roles(roles)
					.createdDate(new Date())
					.updatedDate(new Date())
					.build();
			userRepository.save(user);
		}
	}
}
