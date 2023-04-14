package lithium.service.settlement.services;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.settlement.data.entities.Address;
import lithium.service.settlement.data.entities.BankDetails;
import lithium.service.settlement.data.entities.BatchSettlements;
import lithium.service.settlement.data.entities.Entity;
import lithium.service.settlement.data.entities.Settlement;
import lithium.service.settlement.data.entities.SettlementEntry;
import lithium.service.settlement.data.entities.User;
import lithium.service.settlement.data.repositories.AddressRepository;
import lithium.service.settlement.data.repositories.BankDetailsRepository;
import lithium.service.settlement.data.repositories.SettlementEntryRepository;
import lithium.service.settlement.data.repositories.SettlementRepository;
import lithium.service.settlement.data.specifications.SettlementEntrySpecifications;
import lithium.service.settlement.data.specifications.SettlementSpecifications;

@Service
public class SettlementService {
	@Autowired DomainService domainService;
	@Autowired EntityService entityService;
	@Autowired UserService userService;
	@Autowired SettlementRepository settlementRepo;
	@Autowired SettlementEntryRepository settlementEntryRepo;
	@Autowired SettlementEntryLabelValueService settlementEntryLabelValueService;
	@Autowired SettlementPDFService settlementPDFService;
	@Autowired BatchSettlementsService batchSettlementsService;
	@Autowired LithiumServiceClientFactory services;
	@Autowired AddressRepository addressRepo;
	@Autowired BankDetailsRepository bankDetailsRepo;
	@Autowired ModelMapper modelMapper;
	@Autowired ExternalEntityService externalEntityService;
	@Autowired ExternalUserService externalUserService;
	
	public Settlement enrichDataWithExternalUserOrEntity(Settlement settlement) {
		if (settlement.getEntity() != null && settlement.getEntity().getUuid() != null) {
			try {
				settlement.setExternalEntity(externalEntityService.findByUuid(settlement.getEntity().getUuid()));
			} catch (Exception e) {}
		}
		if (settlement.getUser() != null && settlement.getUser().getGuid() != null) {
			try {
				settlement.setExternalUser(externalUserService.findByGuid(settlement.getUser().getGuid()));
			} catch (Exception e) {}
		}
		return settlement;
	}
	
	public DataTableResponse<Settlement> settlementsTable(BatchSettlements batchSettlements, DataTableRequest request) {
		Specification<Settlement> spec = Specification.where(SettlementSpecifications.inBatch(batchSettlements.getId()));
		
		if ((request.getSearchValue() != null) && (request.getSearchValue().length() > 0)) {
			Specification<Settlement> s = Specification.where(SettlementSpecifications.any(request.getSearchValue()));
			spec = (spec == null)? s: spec.and(s);
		}
		
		Page<Settlement> pageList = settlementRepo.findAll(spec, request.getPageRequest());
		
		pageList.getContent().forEach(settlement -> {
			settlement = enrichDataWithExternalUserOrEntity(settlement);
		});
		
		return new DataTableResponse<>(request, pageList);
	}
	
	public DataTableResponse<SettlementEntry> settlementEntriesTable(Settlement settlement, DataTableRequest request) {
		Specification<SettlementEntry> spec = Specification.where(SettlementEntrySpecifications.settlement(settlement));
		
		if ((request.getSearchValue() != null) && (request.getSearchValue().length() > 0)) {
			Specification<SettlementEntry> s = Specification.where(SettlementEntrySpecifications.any(request.getSearchValue()));
			spec = (spec == null)? s: spec.and(s);
		}
		
		Page<SettlementEntry> pageList = settlementEntryRepo.findAll(spec, request.getPageRequest());
		
		return new DataTableResponse<>(request, pageList);
	}
	
	public Settlement findByEntity(String batchName, String entityUuid, Date dateStart, Date dateEnd) {
		return settlementRepo.findByBatchSettlementsNameIgnoreCaseAndEntityUuidAndDateStartAndDateEndAndOpenTrue(batchName, entityUuid, dateStart, dateEnd);
	}
	
	public Settlement findByUser(String batchName, String userGuid, Date dateStart, Date dateEnd) throws UnsupportedEncodingException {
		return settlementRepo.findByBatchSettlementsNameIgnoreCaseAndUserGuidAndDateStartAndDateEndAndOpenTrue(batchName, userGuid, dateStart, dateEnd);
	}
	
