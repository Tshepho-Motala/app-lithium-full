package lithium.service.settlement.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lithium.service.Response;
import lithium.service.settlement.client.objects.BatchSettlements;
import lithium.service.settlement.client.objects.Settlement;
import lithium.service.settlement.client.objects.SettlementEntry;

@FeignClient(name="service-settlement")
public interface SettlementClient {
	@RequestMapping("/settlements/create")
	Response<Settlement> create(@RequestBody Settlement settlement);
	
	@RequestMapping(value="/batch/settlements/{domainName}/{batchName}/initBatchRerun", method=RequestMethod.POST)
	Response<BatchSettlements> initBatchRerun(@PathVariable("domainName") String domainName,
			@PathVariable("batchName") String batchName);
	
	@RequestMapping(value="/batch/settlements/{domainName}/{batchName}/closeBatchRerun", method=RequestMethod.POST)
	Response<BatchSettlements> closeBatchRerun(@PathVariable("domainName") String domainName,
			@PathVariable("batchName") String batchName);
	
	@RequestMapping("/settlements/settlement/findbyentity/{batchName}/{entityUuid}/{dateStart}/{dateEnd}")
	Response<Settlement> findByEntity(@PathVariable("batchName") String batchName,
			@PathVariable("entityUuid") String entityUuid,
			@PathVariable("dateStart") String dateStart,
			@PathVariable("dateEnd") String dateEnd);
	
	@RequestMapping("/settlements/settlement/findbyuser/{batchName}/{userGuid}/{dateStart}/{dateEnd}")
	Response<Settlement> findByUser(@PathVariable("batchName") String batchName,
			@PathVariable("userGuid") String userGuid,
			@PathVariable("dateStart") String dateStart,
			@PathVariable("dateEnd") String dateEnd);
	
	@RequestMapping("/settlement/{id}")
	Response<Settlement> get(@PathVariable("id") Long id);
	
	@RequestMapping("/settlement/{id}/entry/add")
	Response<Settlement> addSettlementEntry(@PathVariable("id") Long id, @RequestBody SettlementEntry entry);
	
	@RequestMapping(value="/settlement/{id}/finalize", method=RequestMethod.POST)
	Response<Settlement> finalizeSettlement(@PathVariable("id") Long id);
}
