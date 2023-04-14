package lithium.service.machine.jobs;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lithium.leader.LeaderCandidate;
import lithium.service.Response;
import lithium.service.accounting.client.AccountingSummaryDomainLabelValueClient;
import lithium.service.accounting.objects.Period;
import lithium.service.accounting.objects.SummaryLabelValue;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.entity.client.EntityClient;
import lithium.service.machine.data.entities.LocationDistributionConfigurationRevision;
import lithium.service.machine.data.entities.Machine;
import lithium.service.machine.data.entities.MachineSettlement;
import lithium.service.machine.data.entities.MachineSettlementProcessingBoundary;
import lithium.service.machine.data.entities.RelationshipDistributionConfigurationRevision;
import lithium.service.machine.data.objects.Distribution;
import lithium.service.machine.data.repositories.MachineRepository;
import lithium.service.machine.data.repositories.MachineSettlementProcessingBoundaryRepository;
import lithium.service.machine.data.repositories.MachineSettlementRepository;
import lithium.service.machine.services.MachineService;
import lithium.service.settlement.client.SettlementClient;
import lithium.service.settlement.client.objects.Address;
import lithium.service.settlement.client.objects.BankDetails;
import lithium.service.settlement.client.objects.BatchSettlements;
import lithium.service.settlement.client.objects.Domain;
import lithium.service.settlement.client.objects.Entity;
import lithium.service.settlement.client.objects.Settlement;
import lithium.service.settlement.client.objects.SettlementEntry;
import lombok.extern.slf4j.Slf4j;

@Service
@EnableScheduling
@Slf4j
public class MachineSettlementJob {
	@Autowired MachineSettlementRepository jobRepo;
	@Autowired MachineSettlementProcessingBoundaryRepository jobProcessingBoundaryRepo;
	@Autowired MachineRepository machineRepo;
	@Autowired MachineService machineService;
	@Autowired LithiumServiceClientFactory services;
	@Autowired LeaderCandidate leaderCandidate;
	@Autowired ModelMapper modelMapper;
	@Autowired CachingDomainClientService currencyService;
	
