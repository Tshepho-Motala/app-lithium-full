package lithium.service.pushmsg.controllers;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import lithium.client.changelog.Category;
import lithium.client.changelog.SubCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.service.Response;
import lithium.service.pushmsg.data.entities.PushMsgContent;
import lithium.service.pushmsg.data.entities.PushMsgHeading;
import lithium.service.pushmsg.data.entities.PushMsgTemplate;
import lithium.service.pushmsg.data.entities.PushMsgTemplateRevision;
import lithium.service.pushmsg.data.entities.User;
import lithium.service.pushmsg.data.repositories.PushMsgTemplateRepository;
import lithium.service.pushmsg.data.repositories.PushMsgTemplateRevisionRepository;
import lithium.service.pushmsg.services.UserService;

@RestController
@RequestMapping("/pushmsgtemplate/{id}")
public class PushMsgTemplateController {
	@Autowired UserService userService;
	@Autowired PushMsgTemplateRepository repository;
	@Autowired PushMsgTemplateRevisionRepository revisionRepository;
	@Autowired ChangeLogService changeLogService;
	
	@GetMapping
	public Response<PushMsgTemplate> get(@PathVariable("id") PushMsgTemplate t) {
		return Response.<PushMsgTemplate>builder().data(t).build();
	}
	
	@PostMapping
	public Response<PushMsgTemplate> save(@RequestBody PushMsgTemplate t, Principal principal) throws Exception {
		PushMsgTemplate current = repository.findOne(t.getId());
		PushMsgTemplateRevision currentRevision = revisionRepository.findOne(t.getCurrent().getId());
		List<ChangeLogFieldChange> clfc = changeLogService.copy(t.getEdit(), currentRevision, new String[] { "description", "providerTemplateId" });
		if (current.getEnabled() != t.getEnabled()) {
			ChangeLogFieldChange fieldEnabledChange = 
				ChangeLogFieldChange.builder()
					.field("enabled")
					.fromValue(current.getEnabled().toString())
					.toValue(t.getEnabled().toString())
					.build();
			clfc.add(fieldEnabledChange);
		}
		t.getEdit().setPushMsgTemplate(t);
		revisionRepository.save(t.getEdit());
		t.setCurrent(t.getEdit());
		t.setEdit(null); t.setEditBy(null); t.setEditStartedOn(null);
		t = repository.save(t);
		changeLogService.registerChangesWithDomain("pushmsgtemplate", "edit", t.getId(), principal.getName(), null, null, clfc, Category.SUPPORT, SubCategory.COMMUNICATIONS, 0, current.getDomain().getName());
		return Response.<PushMsgTemplate>builder().data(t).build();
	}
	
	@PostMapping("/continueLater")
	public Response<PushMsgTemplate> continueLater(@RequestBody PushMsgTemplate t, Principal principal) {
		PushMsgTemplate current = repository.findOne(t.getId());
		t.getEdit().setPushMsgTemplate(t);
		PushMsgTemplateRevision edit = revisionRepository.save(t.getEdit());
		current.setEdit(edit);
		current.setEditStartedOn(new Date());
		current.setEditBy(userService.findOrCreate(principal.getName()));
		current = repository.save(current);
		return Response.<PushMsgTemplate>builder().data(current).build();
	}
	
	@PostMapping("/cancelEdit")
	public Response<PushMsgTemplate> cancelEdit(@RequestBody PushMsgTemplate t, Principal principal) {
		PushMsgTemplate current = repository.findOne(t.getId());
		current.setEdit(null);
		current.setEditBy(null);
		current.setEditStartedOn(null);
		current = repository.save(current);
		return Response.<PushMsgTemplate>builder().data(current).build();
	}
	
	@GetMapping("/edit")
	@Transactional
	public Response<PushMsgTemplate> edit(
		@PathVariable("id") PushMsgTemplate t,
		Principal p
	) {
		if (t.getEdit() == null) {
			User user = userService.findOrCreate(p.getName());
			PushMsgTemplateRevision revision = t.getCurrent().toBuilder().id(null).build();
			List<PushMsgContent> contents = revision.getPushMsgContents().stream().collect(Collectors.toList());
			contents.stream().forEach(c -> { c.setId(null); });
			revision.setPushMsgContents(contents);
			List<PushMsgHeading> headings = revision.getPushMsgHeadings().stream().collect(Collectors.toList());
			headings.stream().forEach(h -> { h.setId(null); });
			revision.setPushMsgHeadings(headings);
			
//			PushMsgTemplateRevision revision = PushMsgTemplateRevision.builder()
//				.pushMsgTemplate(t)
//				.description(t.getCurrent().getDescription())
//				.providerTemplateId(t.getCurrent().getProviderTemplateId())
//				.pushMsgHeadings(t.getCurrent().getPushMsgHeadings())
//				.pushMsgContents(t.getCurrent().getPushMsgContents())
//				.build();
			revision = revisionRepository.save(revision);
			t.setEdit(revision);
			t.setEditBy(user);
			t.setEditStartedOn(new Date());
			t = repository.save(t);
		}
		return Response.<PushMsgTemplate>builder().data(t).build();
	}
	
	@GetMapping(value = "/changelogs")
	public @ResponseBody Response<ChangeLogs> changeLogs(@PathVariable Long id, @RequestParam int p) throws Exception {
		return changeLogService.list("pushmsgtemplate", id, p);
	}
}
