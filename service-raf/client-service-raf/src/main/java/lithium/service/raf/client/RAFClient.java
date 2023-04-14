package lithium.service.raf.client;

import lithium.service.raf.client.objects.ReferralConversion;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.Response;
import lithium.service.raf.client.objects.Referral;
import lithium.service.raf.client.objects.ReferralBasic;

@FeignClient(name="service-raf")
public interface RAFClient {
	@RequestMapping(path="/referral", method=RequestMethod.POST)
	public Response<Referral> add(@RequestBody ReferralBasic user);

	@RequestMapping(path="/referral/add-after-signup", method=RequestMethod.POST)
	public Response<ReferralConversion> addReferralAfterSignUp(@RequestBody ReferralBasic user);

	@RequestMapping(path = "/findByPlayerGuid/{domainName}", method = RequestMethod.GET)
	public Response<Referral> findByPlayerGuid(@PathVariable("domainName") String domainName, @RequestParam("userName") String userName);
	
//	@RequestMapping(path="/referral/markConverted", method=RequestMethod.POST)
//	public Response<Referral> markConverted(@RequestParam("domainName") String domainName, @RequestParam("userName") String userName);
}