	@Scheduled(fixedDelay=60000)
	public void process() throws Exception {
		if (!leaderCandidate.iAmTheLeader()) return;
		
		log.info("MachineSettlementJob:: process");
		
		SettlementClient client = null;
		try {
			client = services.target(SettlementClient.class, "service-settlement", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting settlement-client. " + e.getMessage(), e);
			return;
		}
		
		List<MachineSettlement> jobs = jobRepo.findByCompletedFalse();
		
		for (MachineSettlement job: jobs) {
			try {
				if (job.getProcessing() == true) {
					if (Minutes.minutesBetween(new DateTime(job.getStartedOn()), new DateTime(new Date())).getMinutes() > 120) {
						log.warn("MachineSettlement (" + job.getId() + ") has been processing for more than two hours. "
							+ "Possible stuck job due to service errors or shutdown/restart. This job will now continue.");
					} else {
						continue;
					}
				}
				
				job.setStartedOn(new Date());
				job.setProcessing(true);
				job = jobRepo.save(job);
				
				MachineSettlementProcessingBoundary boundary = job.getBoundary();
				if (boundary == null) {
					boundary = MachineSettlementProcessingBoundary.builder()
					.job(job)
					.build();
					boundary = jobProcessingBoundaryRepo.save(boundary);
					
					job.setBoundary(boundary);
					job = jobRepo.save(job);
				}
				
				int page = 0;
				boolean hasMore = true;
				
				List<Machine> machinesList = new ArrayList<Machine>();
				PageRequest pageRequest = PageRequest.of(page, 10);
				
				while (hasMore) {
					Page<Machine> machines = new PageImpl<Machine>(machinesList, pageRequest, page);
					if (boundary.getLastMachineIdProcessed() != null) {
						machines = machineRepo.findByIdGreaterThanOrderById(boundary.getLastMachineIdProcessed(), pageRequest);
					} else {
						machines = machineRepo.findAll(pageRequest);
					}
					
					for (Machine machine: machines.getContent()) {
						lithium.service.domain.client.objects.Domain domain = currencyService.retrieveDomainFromDomainService(machine.getDomain().getName());
						
						DateTime dtDateStart = new DateTime(job.getDateStart());
						if (boundary.getLastDateProcessed() != null) {
							dtDateStart = new DateTime(boundary.getLastDateProcessed()).plusDays(1);
						}
						DateTime dtDateEnd = new DateTime(job.getDateEnd().after(new Date()) ?
								new DateTime(new Date()).withTime(23, 59, 59, 0).toDate() : job.getDateEnd());
						
						while (!dtDateStart.isAfter(dtDateEnd)) {
							Distribution distribution = machineService.getMachineDistribution(
								machine.getId(), null, dtDateStart.toDate(), job.getDomain().getName());
							
							DateTime de = dtDateStart.plusDays(1);
							Long machineGrossBetsCents = findMachineGrossBets(domain.getName(), machine.getGuid(), domain.getCurrency(), dtDateStart.toDate().toInstant().toString(), de.toDate().toInstant().toString());
							
							for (LocationDistributionConfigurationRevision config: distribution.getLocationDistConfigRevisions()) {
								if (boundary.getLastLocationDistConfigRevIdProcessed() != null &&
									boundary.getLastLocationDistConfigRevIdProcessed() > config.getId()) {
										continue;
								}
								
								Settlement settlement = findOrCreateSettlement(job.getBatchName(), config.getEntity().getUuid(), job.getDomain().getName(),
									job.getCreatedBy(), job.getDateStart(), job.getDateEnd(), client);
								addSettlementEntry(settlement, machine.getId(), machine.getGuid(), machine.getName(), machineGrossBetsCents, config.getPercentage(), dtDateStart, de, client);
								
								boundary.setLastLocationDistConfigRevIdProcessed(config.getId());
								boundary = jobProcessingBoundaryRepo.save(boundary);
							}
							
							boundary.setLastLocationDistConfigRevIdProcessed(null);
							boundary = jobProcessingBoundaryRepo.save(boundary);
							
							for (RelationshipDistributionConfigurationRevision config: distribution.getRelationshipDistConfigRevisions()) {
								if (boundary.getLastRelationshipDistConfigRevIdProcessed() != null &&
									boundary.getLastRelationshipDistConfigRevIdProcessed() > config.getId()) {
										continue;
								}
								
								Settlement settlement = findOrCreateSettlement(job.getBatchName(), config.getEntity().getUuid(), job.getDomain().getName(),
									job.getCreatedBy(), job.getDateStart(), job.getDateEnd(), client);
								addSettlementEntry(settlement, machine.getId(), machine.getGuid(), machine.getName(), machineGrossBetsCents, config.getPercentage(), dtDateStart, de, client);
								
								boundary.setLastRelationshipDistConfigRevIdProcessed(config.getEntity().getId());
								boundary = jobProcessingBoundaryRepo.save(boundary);
							}
							
							boundary.setLastRelationshipDistConfigRevIdProcessed(null);
							
							boundary.setLastDateProcessed(dtDateStart.toDate());
							boundary = jobProcessingBoundaryRepo.save(boundary);
							
							dtDateStart = dtDateStart.plusDays(1);
						}
						
						boundary.setLastDateProcessed(null);
						boundary.setLastMachineIdProcessed(machine.getId());
						boundary = jobProcessingBoundaryRepo.save(boundary);
					}
					
					page++;
					hasMore = machines.hasNext();
				}
				
				if (job.getRerun()) {
					client.closeBatchRerun(job.getDomain().getName(), job.getBatchName());
					job.setRerun(false);
				}
				
				job.setProcessing(false);
				job.setCompleted(true);
				job = jobRepo.save(job);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				job.setLastFailedReason(e.getMessage());
				job.setLastFailedDate(new Date());
				job.setProcessing(false);
				job = jobRepo.save(job);
			}
		}
	}
	
	private Settlement findOrCreateSettlement(String batchName, String entityUuid, String domainName, String createdBy, Date dateStart, Date dateEnd, SettlementClient client) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Settlement settlement = client.findByEntity(batchName, entityUuid, sdf.format(dateStart), sdf.format(dateEnd)).getData();
		if (settlement == null) {
			EntityClient entityClient = services.target(EntityClient.class, "service-entity", true);
			lithium.service.entity.client.objects.Entity entity = entityClient.findByUuid(entityUuid).getData();
			
			Address physicalAddress = null;
			if (entity.getPhysicalAddress() != null) {
				physicalAddress = modelMapper.map(entity.getPhysicalAddress(), Address.class);
			}
			
			Address billingAddress = null;
			if (entity.getBillingAddress() != null) {
				billingAddress = modelMapper.map(entity.getBillingAddress(), Address.class);
			}
			
			BankDetails bankDetails = null;
			if (entity.getBankDetails() != null) {
				bankDetails = modelMapper.map(entity.getBankDetails(), BankDetails.class);
			}
			
			settlement = Settlement.builder()
			.domain(Domain.builder().name(domainName).build())
			.entity(Entity.builder().uuid(entityUuid).build())
			.createdBy(createdBy)
			.dateStart(dateStart)
			.dateEnd(dateEnd)
			.physicalAddress(physicalAddress)
			.billingAddress(billingAddress)
			.bankDetails(bankDetails)
			.batchSettlements(BatchSettlements.builder().name(batchName).build())
			.build();
			settlement = client.create(settlement).getData();
		}
		return settlement;
	}
	
