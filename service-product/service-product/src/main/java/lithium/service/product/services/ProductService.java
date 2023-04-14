package lithium.service.product.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.gateway.client.stream.GatewayExchangeStream;
import lithium.service.geo.client.GeoClient;
import lithium.service.geo.client.objects.Location;
import lithium.service.product.data.entities.Domain;
import lithium.service.product.data.entities.LocalCurrency;
import lithium.service.product.data.entities.Payout;
import lithium.service.product.data.entities.Product;
import lithium.service.product.data.repositories.LocalCurrencyRepository;
import lithium.service.product.data.repositories.PayoutRepository;
import lithium.service.product.data.repositories.ProductRepository;
import lithium.service.product.data.specifications.LocalCurrencySpecifications;
import lithium.service.product.data.specifications.PayoutSpecifications;
import lithium.service.product.data.specifications.ProductSpecifications;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductService {
	@Autowired DomainService domainService;
	@Autowired UserService userService;
	@Autowired LithiumServiceClientFactory clientFactory;
	@Autowired GatewayExchangeStream gatewayExchangeStream;
	@Autowired ProductRepository productRepository;
	@Autowired LocalCurrencyRepository localCurrencyRepository;
	@Autowired PayoutRepository payoutRepository;
	
	private Optional<GeoClient> getGeoClient() {
		return getClient(GeoClient.class, "service-geo");
	}
	
	private <E> Optional<E> getClient(Class<E> theClass, String url) {
		E clientInstance = null;
		try {
			clientInstance = clientFactory.target(theClass, url, true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error(e.getMessage(), e);
		}
		return Optional.ofNullable(clientInstance);
	}
	
	public String getCurrencyCode(String ipAddr) throws Exception {
		if ((ipAddr!=null) && (!ipAddr.isEmpty())) {
			Response<Location> geoResponse = getGeoClient().get().location(ipAddr);
			if (geoResponse.isSuccessful() && geoResponse.getData() != null) {
				log.info("geoResponse : "+geoResponse);
				return geoResponse.getData().getCountry().getCurrencyCode();
			}
		}
		return "";
	}
	
	public List<Product> listByDomain(String domainName, String ipAddr) throws Exception {
		Domain domain = domainService.findOrCreate(domainName);
		ipAddr = "196.22.242.140"; // SA
//		ipAddr = "72.229.28.185";  // US
//		ipAddr = "88.198.46.60";  //EU
		String currencyCode = getCurrencyCode(ipAddr);
		log.info("ipAddr :: "+ipAddr+" currencyCode :: "+currencyCode);
		List<Product> listByDomain = productRepository.findByDomain(domain);
		listByDomain.forEach(p -> {
			p.getLocalCurrencies().forEach(lc -> {
				if (lc.getCurrencyCode().equalsIgnoreCase(currencyCode)) {
					p.setCurrencyCode(lc.getCurrencyCode());
					p.setCurrencyAmount(lc.getCurrencyAmount());
				}
			});
		});
		return listByDomain;
	}
	
	public Product save(Product product) {
		return productRepository.save(product);
	}
	
	public Product find(String domainName, String guid, String ipAddr) throws Exception {
		Domain domain = domainService.findOrCreate(domainName);
		String currencyCode = getCurrencyCode(ipAddr);
		log.info("ipAddr :: "+ipAddr+" currencyCode :: "+currencyCode);
		log.info("domainName: "+domainName+", guid:"+guid);
		Product p = productRepository.findByGuidAndDomain(guid, domain);
		p.getLocalCurrencies().forEach(lc -> {
			if (lc.getCurrencyCode().equalsIgnoreCase(currencyCode)) {
				p.setCurrencyCode(lc.getCurrencyCode());
				p.setCurrencyAmount(lc.getCurrencyAmount());
			}
		});
		return p;
	}
	
	public Page<Product> findByDomains(List<String> domains, String searchValue, Boolean enabled, String ipAddr, Pageable pageable) throws Exception {
		log.trace("findByDomains");
		Specification<Product> spec = Specification.where(ProductSpecifications.domains(domains));
		if (enabled != null) spec = spec.and(ProductSpecifications.enabled(enabled));
		if ((searchValue != null) && (searchValue.length() > 0)) {
			Specification<Product> s = Specification.where(ProductSpecifications.any(searchValue));
			spec = (spec == null)? s: spec.and(s);
		}
		Page<Product> result = productRepository.findAll(spec, pageable);
		
		String currencyCode = getCurrencyCode(ipAddr);
		log.info("ipAddr :: "+ipAddr+" currencyCode :: "+currencyCode);
		result.forEach(p -> {
			p.getLocalCurrencies().forEach(lc -> {
				if (lc.getCurrencyCode().equalsIgnoreCase(currencyCode)) {
					p.setCurrencyCode(lc.getCurrencyCode());
					p.setCurrencyAmount(lc.getCurrencyAmount());
				}
			});
		});
		
		return result;
	}
	
	public Page<LocalCurrency> findLocalCurrencies(Product product, String search, Pageable pageable) {
		log.trace("findLocalCurrency");
		if (search==null) search = "";
		Specification<LocalCurrency> spec = Specification.where(LocalCurrencySpecifications.any(product.getId(), search));
		Page<LocalCurrency> result = localCurrencyRepository.findAll(spec, pageable);
		return result;
	}
	
	public Page<Payout> findPayouts(Product product, String search, Pageable pageable) {
		log.trace("findLocalCurrency");
		if (search==null) search = "";
		Specification<Payout> spec = Specification.where(PayoutSpecifications.any(product.getId(), search));
		Page<Payout> result = payoutRepository.findAll(spec, pageable);
		return result;
	}

	public Product add(Product productBasic) {
		Domain domain = domainService.findOrCreate(productBasic.getDomain().getName());
		Product p = productRepository.findByGuidAndDomain(productBasic.getGuid(), domain);
		if (p == null) {
			productBasic.setDomain(domain);
			productBasic.setEnabled(false);
			p = save(productBasic);
		}
		return p;
	}
	public Product edit(Product productBasic) {
		Product p = productRepository.findOne(productBasic.getId());
		p.setDescription(productBasic.getDescription());
		p.setName(productBasic.getName());
		p.setGuid(productBasic.getGuid());
		p.setNotification(productBasic.getNotification());
		p.setCurrencyAmount(productBasic.getCurrencyAmount());
		p.setCurrencyCode(productBasic.getCurrencyCode());
		return save(p);
	}
	
	public LocalCurrency addLocalCurrency(LocalCurrency localCurrency) {
		log.debug("Saving : "+localCurrency);
		LocalCurrency lc = localCurrencyRepository.findByCountryCodeAndCurrencyCodeAndProduct(localCurrency.getCountryCode(), localCurrency.getCurrencyCode(), localCurrency.getProduct());
		if (lc == null) {
			lc = localCurrencyRepository.save(localCurrency);
		}
		return lc;
	}
	public LocalCurrency editLocalCurrency(LocalCurrency localCurrency) {
		log.debug("Editing : "+localCurrency);
		localCurrency = localCurrencyRepository.save(localCurrency);
		return localCurrency;
	}
	public void removeLocalCurrency(LocalCurrency localCurrency) {
		log.debug("Removing : "+localCurrency);
		localCurrencyRepository.delete(localCurrency);
	}
	
	public Payout addPayout(Payout payout) {
		log.debug("Saving : "+payout);
		Payout lc = payoutRepository.findByBonusCodeAndCurrencyCodeAndProduct(payout.getBonusCode(), payout.getCurrencyCode(), payout.getProduct());
		if (lc == null) {
			lc = payoutRepository.save(payout);
		}
		return lc;
	}
	public Payout editPayout(Payout payout) {
		log.debug("Editing : "+payout);
		payout = payoutRepository.save(payout);
		return payout;
	}
	public void removePayout(Payout payout) {
		log.debug("Removing : "+payout);
		payoutRepository.delete(payout);
	}
}