	public void deleteSettlements(String domainName, String batchName) {
		settlementRepo.deleteByDomainNameAndBatchSettlementsName(domainName, batchName);
		BatchSettlements batchSettlements = batchSettlementsService.find(domainName, batchName);
		batchSettlements.setOpen(true);
		batchSettlements = batchSettlementsService.save(batchSettlements);
	}
	
	@Transactional(rollbackOn=Exception.class)
	public Settlement createSettlement(lithium.service.settlement.client.objects.Settlement s) {
		BatchSettlements batchSettlements = batchSettlementsService.findOrCreate(s.getDomain().getName(), s.getBatchSettlements().getName());
		
		Entity entity = null;
		if (s.getEntity() != null && s.getEntity().getUuid() != null) {
			entity = entityService.findOrCreate(s.getEntity().getUuid());
		}
		
		User user = null;
		if (s.getUser() != null && s.getUser().getGuid() != null) {
			user = userService.findOrCreate(s.getUser().getGuid());
		}
		
		Address physicalAddress = null;
		if (s.getPhysicalAddress() != null) {
			physicalAddress = modelMapper.map(s.getPhysicalAddress(), Address.class);
			physicalAddress.setId(null);
			physicalAddress = addressRepo.save(physicalAddress);
		}
		
		Address billingAddress = null;
		if (s.getBillingAddress() != null) {
			billingAddress = modelMapper.map(s.getBillingAddress(), Address.class);
			billingAddress.setId(null);
			billingAddress = addressRepo.save(billingAddress);
		}
		
		BankDetails bankDetails = null;
		if (s.getBankDetails() != null) {
			bankDetails = modelMapper.map(s.getBankDetails(), BankDetails.class);
			bankDetails.setId(null);
			bankDetails = bankDetailsRepo.save(bankDetails);
		}
		
		Settlement settlement = Settlement.builder()
		.domain(domainService.findOrCreate(s.getDomain().getName()))
		.entity(entity)
		.user(user)
		.createdBy(s.getCreatedBy())
		.dateStart(s.getDateStart())
		.dateEnd(s.getDateEnd())
		.open(true)
		.batchSettlements(batchSettlements)
		.physicalAddress(physicalAddress)
		.billingAddress(billingAddress)
		.bankDetails(bankDetails)
		.build();
		settlement = settlementRepo.save(settlement);
		
		return settlement;
	}
	
	@Transactional(rollbackOn=Exception.class)
	public Settlement addSettlementEntry(Settlement settlement, lithium.service.settlement.client.objects.SettlementEntry entry) throws Exception {
		if (!settlement.getOpen())
			throw new Exception("Settlement is already closed");
		
		SettlementEntry settlementEntry = SettlementEntry.builder()
		.settlement(settlement)
		.amount(entry.getAmount())
		.dateStart(entry.getDateStart())
		.dateEnd(entry.getDateEnd())
		.description(entry.getDescription())
		.build();
		settlementEntry = settlementEntryRepo.save(settlementEntry);
		
		settlement.setTotal(settlement.getTotal() != null? settlement.getTotal().add(settlementEntry.getAmount()): settlementEntry.getAmount());
		settlement = settlementRepo.save(settlement);
		
		if (entry.getLabelValueMap() != null) {
			for (String key: entry.getLabelValueMap().keySet()) {
				String value = entry.getLabelValueMap().get(key);
				settlementEntryLabelValueService.findOrCreate(settlementEntry, key, value);
			}
		}
		
		return settlement;
	}
	
	@Transactional(rollbackOn=Exception.class)
	public Settlement finalizeSettlement(Settlement settlement) throws Exception {
		settlement = settlementPDFService.create(settlement);
		settlementPDFService.sendPdf(settlement);
		settlement = settlementPDFService.markPdfSent(settlement);
		settlement.setOpen(false);
		settlement = settlementRepo.save(settlement);
		return settlement;
	}
	
	public Settlement resendPdf(Settlement settlement) throws LithiumServiceClientFactoryException {
		settlementPDFService.sendPdf(settlement);
		settlement = settlementPDFService.markPdfSent(settlement);
		return settlement;
	}
}
