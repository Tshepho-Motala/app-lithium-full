package lithium.service.casino.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import lithium.service.casino.client.data.BonusAllocate;
import lithium.service.casino.service.CasinoTriggerBonusService;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableBinding(TriggerBonusQueueSink.class)
@Slf4j
public class TriggerBonusQueueProcessor {
	@Autowired CasinoTriggerBonusService casinoTriggerBonusService;
	
	@StreamListener(TriggerBonusQueueSink.INPUT) 
	public void handle(BonusAllocate bonusAllocate) throws Exception {
		log.info("Received bonus allocate request from queue for processing: "+bonusAllocate);
		casinoTriggerBonusService.processTriggerBonus(bonusAllocate);
	}
}