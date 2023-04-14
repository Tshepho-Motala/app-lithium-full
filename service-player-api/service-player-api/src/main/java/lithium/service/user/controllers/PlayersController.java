//package lithium.service.user.controllers;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import lithium.service.Response;
//import lithium.service.user.data.entities.Domain;
//import lithium.service.user.data.entities.User;
//import lithium.service.user.data.objects.UserBasic;
//import lithium.service.user.data.repositories.DomainRepository;
//import lithium.service.user.data.repositories.UserRepository;
//import lithium.service.user.services.DomainService;
//import lithium.service.user.services.ExternalDomainService;
//
//@RestController
//@RequestMapping("/players/{domain}")
//public class PlayersController {
//	
//	@Autowired ExternalDomainService externalDomainService;
//
//	@PostMapping
//	public Response<Map<String, String>> signup(@PathVariable("domain") String domainName, @RequestBody UserBasic o) throws Exception {
//		
//		lithium.service.domain.client.objects.Domain externalDomain = externalDomainService.findByName(domainName); 
//		if (externalDomain == null) throw new Exception("No such domain");
//		if (!externalDomain.getEnabled()) throw new Exception("Domain disabled");
//		if (externalDomain.getDeleted()) throw new Exception("Domain does not exist");
//		if (!externalDomain.getPlayers()) throw new Exception("This is not a player domain");
//		if (!isUnique(domainName, o.getUsername()).getData()) throw new Exception("The username is not unique");
//		
//		User user = User.builder()
//				.domain(domain)
//				.username(o.getUsername().toLowerCase())
//				.password(o.getPassword())
//				.email(o.getEmail().toLowerCase())
//				.firstName(o.getFirstName())
//				.lastName(o.getLastName())
//				.createdDate(new Date())
//				.updatedDate(new Date())
//				.build();
//		repository.save(user);
//		HashMap<String, String> errors = new HashMap<>();
//		return Response.<Map<String, String>>builder().data(errors).build();
//	}
//	
//	@RequestMapping("isunique")
//	public Response<Boolean> isUnique(@PathVariable("domain") String domain, @RequestParam("username") String username) {
//		
//		return Response.<Boolean>builder().data(new Boolean(repository.findByDomainNameAndUsername(domain.toLowerCase(), username.toLowerCase()) == null)).build(); 
//	}
//
//}
