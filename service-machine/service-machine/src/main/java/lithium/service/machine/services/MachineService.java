package lithium.service.machine.services;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import lithium.client.changelog.Category;
import lithium.client.changelog.SubCategory;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.entity.client.EntityClient;
import lithium.service.machine.client.objects.StatusUpdate;
import lithium.service.machine.data.entities.Domain;
import lithium.service.machine.data.entities.Location;
import lithium.service.machine.data.entities.LocationDistributionConfiguration;
import lithium.service.machine.data.entities.LocationDistributionConfigurationRevision;
import lithium.service.machine.data.entities.Machine;
import lithium.service.machine.data.entities.Relationship;
import lithium.service.machine.data.entities.RelationshipDistributionConfiguration;
import lithium.service.machine.data.entities.RelationshipDistributionConfigurationRevision;
import lithium.service.machine.data.entities.Status;
import lithium.service.machine.data.objects.Distribution;
import lithium.service.machine.data.repositories.LocationDistributionConfigurationRepository;
import lithium.service.machine.data.repositories.LocationDistributionConfigurationRevisionRepository;
import lithium.service.machine.data.repositories.LocationRepository;
import lithium.service.machine.data.repositories.MachineObserverRepository;
import lithium.service.machine.data.repositories.MachineRepository;
import lithium.service.machine.data.repositories.RelationshipDistributionConfigurationRepository;
import lithium.service.machine.data.repositories.RelationshipDistributionConfigurationRevisionRepository;
import lithium.service.machine.data.repositories.RelationshipRepository;
import lithium.service.machine.data.repositories.StatusRepository;
import lithium.service.machine.data.specifications.MachineSpecifications;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MachineService {
	@Autowired MachineRepository machineRepo;
	@Autowired MachineObserverRepository observerRepo;
	@Autowired LocationRepository locationRepo;
	@Autowired LocationDistributionConfigurationRepository locationDistConfigRepo;
	@Autowired LocationDistributionConfigurationRevisionRepository locationDistConfigRevisionRepo;
	@Autowired RelationshipRepository relationshipRepo;
	@Autowired RelationshipDistributionConfigurationRepository relationshipDistConfigRepo;
	@Autowired RelationshipDistributionConfigurationRevisionRepository relationshipDistConfigRevisionRepo;
	@Autowired StatusRepository statusRepo;
	@Autowired DomainService domainService;
	@Autowired ChangeLogService changeLogService;
	@Autowired ModelMapper modelMapper;
	@Autowired LithiumServiceClientFactory services;
	@Autowired AmqpTemplate amqp;
	@Autowired StatusService statusService;

	public DataTableResponse<Machine> table(String domainName, DataTableRequest request, Status status) {
		Domain domain = domainService.findOrCreate(domainName);
		
		Specification<Machine> spec = Specification.where(MachineSpecifications.domain(domain));
		
		if ((request.getSearchValue() != null) && (request.getSearchValue().length() > 0)) {
			Specification<Machine> s = Specification.where(MachineSpecifications.any(request.getSearchValue()));
			spec = (spec == null)? s: spec.and(s);
		}
		
		if (status != null) {
			spec = spec.and(Specification.where(MachineSpecifications.status(status)));
		}
		
		Page<Machine> pageList = machineRepo.findAll(spec, request.getPageRequest());
		
		return new DataTableResponse<>(request, pageList);
	}
	
	@Transactional(rollbackOn=Exception.class)
	public Machine createMachine(lithium.service.machine.client.objects.Machine m, LithiumTokenUtil tokenUtil) throws Exception {
		Domain domain = domainService.findOrCreate(tokenUtil.playerDomainWithRole("MACHINES_MANAGE").getName());
		
		Machine machine = Machine.builder()
		.domain(domain)
		.guid(m.getGuid())
		.status(statusService.NEW)
		.name(m.getName())
		.description(m.getDescription())
		.build();
		machine = machineRepo.save(machine);
		
		if (m.getLocation() != null) {
			Location location = Location.builder()
			.machine(machine)
			.entityUuid(m.getLocation().getEntityUuid())
			.build();
			location = locationRepo.save(location);
			
			Date dStart = m.getLocation().getDistributionConfiguration().getCurrent().getStart();
			DateTime dtStart = null;
			if (dStart != null) {
				dtStart = new DateTime(dStart).withTime(0, 0, 0, 0);
			}
			Date dEnd = m.getLocation().getDistributionConfiguration().getCurrent().getEnd();
			DateTime dtEnd = null;
			if (dEnd != null) {
				if (dStart == null || dEnd.compareTo(dStart) < 0)
					throw new IllegalArgumentException("Start date is null or after end date");
				dtEnd = new DateTime(dEnd).withTime(23, 59, 59, 0);
			}
			
			LocationDistributionConfigurationRevision locationDistConfigRevision = LocationDistributionConfigurationRevision.builder()
			.start(dStart != null? dtStart.toDate(): null)
			.end(dEnd != null? dtEnd.toDate(): null)
			.percentage(m.getLocation().getDistributionConfiguration().getCurrent().getPercentage())
			.build();
			locationDistConfigRevision = locationDistConfigRevisionRepo.save(locationDistConfigRevision);
			
			LocationDistributionConfiguration locationDistConfig = LocationDistributionConfiguration.builder()
			.location(location)
			.current(locationDistConfigRevision)
			.build();
			locationDistConfig = locationDistConfigRepo.save(locationDistConfig);
			
			locationDistConfigRevision.setLocationDistributionConfiguration(locationDistConfig);
			locationDistConfigRevision = locationDistConfigRevisionRepo.save(locationDistConfigRevision);
			
			location.setDistributionConfiguration(locationDistConfig);
			location = locationRepo.save(location);
			
			if (locationDistConfigRevision.getEnd() == null) {
				machine.setLocation(location);
			}
		}
		
		if (m.getRelationships() != null && m.getRelationships().size() > 0) {
			Set<Relationship> relationships = new HashSet<>();
			for (lithium.service.machine.client.objects.Relationship r: m.getRelationships()) {
				Relationship relationship = Relationship.builder()
				.machine(machine)
				.entityUuid(r.getEntityUuid())
				.build();
				relationship = relationshipRepo.save(relationship);
				
				Date dStart = r.getDistributionConfiguration().getCurrent().getStart();
				DateTime dtStart = null;
				if (dStart != null) {
					dtStart = new DateTime(dStart).withTime(0, 0, 0, 0);
				}
				Date dEnd = r.getDistributionConfiguration().getCurrent().getEnd();
				DateTime dtEnd = null;
				if (dEnd != null) {
					dtEnd = new DateTime(dEnd).withTime(23, 59, 59, 0);
				}
				
				RelationshipDistributionConfigurationRevision relationshipDistConfigRevision = RelationshipDistributionConfigurationRevision.builder()
				.start(dStart != null? dtStart.toDate(): null)
				.end(dEnd != null? dtEnd.toDate(): null)
				.percentage(r.getDistributionConfiguration().getCurrent().getPercentage())
				.build();
				relationshipDistConfigRevision = relationshipDistConfigRevisionRepo.save(relationshipDistConfigRevision);
				
				RelationshipDistributionConfiguration relationshipDistConfig = RelationshipDistributionConfiguration.builder()
				.relationship(relationship)
				.current(relationshipDistConfigRevision)
				.build();
				relationshipDistConfig = relationshipDistConfigRepo.save(relationshipDistConfig);
				
				relationshipDistConfigRevision.setRelationshipDistributionConfiguration(relationshipDistConfig);
				relationshipDistConfigRevision = relationshipDistConfigRevisionRepo.save(relationshipDistConfigRevision);
				
				if (relationshipDistConfigRevision.getEnd() != null) {
					relationship.setDeleted(true);
				}
				relationship.setDistributionConfiguration(relationshipDistConfig);
				relationships.add(relationship);
			}
			
			machine.setRelationships(relationships);
		}
		
		machine = machineRepo.save(machine);
		
		List<ChangeLogFieldChange> clfc = changeLogService.compare(machine, new Machine(), new String[] {
			"domain", "guid", "status", "name", "description", "createdDate", "updatedDate", "location", "relationships"
		});
		changeLogService.registerChangesWithDomain("machine", "create", machine.getId(), tokenUtil.username(), null, null, clfc, Category.SUPPORT, SubCategory.SUPPORT, 0, domain.getName());
		
		return machine;
	}
	
	public Machine saveMachine(Machine machine, lithium.service.machine.client.objects.Machine machinePost, Principal principal) throws Exception {
		List<ChangeLogFieldChange> clfc = changeLogService.copy(machinePost, machine,
				new String[] { "guid", "name", "description", "updatedDate" } );
		changeLogService.registerChangesWithDomain("machine", "edit", machine.getId(), principal.getName(), null, null, clfc, Category.SUPPORT, SubCategory.SUPPORT, 0, machine.getDomain().getName());
		machine.setUpdatedDate(new Date());
		machine = machineRepo.save(machine);
		notifyObservers(machine);
		return machine;
	}
	
	public Machine saveStatus(StatusUpdate statusUpdate, Principal principal) throws Exception {
		Machine machine = machineRepo.findOne(statusUpdate.getMachineId());
		
		String oldStatus = machine.getStatus().getName();
		
		Status status = statusRepo.findOne(statusUpdate.getStatusId());
		machine.setStatus(status);
		
		List<ChangeLogFieldChange> clfc = new ArrayList<ChangeLogFieldChange>();
		ChangeLogFieldChange c = ChangeLogFieldChange.builder()
				.field("status")
				.fromValue(oldStatus)
				.toValue(status.getName())
				.build();
		clfc.add(c);
		changeLogService.registerChangesWithDomain(
			"machine",
			status.getName().startsWith("Inactive")? "disable": status.getName().startsWith("Active")? "enable": "edit",
			machine.getId(),
			principal.getName(),
			statusUpdate.getComment(),
			null,
			clfc, Category.SUPPORT, SubCategory.SUPPORT, 0, machine.getDomain().getName()
		);
		
		machine = machineRepo.save(machine);
		notifyObservers(machine);
		return machine;
	}
	
	@Transactional(rollbackOn=Exception.class)
	public Machine addLocation(Machine machine, lithium.service.machine.client.objects.Location l, Principal principal) throws Exception {
		if (l != null) {
			Location location = Location.builder()
			.machine(machine)
			.entityUuid(l.getEntityUuid())
			.build();
			location = locationRepo.save(location);
			
			Date dStart = l.getDistributionConfiguration().getCurrent().getStart();
			DateTime dtStart = null;
			if (dStart != null) {
				dtStart = new DateTime(dStart).withTime(0, 0, 0, 0);
			}
			Date dEnd = l.getDistributionConfiguration().getCurrent().getEnd();
			DateTime dtEnd = null;
			if (dEnd != null) {
				if (dStart == null || dEnd.compareTo(dStart) < 0)
					throw new IllegalArgumentException("Start date is null or after end date");
				dtEnd = new DateTime(dEnd).withTime(23, 59, 59, 0);
			}
			
			LocationDistributionConfigurationRevision locationDistConfigRevision = LocationDistributionConfigurationRevision.builder()
			.percentage(l.getDistributionConfiguration().getCurrent().getPercentage())
			.start(dStart != null? dtStart.toDate(): null)
			.end(dEnd != null? dtEnd.toDate(): null)
			.build();
			locationDistConfigRevision = locationDistConfigRevisionRepo.save(locationDistConfigRevision);
			
			LocationDistributionConfiguration locationDistConfig = LocationDistributionConfiguration.builder()
			.location(location)
			.current(locationDistConfigRevision)
			.build();
			locationDistConfig = locationDistConfigRepo.save(locationDistConfig);
			
			locationDistConfigRevision.setLocationDistributionConfiguration(locationDistConfig);
			locationDistConfigRevision = locationDistConfigRevisionRepo.save(locationDistConfigRevision);
			
			location.setDistributionConfiguration(locationDistConfig);
			location = locationRepo.save(location);
			
			if (locationDistConfigRevision.getEnd() == null) {
				machine.setLocation(location);
				machine = machineRepo.save(machine);
			}
			
			List<ChangeLogFieldChange> clfc = changeLogService.copy(machine, new Machine(), new String[] { "location" });
			changeLogService.registerChangesWithDomain("machine", "edit", machine.getId(), principal.getName(), null, null, clfc, Category.SUPPORT, SubCategory.SUPPORT, 0, machine.getDomain().getName());
		}
		
		return machine;
	}
	
	@Transactional(rollbackOn=Exception.class)
	public Machine saveLocation(Machine machine, lithium.service.machine.client.objects.Location l, Principal principal) throws Exception {
		if (l != null) {
			if (machine.getLocation().getEntityUuid().equals(l.getEntityUuid())) {
				return updateLocationDistConfig(machine, l, principal);
			} else {
				Location oldLocation = Location.builder()
				.id(machine.getLocation().getId())
				.entityUuid(machine.getLocation().getEntityUuid())
				.distributionConfiguration(machine.getLocation().getDistributionConfiguration())
				.build();
				Location location = machine.getLocation();
				location.setEntityUuid(l.getEntityUuid());
				location = locationRepo.save(location);
				ChangeLogFieldChange c = ChangeLogFieldChange.builder()
				.field("location")
				.fromValue(oldLocation.toString())
				.toValue(location.toString())
				.build();
				List<ChangeLogFieldChange> clfc = new ArrayList<>();
				clfc.add(c);
				changeLogService.registerChangesWithDomain("machine", "edit", machine.getId(), principal.getName(), null, null, clfc, Category.SUPPORT, SubCategory.SUPPORT, 0, machine.getDomain().getName());
				machine.setLocation(location);
				machine = machineRepo.save(machine);
				if (location.getDistributionConfiguration().getCurrent().getPercentage()
						.compareTo(l.getDistributionConfiguration().getCurrent().getPercentage()) != 0) {
					machine = updateLocationDistConfig(machine, l, principal);
				}
			}
		}
		return machine;
	}
	
	@Transactional(rollbackOn=Exception.class)
	public Machine updateLocationDistConfig(Machine machine, lithium.service.machine.client.objects.Location l, Principal principal) throws Exception {
		BigDecimal currentPercentage = machine.getLocation().getDistributionConfiguration().getCurrent().getPercentage();
		List<LocationDistributionConfigurationRevision> configs =
			locationDistConfigRevisionRepo.findByLocationDistributionConfiguration(machine.getLocation().getDistributionConfiguration());
		configs.forEach(config -> {
			if (config.getEnd() == null) {
				config.setEnd(new DateTime().withTime(23, 59, 59, 0).toDate());
				config = locationDistConfigRevisionRepo.save(config);
			}
		});
		LocationDistributionConfigurationRevision locationDistConfigRevision = LocationDistributionConfigurationRevision.builder()
		.locationDistributionConfiguration(machine.getLocation().getDistributionConfiguration())
		.percentage(l.getDistributionConfiguration().getCurrent().getPercentage())
		.build();
		locationDistConfigRevision = locationDistConfigRevisionRepo.save(locationDistConfigRevision);
		machine.getLocation().getDistributionConfiguration().setCurrent(locationDistConfigRevision);
		machine = machineRepo.save(machine);
		ChangeLogFieldChange c = ChangeLogFieldChange.builder()
		.field("location (distribution configuration percentage)")
		.fromValue(currentPercentage.toPlainString())
		.toValue(locationDistConfigRevision.getPercentage().toPlainString())
		.build();
		List<ChangeLogFieldChange> clfc = new ArrayList<>();
		clfc.add(c);
		changeLogService.registerChangesWithDomain("machine", "edit", machine.getId(), principal.getName(), null, null, clfc, Category.SUPPORT, SubCategory.SUPPORT, 0, machine.getDomain().getName());
		return machine;
	}
	
	public Machine deleteLocation(Machine machine, Principal principal) throws Exception {
		Machine oldMachine = new Machine();
		oldMachine.setLocation(machine.getLocation());
		machine.getLocation().getDistributionConfiguration().getCurrent().setEnd(new Date());
		machine.setLocation(null);
		machine = machineRepo.save(machine);
		List<ChangeLogFieldChange> clfc = changeLogService.copy(machine, oldMachine, new String[] { "location" });
		changeLogService.registerChangesWithDomain("machine", "edit", machine.getId(), principal.getName(), null, null, clfc, Category.SUPPORT, SubCategory.SUPPORT, 0, machine.getDomain().getName());
		return machine;
	}
	
	@Transactional(rollbackOn=Exception.class)
	public void modifyLocationDistConfigRevisionHistory(Machine machine, LocationDistributionConfigurationRevision locationDistConfigRevision, BigDecimal percentage, Date start, Date end, Principal principal) throws Exception {
		LocationDistributionConfigurationRevision old = LocationDistributionConfigurationRevision.builder()
		.id(locationDistConfigRevision.getId())
		.percentage(locationDistConfigRevision.getPercentage())
		.start(locationDistConfigRevision.getStart())
		.end(locationDistConfigRevision.getEnd())
		.locationDistributionConfiguration(locationDistConfigRevision.getLocationDistributionConfiguration())
		.build();
		locationDistConfigRevision.setPercentage(percentage);
		if (start != null) {
			DateTime dtS = new DateTime(locationDistConfigRevision.getStart());
			DateTime dtStart = new DateTime(start);
			if (dtS.getDayOfMonth() != dtStart.getDayOfMonth() ||
				dtS.getMonthOfYear() != dtStart.getMonthOfYear() ||
				dtS.getYear() != dtStart.getYear()) {
				locationDistConfigRevision.setStart(start);
			}
		}
		if (end != null) {
			if (start == null || end.compareTo(start) < 0)
				throw new IllegalArgumentException("Start date is null or after end date");
			DateTime dtE = new DateTime(locationDistConfigRevision.getEnd());
			DateTime dtEnd = new DateTime(end);
			if (dtE.getDayOfMonth() != dtEnd.getDayOfMonth() ||
				dtE.getMonthOfYear() != dtEnd.getMonthOfYear() ||
				dtE.getYear() != dtEnd.getYear()) {
				locationDistConfigRevision.setEnd(end);
				if (machine.getLocation() != null &&
						machine.getLocation().getDistributionConfiguration().getCurrent().getId().equals(locationDistConfigRevision.getId())) {
					machine.setLocation(null);
					machine = machineRepo.save(machine);
				}
			}
		}
		ChangeLogFieldChange c = ChangeLogFieldChange.builder()
		.field("location (distribution configuration history)")
		.fromValue(old.toString())
		.toValue(locationDistConfigRevision.toString())
		.build();
		List<ChangeLogFieldChange> clfc = new ArrayList<>();
		clfc.add(c);
		changeLogService.registerChangesWithDomain("machine", "edit", machine.getId(), principal.getName(), null, null, clfc, Category.SUPPORT, SubCategory.SUPPORT, 0, machine.getDomain().getName());
		locationDistConfigRevisionRepo.save(locationDistConfigRevision);
	}
	
	@Transactional(rollbackOn=Exception.class)
	public Machine deleteLocationDistConfigRevisionHistory(Machine machine, LocationDistributionConfigurationRevision locationDistConfigRevision, Principal principal) throws Exception {
		ChangeLogFieldChange c = ChangeLogFieldChange.builder()
		.field("location (distribution configuration history)")
		.fromValue(locationDistConfigRevision.toString())
		.toValue(null)
		.build();
		List<ChangeLogFieldChange> clfc = new ArrayList<>();
		clfc.add(c);
		changeLogService.registerChangesWithDomain("machine", "edit", machine.getId(), principal.getName(), null, null, clfc, Category.SUPPORT, SubCategory.SUPPORT, 0, machine.getDomain().getName());
		
		LocationDistributionConfiguration locationDistConfig = locationDistConfigRevision.getLocationDistributionConfiguration();
		if (locationDistConfig.getCurrent() != null && locationDistConfig.getCurrent().equals(locationDistConfigRevision)) {
			locationDistConfig.setCurrent(null);
			locationDistConfig = locationDistConfigRepo.save(locationDistConfig);
			locationDistConfigRevision.setLocationDistributionConfiguration(null);
			locationDistConfigRevision = locationDistConfigRevisionRepo.save(locationDistConfigRevision);
			machine.setLocation(null);
			machine = machineRepo.save(machine);
		} 
		
		locationDistConfigRevisionRepo.delete(locationDistConfigRevision);
		
		List<LocationDistributionConfigurationRevision> revs = locationDistConfigRevisionRepo.findByLocationDistributionConfiguration(locationDistConfig);
		if (revs.size() == 0) {
			Location location = locationDistConfig.getLocation();
			location.setDistributionConfiguration(null);
			location = locationRepo.save(location);
			locationDistConfigRepo.delete(locationDistConfig);
			locationRepo.delete(location);
		}
		
		return machine;
	}
	
	public DataTableResponse<LocationDistributionConfigurationRevision> locationHistory(Machine machine, DataTableRequest request, Principal principal) {
		Page<LocationDistributionConfigurationRevision> pageList = locationDistConfigRevisionRepo.findByLocationDistributionConfigurationLocationMachineOrderByStartDescEndDesc(machine, request.getPageRequest());
		try {
			EntityClient entityClient = services.target(EntityClient.class, "service-entity", true);
			for (LocationDistributionConfigurationRevision revision: pageList.getContent()) {
				revision.setEntity(entityClient.findByUuid(revision.getLocationDistributionConfiguration().getLocation().getEntityUuid()).getData());
			}
		} catch (LithiumServiceClientFactoryException e) {
			log.error(e.getMessage(), e);
		}
		return new DataTableResponse<>(request, pageList);
	}
	
	@Transactional(rollbackOn=Exception.class)
	public Machine addRelationship(Machine machine, lithium.service.machine.client.objects.Relationship r, Principal principal) throws Exception {
		if (r != null) {
			Machine oldMachine = new Machine();
			oldMachine.setRelationships(machine.getRelationships());
			
			Relationship relationship = Relationship.builder()
			.machine(machine)
			.entityUuid(r.getEntityUuid())
			.build();
			relationship = relationshipRepo.save(relationship);
			
			Date dStart = r.getDistributionConfiguration().getCurrent().getStart();
			DateTime dtStart = null;
			if (dStart != null) {
				dtStart = new DateTime(dStart).withTime(0, 0, 0, 0);
			}
			Date dEnd = r.getDistributionConfiguration().getCurrent().getEnd();
			DateTime dtEnd = null;
			if (dEnd != null) {
				if (dStart == null || dEnd.compareTo(dStart) < 0)
					throw new IllegalArgumentException("Start date is null or after end date");
				dtEnd = new DateTime(dEnd).withTime(23, 59, 59, 0);
			}
			
			RelationshipDistributionConfigurationRevision relationshipDistConfigRevision = RelationshipDistributionConfigurationRevision.builder()
			.percentage(r.getDistributionConfiguration().getCurrent().getPercentage())
			.start(dStart != null? dtStart.toDate(): null)
			.end(dEnd != null? dtEnd.toDate(): null)
			.build();
			relationshipDistConfigRevision = relationshipDistConfigRevisionRepo.save(relationshipDistConfigRevision);
			
			RelationshipDistributionConfiguration relationshipDistConfig = RelationshipDistributionConfiguration.builder()
			.relationship(relationship)
			.current(relationshipDistConfigRevision)
			.build();
			relationshipDistConfig = relationshipDistConfigRepo.save(relationshipDistConfig);
			
			relationshipDistConfigRevision.setRelationshipDistributionConfiguration(relationshipDistConfig);
			relationshipDistConfigRevision = relationshipDistConfigRevisionRepo.save(relationshipDistConfigRevision);
			
			relationship.setDistributionConfiguration(relationshipDistConfig);
			relationship = relationshipRepo.save(relationship);
			
			Set<Relationship> relationships = new HashSet<>();
			relationships.addAll(machine.getRelationships());
			relationships.add(relationship);
			
			machine.setRelationships(relationships);
			machine = machineRepo.save(machine);
			
			List<ChangeLogFieldChange> clfc = changeLogService.copy(machine, oldMachine, new String[] { "relationships" });
			changeLogService.registerChangesWithDomain("machine", "edit", machine.getId(), principal.getName(), null, null, clfc, Category.SUPPORT, SubCategory.SUPPORT, 0, machine.getDomain().getName());
		}
		
		return machine;
	}
	
	@Transactional(rollbackOn=Exception.class)
	public Machine saveRelationship(Machine machine, Relationship relationship, lithium.service.machine.client.objects.Relationship r, Principal principal) throws Exception {
		if (r != null) {
			if (relationship.getEntityUuid().equals(r.getEntityUuid())) {
				return updateRelationshipDistConfig(machine, relationship, r.getDistributionConfiguration().getCurrent().getPercentage(), principal);
			} else {
				Relationship oldRelationship = Relationship.builder()
				.id(relationship.getId())
				.entityUuid(relationship.getEntityUuid())
				.distributionConfiguration(relationship.getDistributionConfiguration())
				.build();
				machine.getRelationships().remove(relationship);
				relationship.setEntityUuid(r.getEntityUuid());
				relationship = relationshipRepo.save(relationship);
				machine.getRelationships().add(relationship);
				ChangeLogFieldChange c = ChangeLogFieldChange.builder()
				.field("relationship")
				.fromValue(oldRelationship.toString())
				.toValue(relationship.toString())
				.build();
				List<ChangeLogFieldChange> clfc = new ArrayList<>();
				clfc.add(c);
				changeLogService.registerChangesWithDomain("machine", "edit", machine.getId(), principal.getName(), null, null, clfc, Category.SUPPORT, SubCategory.SUPPORT, 0, machine.getDomain().getName());
				if (relationship.getDistributionConfiguration().getCurrent().getPercentage()
						.compareTo(r.getDistributionConfiguration().getCurrent().getPercentage()) != 0) {
					machine = updateRelationshipDistConfig(machine, relationship, r.getDistributionConfiguration().getCurrent().getPercentage(), principal);
				}
			}
		}
		return machine;
	}
	
	public Machine deleteRelationship(Machine machine, Relationship relationship, Principal principal) throws Exception {
		Machine oldMachine = new Machine();
		Set<Relationship> oldRelationships = new HashSet<>();
		oldRelationships.addAll(machine.getRelationships());
		oldMachine.setRelationships(oldRelationships);
		machine.getRelationships().remove(relationship);
		relationship.getDistributionConfiguration().getCurrent().setEnd(new Date());
		relationship.setDeleted(true);
		machine = machineRepo.save(machine);
		List<ChangeLogFieldChange> clfc = changeLogService.copy(machine, oldMachine, new String[] { "relationships" });
		changeLogService.registerChangesWithDomain("machine", "edit", machine.getId(), principal.getName(), null, null, clfc, Category.SUPPORT, SubCategory.SUPPORT, 0, machine.getDomain().getName());
		return machine;
	}
	
	@Transactional(rollbackOn=Exception.class)
	public Machine updateRelationshipDistConfig(Machine machine, Relationship r, BigDecimal percentage, Principal principal) throws Exception {
		BigDecimal currentPercentage = r.getDistributionConfiguration().getCurrent().getPercentage();
		List<RelationshipDistributionConfigurationRevision> configs =
			relationshipDistConfigRevisionRepo.findByRelationshipDistributionConfiguration(r.getDistributionConfiguration());
		configs.forEach(config -> {
			if (config.getEnd() == null) {
				config.setEnd(new DateTime().withTime(23, 59, 59, 0).toDate());
				config = relationshipDistConfigRevisionRepo.save(config);
			}
		});
		RelationshipDistributionConfigurationRevision relationshipDistConfigRevision = RelationshipDistributionConfigurationRevision.builder()
		.relationshipDistributionConfiguration(r.getDistributionConfiguration())
		.percentage(percentage)
		.build();
		relationshipDistConfigRevision = relationshipDistConfigRevisionRepo.save(relationshipDistConfigRevision);
		r.getDistributionConfiguration().setCurrent(relationshipDistConfigRevision);
		r = relationshipRepo.save(r);
		machine = machineRepo.save(machine);
		ChangeLogFieldChange c = ChangeLogFieldChange.builder()
		.field("relationship (distribution configuration percentage)")
		.fromValue(currentPercentage.toPlainString())
		.toValue(relationshipDistConfigRevision.getPercentage().toPlainString())
		.build();
		List<ChangeLogFieldChange> clfc = new ArrayList<>();
		clfc.add(c);
		changeLogService.registerChangesWithDomain("machine", "edit", machine.getId(), principal.getName(), null, null, clfc, Category.SUPPORT, SubCategory.SUPPORT, 0, machine.getDomain().getName());
		return machine;
	}
	
	@Transactional(rollbackOn=Exception.class)
	public void modifyRelationshipDistConfigRevisionHistory(Machine machine, RelationshipDistributionConfigurationRevision relationshipDistConfigRevision, BigDecimal percentage, Date start, Date end, Principal principal) throws Exception {
		RelationshipDistributionConfigurationRevision old = RelationshipDistributionConfigurationRevision.builder()
		.id(relationshipDistConfigRevision.getId())
		.percentage(relationshipDistConfigRevision.getPercentage())
		.start(relationshipDistConfigRevision.getStart())
		.end(relationshipDistConfigRevision.getEnd())
		.relationshipDistributionConfiguration(relationshipDistConfigRevision.getRelationshipDistributionConfiguration())
		.build();
		relationshipDistConfigRevision.setPercentage(percentage);
		if (start != null) {
			DateTime dtS = new DateTime(relationshipDistConfigRevision.getStart());
			DateTime dtStart = new DateTime(start);
			if (dtS.getDayOfMonth() != dtStart.getDayOfMonth() ||
				dtS.getMonthOfYear() != dtStart.getMonthOfYear() ||
				dtS.getYear() != dtStart.getYear()) {
				relationshipDistConfigRevision.setStart(start);
			}
		}
		if (end != null) {
			if (start == null || end.compareTo(start) < 0)
				throw new IllegalArgumentException("Start date is null or after end date");
			DateTime dtE = new DateTime(relationshipDistConfigRevision.getEnd());
			DateTime dtEnd = new DateTime(end);
			if (dtE.getDayOfMonth() != dtEnd.getDayOfMonth() ||
				dtE.getMonthOfYear() != dtEnd.getMonthOfYear() ||
				dtE.getYear() != dtEnd.getYear()) {
				relationshipDistConfigRevision.setEnd(end);
				Relationship relationship = relationshipDistConfigRevision.getRelationshipDistributionConfiguration().getRelationship();
				if (!relationship.getDeleted() &&
						relationship.getDistributionConfiguration().getCurrent().getId().equals(relationshipDistConfigRevision.getId())) {
					relationship.setDeleted(true);
					relationship = relationshipRepo.save(relationship);
				}
			}
		}
		ChangeLogFieldChange c = ChangeLogFieldChange.builder()
		.field("relationship (distribution configuration history)")
		.fromValue(old.toString())
		.toValue(relationshipDistConfigRevision.toString())
		.build();
		List<ChangeLogFieldChange> clfc = new ArrayList<>();
		clfc.add(c);
		changeLogService.registerChangesWithDomain("machine", "edit", machine.getId(), principal.getName(), null, null, clfc, Category.SUPPORT, SubCategory.SUPPORT, 0, machine.getDomain().getName());
		relationshipDistConfigRevisionRepo.save(relationshipDistConfigRevision);
	}
	
	public void notifyObservers(Machine machine) {
		observerRepo.findByMachine(machine).forEach((observer) -> {
			amqp.convertAndSend(observer.getObserverGuid(), "{ 'socketId': '" + observer.getSocketSessionId() + "' }");
		});
	}
	
	@Transactional(rollbackOn=Exception.class)
	public Machine deleteRelationshipDistConfigRevisionHistory(Machine machine, RelationshipDistributionConfigurationRevision relationshipDistConfigRevision, Principal principal) throws Exception {
		ChangeLogFieldChange c = ChangeLogFieldChange.builder()
		.field("relationship (distribution configuration history)")
		.fromValue(relationshipDistConfigRevision.toString())
		.toValue(null)
		.build();
		List<ChangeLogFieldChange> clfc = new ArrayList<>();
		clfc.add(c);
		changeLogService.registerChangesWithDomain("machine", "edit", machine.getId(), principal.getName(), null, null, clfc, Category.SUPPORT, SubCategory.SUPPORT, 0, machine.getDomain().getName());
		
		RelationshipDistributionConfiguration relationshipDistConfig = relationshipDistConfigRevision.getRelationshipDistributionConfiguration();
		if (relationshipDistConfig.getCurrent() != null && relationshipDistConfig.getCurrent().equals(relationshipDistConfigRevision)) {
			Relationship relationship = relationshipDistConfig.getRelationship();
			relationship.setDeleted(true);
			relationship = relationshipRepo.save(relationship);
			relationshipDistConfig.setCurrent(null);
			relationshipDistConfig = relationshipDistConfigRepo.save(relationshipDistConfig);
			relationshipDistConfigRevision.setRelationshipDistributionConfiguration(null);
			relationshipDistConfigRevision = relationshipDistConfigRevisionRepo.save(relationshipDistConfigRevision);
		}
		
		relationshipDistConfigRevisionRepo.delete(relationshipDistConfigRevision);
		
		List<RelationshipDistributionConfigurationRevision> revs = relationshipDistConfigRevisionRepo.findByRelationshipDistributionConfiguration(relationshipDistConfig);
		if (revs.size() == 0) {
			Relationship relationship = relationshipDistConfig.getRelationship();
			relationship.setDistributionConfiguration(null);
			relationship = relationshipRepo.save(relationship);
			relationshipDistConfigRepo.delete(relationshipDistConfig);
			Set<Relationship> relationships = new HashSet<>();
			relationships.addAll(machine.getRelationships());
			Iterator<Relationship> iterator = relationships.iterator();
			while (iterator.hasNext()) {
				Relationship r = iterator.next();
				if (r.getId().equals(relationship.getId())) {
					iterator.remove();
					break;
				}
			}
			machine.setRelationships(relationships);
			machine = machineRepo.save(machine);
			relationshipRepo.delete(relationship);
		}
		
		return machine;
	}
	
	public DataTableResponse<RelationshipDistributionConfigurationRevision> relationshipHistory(Machine machine, DataTableRequest request, Principal principal) {
		Page<RelationshipDistributionConfigurationRevision> pageList = relationshipDistConfigRevisionRepo.findByRelationshipDistributionConfigurationRelationshipMachineOrderByStartDescEndDesc(machine, request.getPageRequest());
		try {
			EntityClient entityClient = services.target(EntityClient.class, "service-entity", true);
			for (RelationshipDistributionConfigurationRevision revision: pageList.getContent()) {
				revision.setEntity(entityClient.findByUuid(revision.getRelationshipDistributionConfiguration().getRelationship().getEntityUuid()).getData());
			}
		} catch (LithiumServiceClientFactoryException e) {
			log.error(e.getMessage(), e);
		}
		return new DataTableResponse<>(request, pageList);
	}
	
	public Distribution getMachineDistribution(Long machineId, String machineGuid, Date date, String domainName) throws Exception {
		log.debug("getMachineDistribution | machineId " + machineId + " machineGuid " + machineGuid + " date " + date + " domainName " + domainName);
		
		if (machineId == null && machineGuid == null) {
			Distribution distribution = Distribution.builder()
			.message("No machine identifier provided!")
			.build();
			return distribution;
		}
		
		Machine machine = null;
		if (machineId != null) {
			machine = machineRepo.findOne(machineId);
		} else if (machineGuid != null) {
			Domain domain = domainService.findOrCreate(domainName);
			machine = machineRepo.findByDomainAndGuid(domain, machineGuid);
		}
		
		final DateTime dtDate = new DateTime(date).withTimeAtStartOfDay();
		log.debug("date " + dtDate.toDate());
		
		List<LocationDistributionConfigurationRevision> locationDistConfigRevisions =
			locationDistConfigRevisionRepo.findByLocationDistributionConfigurationLocationMachine(machine);
		locationDistConfigRevisions = locationDistConfigRevisions.stream()
		.filter(config -> {
			return ((!config.getStart().after(dtDate.toDate())) && (config.getEnd() == null || !config.getEnd().before(dtDate.toDate())));
		}).collect(Collectors.toList());
		
		List<RelationshipDistributionConfigurationRevision> relationshipDistConfigRevisions =
			relationshipDistConfigRevisionRepo.findByRelationshipDistributionConfigurationRelationshipMachine(machine);
		relationshipDistConfigRevisions = relationshipDistConfigRevisions.stream()
		.filter(config -> {
			return ((!config.getStart().after(dtDate.toDate())) && (config.getEnd() == null || !config.getEnd().before(dtDate.toDate())));
		}).collect(Collectors.toList());
		
		if (locationDistConfigRevisions.size() > 0 || relationshipDistConfigRevisions.size() > 0) {
			Map<String, lithium.service.entity.client.objects.Entity> entities = new LinkedHashMap<>();
			
			final EntityClient entityClient = services.target(EntityClient.class, "service-entity", true);
			
			locationDistConfigRevisions.stream().forEach(config -> {
				String entityUuid = config.getLocationDistributionConfiguration().getLocation().getEntityUuid();
				lithium.service.entity.client.objects.Entity entity = entities.get(entityUuid);
				if (entity != null) {
					config.setEntity(entity);
				} else {
					if (entityClient != null) {
						entity = entityClient.findByUuid(entityUuid).getData();
						entities.put(entityUuid, entity);
						config.setEntity(entity);
					}
				}
			});
			
			relationshipDistConfigRevisions.stream().forEach(config -> {
				String entityUuid = config.getRelationshipDistributionConfiguration().getRelationship().getEntityUuid();
				lithium.service.entity.client.objects.Entity entity = entities.get(entityUuid);
				if (entity != null) {
					config.setEntity(entity);
				} else {
					if (entityClient != null) {
						entity = entityClient.findByUuid(entityUuid).getData();
						entities.put(entityUuid, entity);
						config.setEntity(entity);
					}
				}
			});
		}
		
		return 
				Distribution.builder()
				.locationDistConfigRevisions(locationDistConfigRevisions)
				.relationshipDistConfigRevisions(relationshipDistConfigRevisions)
				.message("OK")
				.build();
	}
}
