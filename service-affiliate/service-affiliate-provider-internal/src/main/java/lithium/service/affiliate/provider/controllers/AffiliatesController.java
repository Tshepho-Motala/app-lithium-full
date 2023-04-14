package lithium.service.affiliate.provider.controllers;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.affiliate.provider.data.entities.Affiliate;
import lithium.service.affiliate.provider.data.repositories.AffiliateRepository;
import lithium.service.affiliate.provider.service.AffiliateService;
import lithium.service.user.client.AffiliateClient;
import lithium.service.user.client.objects.Address;
import lithium.service.user.client.objects.User;
import lithium.tokens.LithiumTokenUtil;

@RestController
@RequestMapping("/affiliates/{domainName}")
public class AffiliatesController {
	@Autowired AffiliateService service;
	@Autowired AffiliateRepository affiliateRepository;
	
	@PostMapping("/create")
	public Response<User> create(@PathVariable("domainName") String domainName, @RequestBody User affiliateUser) {
		AffiliateClient client = service.getAffiliateClient();
		
		Address address = null;
		if (affiliateUser.getResidentialAddress() != null) {
			address = Address.builder()
				.addressLine1(affiliateUser.getResidentialAddress().getAddressLine1())
				.addressLine2(affiliateUser.getResidentialAddress().getAddressLine2())
				.addressLine3(affiliateUser.getResidentialAddress().getAddressLine3())
				.city(affiliateUser.getResidentialAddress().getCity())
				.country(affiliateUser.getResidentialAddress().getCountry())
				.postalCode(affiliateUser.getResidentialAddress().getPostalCode())
				.build();
		}
		
		Map<String, String> labelAndValue = new LinkedHashMap<String, String>();
		if (affiliateUser.getCompanyName() != null && !affiliateUser.getCompanyName().isEmpty())
			labelAndValue.put("companyName", affiliateUser.getCompanyName());
		if (affiliateUser.getWebsiteURL() != null && !affiliateUser.getWebsiteURL().isEmpty())
			labelAndValue.put("websiteUrl", affiliateUser.getWebsiteURL());
		if (affiliateUser.getPaymentDetails() != null && !affiliateUser.getPaymentDetails().isEmpty())
			labelAndValue.put("paymentDetails", affiliateUser.getPaymentDetails());
		labelAndValue.put("termsAccepted", String.valueOf(affiliateUser.isAcceptTerms()));
		labelAndValue.put("communicationsOptedIn", String.valueOf(affiliateUser.isOptIn()));
		
		Response<User> response = client.create(domainName, User.builder()
				.username(affiliateUser.getUsername())
				.passwordPlaintext(affiliateUser.getPassword())
				.firstName(affiliateUser.getFirstName())
				.lastName(affiliateUser.getLastName())
				.email(affiliateUser.getEmail())
				.cellphoneNumber(affiliateUser.getCellphoneNumber())
				.residentialAddress(address)
				.labelAndValue(labelAndValue)
				.build()
			);
		
		if (response.isSuccessful()) {
			User affiliate = response.getData();
			affiliateRepository.save(
					Affiliate.builder()
					.userGuid(affiliate.getDomain().getName()+"/"+affiliate.getUsername())
					.domain(service.findOrCreateDomain(domainName))
					.build()
				);
			return response;
		} else {
			return Response.<User>builder().status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GetMapping
	public Response<User> get(Principal principal) {
		LithiumTokenUtil util = service.getTokenUtil(principal);
		AffiliateClient client = service.getAffiliateClient();
		return client.get(util.domainName(), util.id());
	}
	
	@PostMapping("/changepassword")
	public Response<User> changePassword(Principal principal, @RequestBody String password) {
		LithiumTokenUtil util = service.getTokenUtil(principal);
		AffiliateClient client = service.getAffiliateClient();
		return client.changePassword(util.domainName(), util.id(), password);
	}
	
	@PostMapping("/save")
	public Response<User> save(Principal principal, @RequestBody User user) {
		LithiumTokenUtil util = service.getTokenUtil(principal);
		AffiliateClient client = service.getAffiliateClient();
		return client.save(util.domainName(), util.id(), user);
	}
	
	@GetMapping("/isunique/{username}/username")
	public Response<Boolean> isUniqueUsername(@PathVariable("domainName") String domainName, @PathVariable("username") String username) {
		AffiliateClient client = service.getAffiliateClient();
		return client.isUniqueUsername(domainName, username);
	}
	
	@GetMapping("/isunique/{email}/email")
	public Response<Boolean> isUniqueEmail(@PathVariable("domainName") String domainName, @PathVariable("email") String email) {
		AffiliateClient client = service.getAffiliateClient();
		return client.isUniqueEmail(domainName, email);
	}
}