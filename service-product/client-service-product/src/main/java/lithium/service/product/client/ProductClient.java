package lithium.service.product.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.Response;
import lithium.service.product.client.objects.Product;

@FeignClient(name="service-product")
public interface ProductClient {
	@RequestMapping(path="/product/find", method=RequestMethod.GET)
	public Response<Product> findByDomainAndGuid(
		@RequestParam(name="domainName", required=true)  String domainName,
		@RequestParam(name="guid", required=true) String guid,
		@RequestParam(name="ipAddr", required=true) String ipAddr
	);
}