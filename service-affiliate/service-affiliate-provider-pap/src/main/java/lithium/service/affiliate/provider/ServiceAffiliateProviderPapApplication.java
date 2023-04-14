package lithium.service.affiliate.provider;

import java.io.IOException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import lithium.application.LithiumShutdownSpringApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lithium.leader.EnableLeaderCandidate;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import lombok.extern.slf4j.Slf4j;

@LithiumService
@EnableLithiumServiceClients
@EnableLeaderCandidate
@EnableScheduling
@Slf4j
public class ServiceAffiliateProviderPapApplication extends LithiumServiceApplication {
	
	@Autowired private LithiumServiceClientFactory services;
	
	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceAffiliateProviderPapApplication.class, args);
	}
	
	@Override
	public void startup(ApplicationStartedEvent e) throws Exception {
		super.startup(e);
//		
//		AccountingTransactionTypeClient transactionTypeService = 
//				services.target(AccountingTransactionTypeClient.class, "service-accounting-provider-internal", true);
//
//		//For accounting, the affiliate balance will always be in a negative to show affiliate has balance (afb is liability to company)
//		//example player win in casino will be a affiliate debit transaction since this will reduce liability of the company towards the affiliate
//		{
//			Long ttid = transactionTypeService.create(AffiliateTranType.AFFILIATE_DEBIT.toString()).getData().getId();
//			transactionTypeService.addAccount(ttid, AffiliateTranType.AFFILIATE_BALANCE.toString(), true, false); //afb is debited (so positive is added to afb since reduces liability to company)
//			transactionTypeService.addAccount(ttid, AffiliateTranType.AFFILIATE_DEBIT.toString(), false, true);
//			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, AffiliateTranType.AFFILIATE_DEBIT.toString());
//			transactionTypeService.addLabel(ttid, AFFILIATE_CONTRACT_HISTORY_ID, true);
//			transactionTypeService.addLabel(ttid, PLAYER_GUID, true);
//			transactionTypeService.addLabel(ttid, ORIGINAL_AMOUNT, false);
//			transactionTypeService.addLabel(ttid, PERCENTAGE, false);
//			transactionTypeService.addLabel(ttid, FIXED_AMOUNT, false);
//		}
		
		try {
			loadData();
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}
	
	@RequestMapping("/loaddata")
	@Transactional
	public void loadData() throws Exception {

	}
	
	@Bean
	RestTemplate restTemplate() {
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		HttpMessageConverter<Object> jackson = new MappingJackson2HttpMessageConverter(mapper);
		
		RestTemplate restTemplate = new RestTemplate();
		
		return restTemplate;
	}

}