	private Settlement addSettlementEntry(Settlement settlement, Long machineId, String machineGuid, String machineName, Long machineGrossBets, BigDecimal percentage, DateTime dateStart, DateTime dateEnd, SettlementClient client) {
		Map<String, String> labelValueMap = new LinkedHashMap<String, String>();
		labelValueMap.put("machineId", String.valueOf(machineId));
		labelValueMap.put("machineGuid", machineGuid);
		labelValueMap.put("machineName", machineName);
//		labelValueMap.put("machineGrossBets", String.valueOf(machineGrossBets));
//		labelValueMap.put("percentage", percentage.toString());
		
		SettlementEntry entry = SettlementEntry.builder()
		.settlement(settlement)
		.amount(getAmount(machineGrossBets, percentage))
		.dateStart(dateStart.toDate())
		.dateEnd(dateEnd.toDate())
		.description("Machine (" + machineName + ") " + new BigDecimal(machineGrossBets).divide(new BigDecimal(100)).setScale(2, RoundingMode.CEILING) + " at " + percentage.toString() + "%")
		.labelValueMap(labelValueMap)
		.build();
		settlement = client.addSettlementEntry(settlement.getId(), entry).getData();
		return settlement;
	}
	
	private BigDecimal getAmount(Long machineGrossBets, BigDecimal percentage) {
		return 
			new BigDecimal(machineGrossBets)
			.divide(new BigDecimal(100))
			.multiply(percentage.divide(new BigDecimal(100)))
			.setScale(2, RoundingMode.CEILING);
	}
	
	private Long findMachineGrossBets(String domain, String machineGuid, String currency, String dateStart, String dateEnd) throws Exception {
		long scratchBuyAmount = 0;
		long lotteryBuyAmount = 0;
		
		{
			AccountingSummaryDomainLabelValueClient accountingSummaryDomainLabelValueClient = services.target(AccountingSummaryDomainLabelValueClient.class);
			Response<List<SummaryLabelValue>> r = accountingSummaryDomainLabelValueClient.findLimited(domain, Period.GRANULARITY_DAY, "PLAYER_BALANCE", "SCRATCHCARD_BUY", "machine_guid", machineGuid, currency, dateStart, dateEnd);
			List<SummaryLabelValue> d = r.getData();
			if (d == null || d.size() < 1) {
				scratchBuyAmount += 0L;
			} else {
				for (SummaryLabelValue slv: d) {
					scratchBuyAmount += slv.getDebitCents() - slv.getCreditCents();
				};
			}
		}
		
		{
			AccountingSummaryDomainLabelValueClient accountingSummaryDomainLabelValueClient = services.target(AccountingSummaryDomainLabelValueClient.class);
			Response<List<SummaryLabelValue>> r = accountingSummaryDomainLabelValueClient.findLimited(domain, Period.GRANULARITY_DAY, "PLAYER_BALANCE", "LOTTERY_ENTRY_BUY", "machine_guid", machineGuid, currency, dateStart, dateEnd);
			List<SummaryLabelValue> d = r.getData();
			if (d == null || d.size() < 1) {
				lotteryBuyAmount += 0L;
			} else {
				for (SummaryLabelValue slv: d) {
					lotteryBuyAmount += slv.getDebitCents() - slv.getCreditCents();
				};
			}
		}
		
		return (scratchBuyAmount + lotteryBuyAmount);
	}
}
