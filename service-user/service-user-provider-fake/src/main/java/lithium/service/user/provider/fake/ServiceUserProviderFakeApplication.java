package lithium.service.user.provider.fake;

import java.util.Map;

import lithium.util.PasswordHashing;
import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.user.client.objects.User;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import lombok.extern.slf4j.Slf4j;

@LithiumService
@RestController
@Slf4j
public class ServiceUserProviderFakeApplication extends LithiumServiceApplication {

	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceUserProviderFakeApplication.class, args);
	}
		
	public ResponseEntity<User> fakeUser(String username, String password) {
		User user = new User();
		user.setUsername(username);
		user.setPasswordPlaintext(password);
		user.setFirstName("Fake firstname " + username);
		user.setLastName("Fake lastname " + username);
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	@RequestMapping("/users/auth")
	public ResponseEntity<User> auth(@RequestParam String domain, @RequestParam String username, @RequestParam String password, @RequestParam(required=false) Map<String, String> parameters) {
		log.info("auth domain " + domain + " username " + username + " password " + password);
		return fakeUser(username, password);
	}

	@RequestMapping("/users/user")
	public ResponseEntity<User> user(@RequestParam String domain, @RequestParam String username, @RequestParam(required=false) Map<String, String> parameters) {
		log.info("user domain " + domain + " username " + username);
		return fakeUser(username, username);
	}
}
