package lithium.service.changelog.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.changelog.data.entities.Category;
import lithium.service.changelog.data.entities.ChangeLog;
import lithium.service.changelog.data.entities.ChangeLogEntity;
import lithium.service.changelog.data.entities.ChangeLogType;
import lithium.service.changelog.data.entities.SubCategory;
import lithium.service.changelog.data.entities.User;
import lithium.service.changelog.data.repositories.ChangeLogEntityRepository;
import lithium.service.changelog.data.repositories.ChangeLogFieldChangeRepository;
import lithium.service.changelog.data.repositories.ChangeLogRepository;
import lithium.service.changelog.data.repositories.ChangeLogTypeRepository;
import lithium.service.changelog.data.specifications.ChangeLogSpecifications;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.NoteSection;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Service
public class ChangelogEntriesService {
	@Autowired private ChangeLogRepository changeLogRepository;
	@Autowired private ChangeLogEntityRepository entityRepository;
	@Autowired private ChangeLogFieldChangeRepository clfcRepository;
	@Autowired private ChangeLogTypeRepository typeRepository;
	@Autowired private ChangeLogService changeLogService;
	@Autowired private DomainService domainService;
	@Autowired private UserService userService;

	public Iterable<ChangeLogEntity> entities() {
		return entityRepository.findAll();
	}

	public Iterable<ChangeLogType> types() {
		return typeRepository.findAll();
	}

	public ChangeLog setPriority(Long changeLogId, Integer priority)
	{
		ChangeLog changeLog = changeLogRepository.findOne(changeLogId);
		if (changeLog == null) return null;
		changeLog.setPriority(priority);
		changeLogRepository.save(changeLog);
		return changeLog;
	}

	public ChangeLog setPinned(Long changeLogId, boolean pinned)
	{
		ChangeLog changeLog = changeLogRepository.findOne(changeLogId);
		if (changeLog == null) return null;
		changeLog.setPinned(pinned);
		changeLogRepository.save(changeLog);
		return changeLog;
	}

	public ChangeLog setDeleted(Long changeLogId, boolean deleted)
	{
		ChangeLog changeLog = changeLogRepository.findOne(changeLogId);
		if (changeLog == null) return null;
		changeLog.setDeleted(deleted);
		changeLogRepository.save(changeLog);
		return changeLog;
	}

	public ChangeLog changeLogWithFieldChanges(ChangeLog changeLog) {
		changeLog.setFieldChanges(clfcRepository.findByChangeLog(changeLog));
		return changeLog;
	}

    public Page<ChangeLog> find(Date changeDateRangeStart, Date changeDateRangeEnd, String[] entities, String[] types, String priorityFrom, String priorityTo, String entryRecordId, String pinned,
                                String[] categories, String[] subCategories, String deleted, String searchValue, String[] domainNames, Pageable pageable) {
        Specification<ChangeLog> spec = null;

		spec = addToSpec(changeDateRangeStart, false, spec, ChangeLogSpecifications::changeDateRangeStart);
        spec = addToSpec(changeDateRangeEnd, true, spec, ChangeLogSpecifications::changeDateRangeEnd);
        spec = addToSpec(entities, spec, ChangeLogSpecifications::entities);
        spec = addToSpec(types, spec, ChangeLogSpecifications::types);
        spec = addToSpec(priorityFrom, spec, ChangeLogSpecifications::priorityFrom);
		spec = addToSpec(priorityTo, spec, ChangeLogSpecifications::priorityTo);
        spec = addToSpec(pinned, spec, ChangeLogSpecifications::pinned);
        spec = addToSpec(entryRecordId, spec, ChangeLogSpecifications::entryRecordId);
        spec = addToSpec(searchValue, spec, ChangeLogSpecifications::any);
        spec = addToSpec(categories, spec, ChangeLogSpecifications::categories);
        spec = addToSpec(subCategories, spec, ChangeLogSpecifications::subCategories);
        spec = addToSpec(deleted, spec, ChangeLogSpecifications::deleted);
		spec = addToSpec(domainService.findDomainIdsByDomainNames(domainNames), spec, ChangeLogSpecifications::domain);

		return changeLogRepository.findAll(spec, pageable);
    }

