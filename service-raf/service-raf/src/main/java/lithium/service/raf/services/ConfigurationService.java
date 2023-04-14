package lithium.service.raf.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.raf.data.entities.Configuration;
import lithium.service.raf.data.objects.AutoConvertRequest;
import lithium.service.raf.data.objects.AutoConvertResult;
import lithium.service.raf.data.repositories.ConfigurationRepository;
import lithium.service.raf.enums.RAFConversionType;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ConfigurationService {
	@Autowired DomainService domainService;
	@Autowired ConfigurationRepository repository;
	
	public Configuration findOrCreate(String domainName) {
		Configuration config = repository.findByDomainName(domainName);
		if (config == null) {
			config = Configuration.builder()
			.domain(domainService.findOrCreate(domainName))
			.conversionType(RAFConversionType.DEPOSIT)
			.build();
			config = repository.save(config);
		}
		return config;
	}
	
	public Configuration modifyConfiguration(Configuration c) throws Exception {
		boolean isXpConversionType = c.getConversionType().getId().compareTo(RAFConversionType.XP_LEVEL.getId()) == 0;
		if (isXpConversionType && c.getConversionXpLevel() == null) {
			throw new Exception("Inappropriate configuration. Conversion XP level missing");
		}
		Configuration config = repository.findOne(c.getId());
		config.setReferrerBonusCode(c.getReferrerBonusCode());
		config.setRefereeBonusCode(c.getRefereeBonusCode());
		config.setReferralNotification(c.getReferralNotification());
		config.setConversionType(c.getConversionType());
		config.setConversionXpLevel((isXpConversionType)? c.getConversionXpLevel(): null);
		config.setAutoConvertPlayer(c.getAutoConvertPlayer());
		return repository.save(config);
	}
	
	public AutoConvertResult enableAutoConvertPlayer(String domainName, AutoConvertRequest request) {
		AutoConvertResult result=new AutoConvertResult();
		if(domainName==null) {
			result.setMessage("Invalid domain name");
			return result;
		}
		if(request!=null && request.getAutoConvertPlayer()!=null) {
			Configuration configuration = repository.findByDomainName(domainName);
			if(configuration!=null) {
				configuration.setAutoConvertPlayer(request.getAutoConvertPlayer());
				repository.save(configuration);
				String message= String.format("%s Auto Convert Player Successful", request.getAutoConvertPlayer());
				result.setMessage(message);
			}else {
				String message= String.format("Configuration for domain %s not found", domainName);
				result.setMessage(message);
			}
		}else {
			result.setMessage("Invalid Request");
		}
		return result;
	}
	
}
