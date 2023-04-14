package lithium.service.entity.services;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import lithium.client.changelog.Category;
import lithium.client.changelog.SubCategory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.entity.client.objects.AddressBasic;
import lithium.service.entity.client.objects.StatusUpdate;
import lithium.service.entity.data.entities.Address;
import lithium.service.entity.data.entities.BankDetails;
import lithium.service.entity.data.entities.Domain;
import lithium.service.entity.data.entities.Entity;
import lithium.service.entity.data.entities.EntityType;
import lithium.service.entity.data.entities.Status;
import lithium.service.entity.data.repositories.AddressRepository;
import lithium.service.entity.data.repositories.BankDetailsRepository;
import lithium.service.entity.data.repositories.EntityRepository;
import lithium.service.entity.data.repositories.EntityTypeRepository;
import lithium.service.entity.data.repositories.StatusRepository;
import lithium.service.entity.data.specifications.EntitySpecifications;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EntityService {
	@Autowired BankDetailsRepository bankDetailsRepo;
	@Autowired DomainService domainService;
	@Autowired EntityRepository entityRepo;
	@Autowired EntityTypeRepository entityTypeRepo;
	@Autowired AddressRepository addressRepo;
	@Autowired StatusRepository statusRepo;
	@Autowired ChangeLogService changeLogService;
	@Autowired ModelMapper modelMapper;
	
	public DataTableResponse<Entity> table(String domainName, DataTableRequest request, EntityType entityType) {
		Domain domain = domainService.findOrCreate(domainName);
		
		Specification<Entity> spec = Specification.where(EntitySpecifications.domain(domain));
		
		if ((request.getSearchValue() != null) && (request.getSearchValue().length() > 0)) {
			Specification<Entity> s = Specification.where(EntitySpecifications.any(request.getSearchValue()));
			spec = (spec == null)? s: spec.and(s);
		}
		
		if (entityType != null) {
			spec = spec.and(Specification.where(EntitySpecifications.entityType(entityType)));
		}
		
		Page<Entity> pageList = entityRepo.findAll(spec, request.getPageRequest());
		
		return new DataTableResponse<>(request, pageList);
	}
	
	public Entity findByUuid(String uuid) {
		return entityRepo.findByUuid(uuid);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public Entity createEntity(String domainName, lithium.service.entity.client.objects.Entity e, Principal principal) throws Exception {
		Address physicalAddress = null;
		if (e.getPhysicalAddress() != null) {
			physicalAddress = modelMapper.map(e.getPhysicalAddress(), Address.class);
			log.info("physicalAddress : " + physicalAddress);
			physicalAddress = addressRepo.save(physicalAddress);
		}
		Address billingAddress = null;
		if (e.getBillingAddress() != null) {
			billingAddress = modelMapper.map(e.getBillingAddress(), Address.class);
			log.info("billingAddress : " + billingAddress);
			billingAddress = addressRepo.save(billingAddress);
		}
		
		BankDetails bankDetails = null;
		if (e.getBankDetails() != null) {
			bankDetails = modelMapper.map(e.getBankDetails(), BankDetails.class);
			bankDetails = bankDetailsRepo.save(bankDetails);
		}
		
		Entity entity = Entity.builder()
		.domain(domainService.findOrCreate(domainName))
		.uuid(UUID.randomUUID().toString())
		.status(statusRepo.findOne(e.getStatus().getId()))
		.name(e.getName())
		.email(e.getEmail())
		.telephoneNumber(e.getTelephoneNumber())
		.cellphoneNumber(e.getCellphoneNumber())
		.physicalAddress(physicalAddress)
		.billingAddress(billingAddress)
		.entityType(entityTypeRepo.findOne(e.getEntityType().getId()))
		.bankDetails(bankDetails)
		.build();
		entity = entityRepo.save(entity);
		
		if (physicalAddress != null) {
			physicalAddress.setEntityId(entity.getId());
			physicalAddress = addressRepo.save(physicalAddress);
		}
		if (billingAddress != null) {
			billingAddress.setEntityId(entity.getId());
			billingAddress = addressRepo.save(billingAddress);
		}
		
		List<ChangeLogFieldChange> clfc = changeLogService.copy(entity, new Entity(), new String[] {
			"domain", "uuid", "status", "name", "email", "telephoneNumber", "cellphoneNumber",
			"createdDate", "updatedDate", "physicalAddress", "billingAddress", "entityType",
			"bankDetails"
		});
		changeLogService.registerChangesWithDomain("entity", "create", entity.getId(), principal.getName(), null, null, clfc, Category.SUPPORT, SubCategory.SUPPORT, 0, domainName);
				
		return entity;
	}
	
	public Entity saveEntity(Entity entity, lithium.service.entity.client.objects.Entity entityPost, Principal principal) throws Exception {
		List<ChangeLogFieldChange> clfc = changeLogService.copy(entityPost, entity,
				new String[] { "name", "email", "telephoneNumber", "cellphoneNumber" } );
		if (!entity.getEntityType().getId().equals(entityPost.getEntityType().getId())) {
			EntityType entityType = entityTypeRepo.findOne(entityPost.getEntityType().getId());
			ChangeLogFieldChange c = ChangeLogFieldChange.builder()
			.field("entityType")
			.fromValue(entity.getEntityType().toString())
			.toValue(entityType.toString())
			.build();
			entity.setEntityType(entityType);
			clfc.add(c);
		}
		changeLogService.registerChangesWithDomain("entity", "edit", entity.getId(), principal.getName(), null, null, clfc, Category.SUPPORT, SubCategory.ENTITY, 0, entity.getDomain().getName());
		entity.setUpdatedDate(new Date());
		entity = entityRepo.save(entity);
		return entity;
	}
	
	public Entity saveBankDetails(Entity entity, lithium.service.entity.client.objects.Entity entityPost, Principal principal) throws Exception {
		Entity e = modelMapper.map(entityPost, Entity.class);
		e.setId(entity.getBankDetails().getId());
		List<ChangeLogFieldChange> clfc = changeLogService.copy(e, entity, new String[] { "bankDetails" } );
		changeLogService.registerChangesWithDomain("entity", "edit", entity.getId(), principal.getName(), null, null, clfc, Category.SUPPORT, SubCategory.ENTITY, 0, entity.getDomain().getName());
		BankDetails bd = e.getBankDetails();
		bd = bankDetailsRepo.save(bd);
		entity.setBankDetails(bd);
		return entity;
	}
	
	public Entity saveStatus(StatusUpdate statusUpdate, Principal principal) throws Exception {
		Entity entity = entityRepo.findOne(statusUpdate.getEntityId());
		
		String oldStatus = entity.getStatus().getName();
		
		Status status = statusRepo.findOne(statusUpdate.getStatusId());
		entity.setStatus(status);
		
		List<ChangeLogFieldChange> clfc = new ArrayList<ChangeLogFieldChange>();
		ChangeLogFieldChange c = ChangeLogFieldChange.builder()
				.field("status")
				.fromValue(oldStatus)
				.toValue(status.getName())
				.build();
		clfc.add(c);
		changeLogService.registerChangesWithDomain(
			"entity",
			status.getName().startsWith("Disable")? "disable": status.getName().startsWith("Enable")? "enable": "edit",
			entity.getId(),
			principal.getName(),
			statusUpdate.getComment(),
			null,
			clfc, Category.SUPPORT, SubCategory.ENTITY, 0, entity.getDomain().getName()
		);
		
		entity = entityRepo.save(entity);
		return entity;
	}
	
	public Entity saveAddress(AddressBasic addressBasic, Principal principal) throws Exception {
		log.debug("AddressBasic : " + addressBasic);
		Address address = modelMapper.map(addressBasic, Address.class);
		log.debug("Address : " + address);
		
		if (address.getId() != null) {
			address.setId(null);
		}
		
		address = addressRepo.save(address);
		log.debug("Saved Address : " + address);
		
		Entity entity = entityRepo.findOne(addressBasic.getEntityId());
		Entity oldEntity = new Entity();
		oldEntity.setPhysicalAddress(entity.getPhysicalAddress());
		oldEntity.setBillingAddress(entity.getBillingAddress());
		
		if (addressBasic.isPhysicalAddress()) {
			entity.setPhysicalAddress(address);
		} else if (addressBasic.isBillingAddress()) {
			entity.setBillingAddress(address);
		}
		
		List<ChangeLogFieldChange> clfc = changeLogService.copy(entity, oldEntity, new String[] { "physicalAddress", "billingAddress" });
		changeLogService.registerChangesWithDomain("entity", "edit", entity.getId(), principal.getName(), null, null, clfc, Category.SUPPORT, SubCategory.ENTITY, 0, entity.getDomain().getName());
		
		entity = entityRepo.save(entity);
		log.debug("Saved entity : " + entity);
		
		return entity;
	}
}
