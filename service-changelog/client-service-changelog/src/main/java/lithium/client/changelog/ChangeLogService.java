package lithium.client.changelog;

import lithium.client.changelog.objects.ChangeLog;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.page.SimplePageImpl;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ChangeLogService {
	@Autowired LithiumServiceClientFactory factory;

	/**
	 * Performs a copy of properties from the source to the target object.
	 * The source object is the latest version usually and the target is the previous version usually.
	 * This means the field changes will show "from target" "to source" in the ChangeLogFieldChange list
	 * @param source
	 * @param target
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	public List<ChangeLogFieldChange> copy(Object source, Object target, String[] fields) throws Exception {
		return ChangeMapper.copy(source, target, fields);
	}

	/**
	 * Performs a comparison of properties from the source to the target object.
	 * The source object is the latest version usually and the target is the previous version usually.
	 * This means the field changes will show "from target" "to source" in the ChangeLogFieldChange list
	 * @param source
	 * @param target
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	public List<ChangeLogFieldChange> compare(Object source, Object target, String[] fields) throws Exception {
		return ChangeMapper.compare(source, target, fields);
	}

	public ChangeLogFieldChange compareById(Object source, Object target, String field, String display) throws Exception {
		return ChangeMapper.compare(source, target, field, display);
	}

	private ChangeLogClient getChangeLogClient() {
		ChangeLogClient cl = null;
		try {
			cl = factory.target(ChangeLogClient.class, "service-changelog", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting changelog service", e);
		}
		return cl;
	}

	public void registerChangesBlocking(String entity, String type, long entityRecordId, String authorGuid, String comments, String additionalInfo, List<ChangeLogFieldChange> changes, Category category, SubCategory subCategory, int priority, String domainName) throws Exception {
		ChangeLog changeLog = ChangeLog.builder()
				.entity(entity).type(type)
				.entityRecordId(entityRecordId)
				.authorGuid(authorGuid).comments(comments).priority(priority)
				.additionalInfo(additionalInfo).categoryName(category.getName()).subCategoryName(subCategory.getName())
				.changes(changes).domainName(domainName).build();
		getChangeLogClient().registerChangesWithDomain(changeLog);
	}

	// FIXME: We need a better way to add priority. What should low, med, high be?
	@Async
	public void registerChangesWithDomain(String entity, String type, long entityRecordId, String authorGuid,
										  String comments, String additionalInfo, List<ChangeLogFieldChange> changes,
										  Category category, SubCategory subCategory, int priority, String domainName) {
		ChangeLog changeLog = ChangeLog.builder()
				.entity(entity).type(type)
				.entityRecordId(entityRecordId)
				.authorGuid(authorGuid).comments(comments)
				.additionalInfo(additionalInfo)
				.categoryName(category.getName())
				.subCategoryName(subCategory.getName())
				.priority(priority)
				.domainName(domainName)
				.changes(changes).build();
		getChangeLogClient().registerChangesWithDomain(changeLog);
	}

	@Async
	public void registerChangesWithDomainAndFullName(String entity, String type, long entityRecordId, String authorGuid,
										  String comments, String additionalInfo, List<ChangeLogFieldChange> changes,
										  Category category, SubCategory subCategory, int priority, String domainName, String fullName) {
		ChangeLog changeLog = ChangeLog.builder()
				.entity(entity).type(type)
				.entityRecordId(entityRecordId)
				.authorGuid(authorGuid)
				.comments(comments)
				.authorFullName(fullName)
				.additionalInfo(additionalInfo)
				.categoryName(category.getName())
				.subCategoryName(subCategory.getName())
				.priority(priority)
				.domainName(domainName)
				.changes(changes).build();
		getChangeLogClient().registerChangesWithDomain(changeLog);
	}

	@Async
	public void registerChangesForNotesWithFullNameAndDomain(String entity, String type, long entityRecordId, String authorGuid, LithiumTokenUtil util,
			String comments, String additionalInfo, List<ChangeLogFieldChange> changes, Category category, SubCategory subCategory, int priority, String domainName) {

		String name = setName(util);
		ChangeLog changeLog = ChangeLog.builder()
				.entity(entity).type(type)
				.entityRecordId(entityRecordId)
				.authorGuid(authorGuid)
				.authorFullName(name)
				.comments(comments)
				.additionalInfo(additionalInfo)
				.categoryName(category.getName())
				.subCategoryName(subCategory.getName())
				.priority(priority)
				.domainName(domainName)
				.changes(changes).build();
		getChangeLogClient().registerChangesWithDomain(changeLog);
	}

	@Async
	public void registerChangesSystem(String entity, String type, long entityRecordId, String comments, String additionalInfo, List<ChangeLogFieldChange> changes, Category category, SubCategory subCategory, int priority, String domainName) {

		registerChangesForNotesWithFullNameAndDomain(entity, type, entityRecordId, "System", null, comments, additionalInfo, changes, category, subCategory, priority, domainName);
	}

	public Response<ChangeLogs> list(String entity, long id, int page) throws Exception {
		return getChangeLogClient().list(entity, id, page);
	}
	
	public Response<SimplePageImpl<ChangeLog>> listLimitedPaged(ChangeLogRequest changeLogRequest) throws Exception {
		return getChangeLogClient().listLimitedPaged(changeLogRequest);
	}
	
	public Response<ChangeLogs> listLimited(ChangeLogRequest changeLogRequest) throws Exception {
		return getChangeLogClient().listLimited(changeLogRequest);
	}

	private String setName(LithiumTokenUtil util){
		//We need to use full name instead of username and so check for first name and last name through the lithium token and principal

		String fullName = "System";

		if(util != null && util.firstName() != null && !util.firstName().isEmpty() && util.lastName() != null && !util.lastName().isEmpty()) {
			fullName = util.firstName() + " " + util.lastName();
		}

		return fullName;
	}

	public Response<String> addNote(ChangeLog changeLog) throws Exception {
		return getChangeLogClient().addNote(changeLog);
	}
}
