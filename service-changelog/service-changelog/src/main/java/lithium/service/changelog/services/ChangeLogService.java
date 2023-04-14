package lithium.service.changelog.services;

import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.changelog.data.context.ChangelogContext;
import lithium.service.changelog.data.entities.Category;
import lithium.service.changelog.data.entities.ChangeLog;
import lithium.service.changelog.data.entities.ChangeLogEntity;
import lithium.service.changelog.data.entities.ChangeLogFieldChange;
import lithium.service.changelog.data.entities.ChangeLogType;
import lithium.service.changelog.data.entities.Domain;
import lithium.service.changelog.data.entities.SubCategory;
import lithium.service.changelog.data.entities.User;
import lithium.service.changelog.data.repositories.CategoryRepository;
import lithium.service.changelog.data.repositories.ChangeLogEntityRepository;
import lithium.service.changelog.data.repositories.ChangeLogFieldChangeRepository;
import lithium.service.changelog.data.repositories.ChangeLogRepository;
import lithium.service.changelog.data.repositories.ChangeLogTypeRepository;
import lithium.service.changelog.data.repositories.SubCategoryRepository;
import lithium.service.changelog.data.specifications.ChangeLogSpecifications;
import lithium.service.client.page.SimplePageImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class ChangeLogService {
	@Autowired
	ChangeLogRepository changeLogRepository;
	@Autowired
	ChangeLogEntityRepository changeLogEntityRepository;
	@Autowired
	ChangeLogFieldChangeRepository changeLogFieldChangeRepository;
	@Autowired
	ChangeLogTypeRepository changeLogTypeRepository;
	@Autowired
	UserService userService;
	@Autowired
	DomainService domainService;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	SubCategoryRepository subCategoryRepository;

	@Autowired
	ChangeLogService self;

	private ChangelogContext prepare(lithium.client.changelog.objects.ChangeLog changeLog) {
		String comments = changeLog.getComments();
		if (comments != null && comments.length() > 65535) {
			log.warn("Comments being truncated on changelog:: entity: " + changeLog.getEntity() + " type:" + changeLog.getType() + " guid:" + changeLog.getAuthorGuid() + " comment:" + comments);
			comments = comments.substring(0, 65534);
		}
		User user = userService.findOrCreate(changeLog.getAuthorGuid());
		if (user == null)
			throw new IllegalArgumentException("Invalid authorUsername");

		ChangeLogEntity entity = changeLogEntityRepository.findByName(changeLog.getEntity());
		if (entity == null) {
			entity = new ChangeLogEntity();
			entity.setName(changeLog.getEntity());
			changeLogEntityRepository.save(entity);
		}

		ChangeLogType type = changeLogTypeRepository.findByName(changeLog.getType());
		if (type == null) {
			type = new ChangeLogType();
			type.setName(changeLog.getType());
			changeLogTypeRepository.save(type);
		}

		Category category = null;
		SubCategory subCategory = null;
		if (changeLog.getCategoryName() != null) {
			category = categoryRepository.findByName(changeLog.getCategoryName());
		}
		if (changeLog.getSubCategoryName() != null) {
			//FIXME: Technical Debt Ticket (PLAT-3389) to re-align sub-categories to categories. Currently, some changelogs are created using sub-categories
			// that are not linked to a category via the enums; therefore, we are now allowing this exception of creating a sub-category with a link to category outside of the enum constraints until this gets addressed.
			Category finalCategory = category;
			subCategory = subCategoryRepository.findOrCreateByName(changeLog.getSubCategoryName(), () -> new SubCategory(finalCategory));
		}
		Domain domain = domainService.findOrCreate("default");
		if(changeLog.getDomainName() != null && !changeLog.getDomainName().isEmpty()){
			domain = domainService.findOrCreate(changeLog.getDomainName());
		}

		ChangelogContext context = ChangelogContext.builder().build();

		ChangeLog cl = context.changeLog();
		cl.setAuthorUser(user);
		cl.setChangeDate(new Date());
		cl.setEntity(entity);
		cl.setEntityRecordId(changeLog.getEntityRecordId());
		cl.setType(type);
		cl.setComments(comments);
		cl.setPriority(changeLog.getPriority());
		cl.setPinned(false);
		cl.setAdditionalInfo(changeLog.getAdditionalInfo());
		cl.setAuthorFullName(changeLog.getAuthorFullName());
		cl.setCategory(category);
		cl.setSubCategory(subCategory);
		cl.setDeleted(false);
		cl.setDomain(domain);

		context.setChangeLog(cl);

		return context;
	}

	private void startChange(ChangelogContext changelogContext) {
		changelogContext.setChangeLog(changeLogRepository.save(changelogContext.getChangeLog()));
	}

	private void registerFieldChange(ChangeLog cl, String field, String from, String to) {
		ChangeLogFieldChange clfc = new ChangeLogFieldChange();
		clfc.setChangeLog(cl);
		clfc.setField(field);
		clfc.setFromValue(from);
		clfc.setToValue(to);
		changeLogFieldChangeRepository.save(clfc);
	}

	public void registerFieldChange(ChangeLog cl, String field, String from, String to, User user, Date date) {
		ChangeLogFieldChange changeLogFieldChanges = new ChangeLogFieldChange();
		changeLogFieldChanges.setChangeLog(cl);
		changeLogFieldChanges.setField(field);
		changeLogFieldChanges.setFromValue(from);
		changeLogFieldChanges.setToValue(to);
		changeLogFieldChanges.setEditedBy(user);
		changeLogFieldChanges.setDateUpdated(date);
		changeLogFieldChangeRepository.save(changeLogFieldChanges);
	}
	
	private void finishChange(ChangelogContext changelogContext) {
		changeLogRepository.save(changelogContext.complete());
	}

	@TimeThisMethod
	public void registerChangesWithDomain(lithium.client.changelog.objects.ChangeLog changeLog) {
		ChangelogContext changelogContext = prepare(changeLog);
		changelogContext.setChanges(changeLog.getChanges());
		self.registerChanges(changelogContext);
	}

	@Retryable
	@Transactional
	@TimeThisMethod
	void registerChanges(ChangelogContext changelogContext) {
		SW.start("startChange");
		startChange(changelogContext);
		SW.stop();
		SW.start("registerFieldChange");
		if (changelogContext.getChanges() != null) {
			for (lithium.client.changelog.objects.ChangeLogFieldChange cfc: changelogContext.getChanges()) {
				registerFieldChange(changelogContext.getChangeLog(), cfc.getField(), cfc.getFromValue(), cfc.getToValue());
			}
		}
		SW.stop();
		SW.start("finishChange");
		finishChange(changelogContext);
		SW.stop();
		log.trace(SW.getFromThreadLocal().prettyPrint());
	}

	public ChangeLogs list(String entityName, Long entityRecordId, @RequestParam int p) {
		Page<lithium.client.changelog.objects.ChangeLog> pageResult = findChangeLogs(entityRecordId, new String[] { entityName }, null, null, p, 10, "desc", "changeDate");
		List<lithium.client.changelog.objects.ChangeLog> changeLogs = pageResult.getContent();
		return new ChangeLogs(changeLogs, pageResult.hasNext());
	}
	
	public ChangeLogs listLimited(ChangeLogRequest changeLogRequest) {
		Page<lithium.client.changelog.objects.ChangeLog> pageResult = findChangeLogs(changeLogRequest.getEntityRecordId(), changeLogRequest.getEntities(), changeLogRequest.getTypes(), changeLogRequest.getSearchValue(), changeLogRequest.getPage(), changeLogRequest.getPageSize(), changeLogRequest.getSortDirection(), changeLogRequest.getSortField());
		List<lithium.client.changelog.objects.ChangeLog> changeLogs = pageResult.getContent();
		return new ChangeLogs(changeLogs, pageResult.hasNext());
	}
	
	public SimplePageImpl<lithium.client.changelog.objects.ChangeLog> listLimitedPaged(ChangeLogRequest changeLogRequest) {
		SimplePageImpl<lithium.client.changelog.objects.ChangeLog> pageResult = findChangeLogs(changeLogRequest.getEntityRecordId(), changeLogRequest.getEntities(), changeLogRequest.getTypes(), changeLogRequest.getSearchValue(),
				changeLogRequest.getPage(), changeLogRequest.getPageSize(), changeLogRequest.getSortDirection(), changeLogRequest.getSortField());
		return pageResult;
	}
	
	private SimplePageImpl<lithium.client.changelog.objects.ChangeLog> findChangeLogs(Long entityRecordId, String[] entities, String[] types, String searchValue, Integer page, Integer pageSize, String sortDirection, String sortField) {
		Specification<ChangeLog> spec = Specification.where(ChangeLogSpecifications.withIdAndEntitiesAndTypes(entityRecordId, getEntitiesFromEntityNames(entities), getTypesFromTypeNames(types)));
		if (searchValue != null && !searchValue.isEmpty()) {
			Specification<ChangeLog> s = Specification.where(ChangeLogSpecifications.any(searchValue));
			spec = spec.and(s);
		}
		PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by(Direction.fromOptionalString(sortDirection).orElse(null), sortField));
		Page<ChangeLog> changeLogs = changeLogRepository.findAll(spec, pageRequest);
		return new SimplePageImpl<lithium.client.changelog.objects.ChangeLog>(changeLogObjectListWithFieldChanges(entityRecordId, changeLogs.getContent()), changeLogs.getNumber(), changeLogs.getSize(), changeLogs.getTotalElements());
	}
	
	private List<lithium.client.changelog.objects.ChangeLog> changeLogObjectListWithFieldChanges(Long entityRecordId, List<ChangeLog> changeLogs) {
		List<lithium.client.changelog.objects.ChangeLog> result = new ArrayList<lithium.client.changelog.objects.ChangeLog>();
		for (ChangeLog c : changeLogs) {
			List<ChangeLogFieldChange> cfcl = changeLogFieldChangeRepository.findByChangeLog(c);
			ArrayList<lithium.client.changelog.objects.ChangeLogFieldChange> fields = new ArrayList<lithium.client.changelog.objects.ChangeLogFieldChange>();
			for (ChangeLogFieldChange cfc : cfcl) {
				String editedBy = (cfc.getEditedBy() != null && cfc.getEditedBy().getGuid() != null) ? cfc.getEditedBy().getGuid() : c.getAuthorUser().getGuid();
				fields.add(new lithium.client.changelog.objects.ChangeLogFieldChange(cfc.getId(), cfc.getField(), cfc.getFromValue(), cfc.getToValue(),editedBy));
			}
			result.add(
					lithium.client.changelog.objects.ChangeLog.builder()
							.entity(c.getEntity().getName())
							.entityRecordId(entityRecordId)
							.changeDate(c.getChangeDate())
							.authorGuid(c.getAuthorUser().getGuid())
							.authorFullName(c.getAuthorFullName())
							.comments(c.getComments())
							.type(c.getType().getName())
							.changes(fields).build()
			);
		}
		return result;
	}
	
	private List<ChangeLogEntity> getEntitiesFromEntityNames(String[] entityNames) {
		if (entityNames == null) {
			throw new IllegalArgumentException("There must be at least one entity");
		}
		List<ChangeLogEntity> changeLogEntities = new ArrayList<ChangeLogEntity>();
		for (String entity: entityNames) {
			ChangeLogEntity e = findOrCreateEntity(entity);
			changeLogEntities.add(e);
		}
		return changeLogEntities;
	}
	
	private ChangeLogEntity findOrCreateEntity(String entityName) {
		ChangeLogEntity entity = changeLogEntityRepository.findByName(entityName);
		if (entity == null) {
			entity = changeLogEntityRepository.save(
					ChangeLogEntity.builder().name(entityName).build()
				);
		}
		return entity;
	}
	
	private List<ChangeLogType> getTypesFromTypeNames(String[] typeNames) {
		List<ChangeLogType> changeLogTypes = new ArrayList<ChangeLogType>();
		if (typeNames == null) {
			Iterator<ChangeLogType> iterator = changeLogTypeRepository.findAll().iterator();
			while (iterator.hasNext()) {
				changeLogTypes.add(iterator.next());
			}
		} else {
			for (String type: typeNames) {
				ChangeLogType t = changeLogTypeRepository.findByName(type);
				if (t == null) {
					throw new IllegalArgumentException("Invalid type name");
				}
				changeLogTypes.add(t);
			}
		}
		return changeLogTypes;
	}
}
