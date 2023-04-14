package lithium.service.changelog.controllers.backoffice;

import lithium.service.Response;
import lithium.service.changelog.data.entities.Category;
import lithium.service.changelog.data.entities.ChangeLog;
import lithium.service.changelog.data.entities.ChangeLogEntity;
import lithium.service.changelog.data.entities.ChangeLogFieldChange;
import lithium.service.changelog.data.entities.ChangeLogType;
import lithium.service.changelog.data.entities.SubCategory;
import lithium.service.changelog.data.repositories.CategoryRepository;
import lithium.service.changelog.data.repositories.ChangeLogFieldChangeRepository;
import lithium.service.changelog.data.repositories.SubCategoryRepository;
import lithium.service.changelog.services.ChangeLogService;
import lithium.service.changelog.services.ChangelogEntriesService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.user.client.UserClient;
import lithium.service.user.client.objects.User;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;


@RestController
@RequestMapping("/backoffice/changelogs/global")
@Slf4j
public class BackOfficeChangelogEntriesController {
	@Autowired
	private ChangelogEntriesService service;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private SubCategoryRepository subCategoryRepository;
	@Autowired
	ChangeLogFieldChangeRepository changeLogFieldChangeRepository;
	@Autowired
	private ChangeLogService changeLogService;

	@Autowired
	private LithiumServiceClientFactory lithiumServiceClientFactory;

	@GetMapping("/entities")
	public Response<Iterable<ChangeLogEntity>> entities() {
		return Response.<Iterable<ChangeLogEntity>>builder()
				.data(service.entities())
				.status(Response.Status.OK)
				.build();
	}

	@GetMapping("/types")
	public Response<Iterable<ChangeLogType>> types() {
		return Response.<Iterable<ChangeLogType>>builder()
				.data(service.types())
				.status(Response.Status.OK)
				.build();
	}

	@RequestMapping("/table")
	public DataTableResponse<ChangeLog> table(
			@RequestParam(name="changeDateRangeStart", required=false) @DateTimeFormat(pattern="yyyy-MM-dd") Date changeDateRangeStart,
			@RequestParam(name="changeDateRangeEnd", required=false) @DateTimeFormat(pattern="yyyy-MM-dd") Date changeDateRangeEnd,
			@RequestParam(name="commaSepEntities", required=false) String commaSepEntities,
			@RequestParam(name="commaSepTypes", required=false) String commaSepTypes,
			@RequestParam(name="commaSepCategory", required=false) String commaSepCategory,
			@RequestParam(name="commaSepSubCategory", required=false) String commaSepSubCategory,
			@RequestParam(name="priorityFrom", required=false) String priorityFrom,
			@RequestParam(name="priorityTo", required=false) String priorityTo,
			@RequestParam(name="entryRecordId", required=false) String entryRecordId,
			@RequestParam(name="pinned", required=false) String pinned,
			@RequestParam(name="deleted", required=false) String deleted,
			@RequestParam(name="withChanges", required=false) Boolean withChanges,
			@RequestParam(name="domainName[]", required=false) String domainName,
			DataTableRequest request
	) {
		log.debug("AdminChangelogEntriesController.table [changeDateRangeStart="+changeDateRangeStart
				+", changeDateRangeEnd="+changeDateRangeEnd+", commaSepEntities="+commaSepEntities+", commaSepTypes="+commaSepTypes+"]");
		if (ObjectUtils.isEmpty(domainName) && ObjectUtils.isEmpty(entryRecordId)) {
			return new DataTableResponse<>(request, Collections.emptyList());
		}
		String[] entities = (commaSepEntities != null && !commaSepEntities.isEmpty()) ? commaSepEntities.split(",") : null;
		String[] types = (commaSepTypes != null && !commaSepTypes.isEmpty()) ? commaSepTypes.split(",") : null;
		String[] categories = (commaSepCategory != null && !commaSepCategory.isEmpty()) ? commaSepCategory.split(",") : null;
		String[] subCategories = (commaSepSubCategory != null && !commaSepSubCategory.isEmpty()) ? commaSepSubCategory.split(",") : null;
		Page<ChangeLog> table = service.find(changeDateRangeStart, changeDateRangeEnd, entities, types, priorityFrom, priorityTo, entryRecordId, pinned,
				categories, subCategories, deleted, request.getSearchValue(), domainName != null ? domainName.split(",") : null, request.getPageRequest());
		for(ChangeLog cl: table.getContent()) {
			cl.getAuthorUser().setGuid(escapeHtml(cl.getAuthorUser().getGuid()));
		}
		if (withChanges != null && withChanges) {
			List<ChangeLogFieldChange> changelogFieldsList = changeLogFieldChangeRepository.findByChangeLogIn(table.getContent());
			table.getContent().stream().forEachOrdered(c -> c.setFieldChanges(changelogFieldsList.stream().filter(f -> f.getChangeLog().getId() == c.getId()).collect(Collectors.toList())));
			table = mapChangeLogAuthorFullName(table, request.getPageRequest());
		}

		return new DataTableResponse<>(request, table);
	}

	@GetMapping("/entry/{id}")
	public Response<ChangeLog> changeLog(@PathVariable("id") ChangeLog changeLog) {
		return Response.<ChangeLog>builder()
			.data(service.changeLogWithFieldChanges(changeLog))
			.status(Response.Status.OK)
			.build();
	}

