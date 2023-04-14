package lithium.letsencrypt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LetsEncryptController {
	
	@Autowired LetsEncryptConfigurationProperties properties;

	@RequestMapping(value="/.well-known/acme-challenge/{challengePathHash}", produces="text/plain")
	@ResponseBody
	public String acmeChallenge(@PathVariable("challengePathHash") String challengePathHash) {
		if (challengePathHash.equals(properties.getRequest())) {
			return properties.getResponse();
		} else {
			return "Invalid Hash";
		}
	}
	
}