	public void addNote(@RequestBody lithium.client.changelog.objects.ChangeLog  changeLog, LithiumTokenUtil util) throws Exception {
			List<ChangeLogFieldChange> clfc = new ArrayList<>();
			changeLogService.registerChangesWithDomain(lithium.client.changelog.objects.ChangeLog.builder()
					.entity("user.note").type("created")
					.entityRecordId(changeLog.getEntityRecordId())
					.authorGuid(util.getJwtUser().getGuid())
					.authorFullName(changeLog.getAuthorFullName())
					.comments(changeLog.getComments())
					.categoryName(changeLog.getCategoryName())
					.subCategoryName(changeLog.getSubCategoryName())
					.priority(changeLog.getPriority())
					.domainName(changeLog.getDomainName())
					.changes(clfc).build());
	}

	public void addMigrationNote(@RequestBody lithium.client.changelog.objects.ChangeLog changeLog)
			throws Exception {
		List<ChangeLogFieldChange> clfc = new ArrayList<>();
		changeLogService.registerChangesWithDomain(lithium.client.changelog.objects.ChangeLog.builder()
				.entity("migration.note").type("created")
				.entityRecordId(changeLog.getEntityRecordId())
				.domainName(changeLog.getDomainName())
				.changeDate(changeLog.getChangeDate())
				.dateUpdated(changeLog.getDateUpdated())
				.deleted(changeLog.isDeleted())
				.authorGuid(changeLog.getAuthorGuid())
				.authorFullName(changeLog.getAuthorFullName())
				.comments(changeLog.getComments())
				.categoryName(changeLog.getCategoryName())
				.subCategoryName(changeLog.getSubCategoryName())
				.priority(changeLog.getPriority())
				.domainName(changeLog.getDomainName())
				.changes(clfc).build());
	}