	@PostMapping("/entry/{id}/priority/{priority}")
	public Response<ChangeLog> changeLog(@PathVariable("id") Long changeLogId, @PathVariable("priority") Integer priority) {
		ChangeLog changeLog = service.setPriority(changeLogId, priority);
		if (changeLog != null) {
			return Response.<ChangeLog>builder()
					.data(service.changeLogWithFieldChanges(changeLog))
					.status(Response.Status.OK)
					.build();
		} else {
			return Response.<ChangeLog>builder()
					.status(Response.Status.NOT_FOUND)
					.build();
		}
	}

	@PostMapping("/entry/{id}/pinned/{pinned}")
	public Response<ChangeLog> changeLog(@PathVariable("id") Long changeLogId, @PathVariable("pinned") Boolean pinned) {
		ChangeLog changeLog = service.setPinned(changeLogId, pinned);
		if (changeLog != null) {
			return Response.<ChangeLog>builder()
					.data(service.changeLogWithFieldChanges(changeLog))
					.status(Response.Status.OK)
					.build();
		} else {
			return Response.<ChangeLog>builder()
					.status(Response.Status.NOT_FOUND)
					.build();
		}
	}

	@PostMapping("/entry/{id}/deleted/{deleted}")
	public Response<ChangeLog> changeLogDeleted(@PathVariable("id") Long changeLogId, @PathVariable("deleted") boolean deleted) {
		ChangeLog changeLog = service.setDeleted(changeLogId, deleted);
		if (changeLog != null) {
			return Response.<ChangeLog>builder()
					.data(service.changeLogWithFieldChanges(changeLog))
					.status(Response.Status.OK)
					.build();
		} else {
			return Response.<ChangeLog>builder()
					.status(Response.Status.NOT_FOUND)
					.build();
		}
	}

	@GetMapping("/categories")
	public Response<Iterable<Category>> categories() {
		return Response.<Iterable<Category>>builder()
				.data(categoryRepository.findAll())
				.status(Response.Status.OK)
				.build();
	}

	@GetMapping("/subcategories")
	public Response<Iterable<SubCategory>> subCategories(@RequestParam() String category) {
		return Response.<Iterable<SubCategory>>builder()
				.data(subCategoryRepository.findByCategoryName(category))
				.status(Response.Status.OK)
				.build();
	}

	@PostMapping(value="/add-note")
	public Response<String> addNote(@RequestBody lithium.client.changelog.objects.ChangeLog changeLog, LithiumTokenUtil util) throws Exception {
		try {
			changeLog.setAuthorFullName(util.firstName() + " " + util.lastName());
			service.addNote(changeLog, util) ;
			return Response.<String>builder().status(Response.Status.OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<String>builder().status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/update-note")
	public Response<lithium.client.changelog.objects.ChangeLog> updateChangeLogNotes(@RequestBody lithium.client.changelog.objects.ChangeLog  changeLog,
																					 LithiumTokenUtil util) {

		lithium.client.changelog.objects.ChangeLog updatedChangeLog = null;
		Category findCategory = categoryRepository.findByName(changeLog.getCategoryName());
		SubCategory findSubCategory = null;
		if(changeLog.getSubCategoryName() != null) {
			findSubCategory = subCategoryRepository.findByName(changeLog.getSubCategoryName());
		}

		if(findCategory != null) {
			updatedChangeLog = service.updateNote(changeLog.getId(),changeLog.getAuthorGuid(),findCategory , findSubCategory,
					changeLog.getPriority(),changeLog.getComments(), util);
		}

		if(updatedChangeLog != null)
			return Response.<lithium.client.changelog.objects.ChangeLog>builder().data(updatedChangeLog)
					.status(Response.Status.OK_SUCCESS).build();
		else
			return Response.<lithium.client.changelog.objects.ChangeLog>builder()
					.status(Response.Status.NOT_FOUND).build();
	}

	Page<ChangeLog> mapChangeLogAuthorFullName(Page<ChangeLog> table, Pageable pageable) {

		List<ChangeLog> changeLogs = table.getContent();
		List<String> guids = changeLogs.stream().filter(c -> c.getAuthorFullName() == null).map(c -> c.getAuthorUser().getGuid()).distinct().collect(Collectors.toList());
		if(guids.isEmpty()){
			return table;
		}
		Response<List<User>> response =  getClient().findByGuids( "livescore",guids);
		List<User> users = response.getData();
		List<ChangeLog> mappedList = changeLogs.stream().map(c -> {
			if(c.getAuthorFullName() == null){
				Optional<User> result =  users.stream().filter(u -> u.getGuid().equalsIgnoreCase(c.getAuthorUser().getGuid())|| u.getUsername().equalsIgnoreCase( c.getAuthorUser().getGuid())).findFirst();
				if(result.isPresent()) {
					User user = result.get();
					c.setAuthorFullName(String.format("%s %s", user.getFirstName(), user.getLastName()));
				}
				else {
					c.setAuthorFullName(User.SYSTEM_FULL_NAME);
				}
			}
			return c;
		}).collect(Collectors.toList());

		return new PageImpl<>(mappedList, pageable,table.getTotalElements());


	}

	UserClient getClient() {
		try {
			return lithiumServiceClientFactory.target(UserClient.class, true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}
}
