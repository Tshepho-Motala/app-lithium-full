package lithium.service.user.provider.method1.controllers;

import java.security.Principal;
import java.util.Map;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lithium.service.user.client.objects.Domain;
import lithium.service.user.client.objects.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
	
	@RequestMapping(path = "/auth")
	public ResponseEntity<User> auth(@RequestParam String domain, @RequestParam String username, @RequestParam String password, @RequestParam(required=false) Map<String, String> parameters) {
		log.info("auth domain "+domain+" username "+username+" password "+password+" parameters "+parameters);
		User user = null;
		try {
			RestTemplate restTemplate = new RestTemplate();
			String parameterName = parameters.get("provider-name");
			String client = parameters.get(parameterName+"-client");
			String url = parameters.get(parameterName+"-login.url");
			String blackBoxId = "";
			if (parameters.containsKey(parameterName+"-black_box_id")) {
				blackBoxId = parameters.get(parameterName+"-black_box_id");
			}
			LoginRequest loginRequest = LoginRequest
				.builder()
				.loginName(username)
				.password(password)
				.pokerClient(client)
				.blackBoxId(blackBoxId.getBytes())
				.build();
			LoginResponse loginResponse = restTemplate.postForObject(url, loginRequest, LoginResponse.class);
			log.info("LoginResponse : "+loginResponse);
			if (loginResponse.getResultCode() == 0) {
				user = User
					.builder()
					.domain(Domain.builder().name(domain).build())
					.firstName(loginResponse.getFirstName())
					.lastName(loginResponse.getLastName())
					.passwordPlaintext(password)
					.username(username)
					.build();
				return new ResponseEntity<User>(user, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ResponseEntity<User>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/user")
	public ResponseEntity<User> user(@RequestParam String domain, @RequestParam String username, @RequestParam(required=false) Map<String, String> parameters, Principal principal) {
		log.info("user domain " + domain + " username " + username + " principal " + principal);
		return new ResponseEntity<User>(HttpStatus.NOT_IMPLEMENTED);
	}
	
	@RequestMapping(path = "/create", method = RequestMethod.POST)
	public ResponseEntity<User> create(@RequestBody @Valid User user) {
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
	
	@RequestMapping(path = "/update", method = RequestMethod.POST)
	public ResponseEntity<User> update(@RequestBody @Valid User user) {
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString(callSuper=true)
	@XmlRootElement(name="loginRequest")
	static class LoginRequest {
		private String loginName;
		private String password;
		private String pokerClient;
		private byte[] blackBoxId;
	}
	
	@Data
	@ToString
	@XmlRootElement(name="loginResponse")
	static class LoginResponse {
		private Integer resultCode;
		private String resultMessage;
		private String sessionHash = null;
		private String firstName = null;
		private String lastName = null;
		private String emailAddress = null;
		private String userID = null;
		private String agentId = null;
		private String region = null;
		private String country = null;
	}
}