	public lithium.client.changelog.objects.ChangeLog updateNote(long changeLogId, String authorGuid, Category category, SubCategory subCategory,
																 int priority, String comments, LithiumTokenUtil util) {
		ChangeLog changeLogRecord = null;
		Date updatedDate = new Date();
		StringBuilder authorFullName = null;
		List<ChangeLogFieldChange> changes = new ArrayList<>();

		try {
			changeLogRecord = changeLogRepository.findByIdAndAuthorUserGuid(changeLogId, authorGuid);
			ChangeLogType changeLogType = typeRepository.findByName(lithium.util.ChangeLogType.EDIT.toString());
			authorFullName = new StringBuilder().append(util.getJwtUser().getFirstName()).append(" ").append(util.getJwtUser().getLastName());

			User editedBy = null;
			if(util.getJwtUser() != null && util.getJwtUser().guid() != null) {
				editedBy  = userService.findOrCreate(util.getJwtUser().guid());
			}
			changes = getChangeLogFieldChanges(category, subCategory, priority, comments, changeLogRecord, changes);

			if(changeLogRecord != null && changeLogType != null) {
				changeLogRecord.setType(changeLogType);
				changeLogRecord.setCategory(category);
				changeLogRecord.setSubCategory(subCategory);
				changeLogRecord.setPriority(priority);
				changeLogRecord.setComments(comments);
				changeLogRecord = changeLogRepository.save(changeLogRecord);

				for(lithium.client.changelog.objects.ChangeLogFieldChange change: changes) {
					changeLogService.registerFieldChange(changeLogRecord, change.getField(), change.getFromValue(), change.getToValue(), editedBy, updatedDate);
				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}

		return changeLogRecord == null ? null :lithium.client.changelog.objects.ChangeLog.builder()
				.authorFullName(changeLogRecord.getAuthorFullName()).comments(changeLogRecord.getComments())
				.authorGuid(changeLogRecord.getAuthorUser().getGuid()).categoryName(changeLogRecord.getCategory().getName())
				.subCategoryName(changeLogRecord.getSubCategory() != null ? changeLogRecord.getSubCategory().getName() : null).priority(changeLogRecord.getPriority())
				.type(changeLogRecord.getType().getName()).updatedBy(authorFullName.toString()).dateUpdated(updatedDate).build();
	}

	private List<lithium.client.changelog.objects.ChangeLogFieldChange> addChangeLogFieldChanges(String oldValue, String newDobDay, String field, List<lithium.client.changelog.objects.ChangeLogFieldChange> changes) {
		changes.add(
				lithium.client.changelog.objects.ChangeLogFieldChange.builder()
						.field(field)
						.fromValue(oldValue)
						.toValue(newDobDay)
						.build()
		);
		return changes;
	}

	private List<lithium.client.changelog.objects.ChangeLogFieldChange> getChangeLogFieldChanges(
			Category category, SubCategory subCategory, int priority, String comments, ChangeLog changeLogRecord,
			List<lithium.client.changelog.objects.ChangeLogFieldChange> changes) {

		if(!category.getName().equals(changeLogRecord.getCategory().getName())) {
			changes = addChangeLogFieldChanges(changeLogRecord.getCategory().getName(), category.getName(), NoteSection.CATEGORY.toString(), changes);
		}

		if(subCategory != null && !subCategory.getName().equals(changeLogRecord.getSubCategory() != null ? changeLogRecord.getSubCategory().getName() : null)) {
			changes = addChangeLogFieldChanges(changeLogRecord.getSubCategory() != null ? changeLogRecord.getSubCategory().getName() : null, subCategory.getName(), NoteSection.SUBCATEGORY.toString(), changes);
		}

		if(!changeLogRecord.getPriority().equals(priority)) {
			changes = addChangeLogFieldChanges(changeLogRecord.getPriority().toString(), String.valueOf(priority), NoteSection.PRIORITY.toString(), changes);
		}

		if(!comments.equals(changeLogRecord.getComments())) {
			changes = addChangeLogFieldChanges(changeLogRecord.getComments(), comments, NoteSection.COMMENTS.toString(), changes);
		}
		return changes;
	}



	private Specification<ChangeLog> addToSpec(final Date aDate, boolean addDay, Specification<ChangeLog> spec,
												Function<Date, Specification<ChangeLog>> predicateMethod) {
		if (aDate != null) {
			DateTime someDate = new DateTime(aDate);
			if (addDay) {
				someDate = someDate.plusDays(1).withTimeAtStartOfDay();
			} else {
				someDate = someDate.withTimeAtStartOfDay();
			}
			Specification<ChangeLog> localSpec = Specification.where(predicateMethod.apply(someDate.toDate()));
			spec = (spec == null) ? localSpec : spec.and(localSpec);
			return spec;
		}
		return spec;
	}

	private Specification<ChangeLog> addToSpec(final String aString, Specification<ChangeLog> spec,
												Function<String, Specification<ChangeLog>> predicateMethod) {
		if (aString != null && !aString.isEmpty()) {
			Specification<ChangeLog> localSpec = Specification.where(predicateMethod.apply(aString));
			spec = (spec == null) ? localSpec : spec.and(localSpec);
			return spec;
		}
		return spec;
	}

	private Specification<ChangeLog> addToSpec(final String[] arrayOfStrings, Specification<ChangeLog> spec,
												Function<String[], Specification<ChangeLog>> predicateMethod) {
		if (arrayOfStrings != null && arrayOfStrings.length > 0) {
			Specification<ChangeLog> localSpec = Specification.where(predicateMethod.apply(arrayOfStrings));
			spec = (spec == null) ? localSpec : spec.and(localSpec);
			return spec;
		}
		return spec;
	}

	private Specification<ChangeLog> addToSpec(Long[] longArray, Specification<ChangeLog> spec,
											   Function<Long[], Specification<ChangeLog>> predicateMethod) {
		if (longArray != null && longArray.length > 0) {
			Specification<ChangeLog> localSpec = Specification.where(predicateMethod.apply(longArray));
			spec = (spec == null) ? localSpec : spec.and(localSpec);
			return spec;
		}
		return spec;
	}
}
