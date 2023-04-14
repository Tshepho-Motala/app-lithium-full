package lithium.service.sms.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import javax.transaction.Transactional;

import lithium.client.changelog.Category;
import lithium.client.changelog.SubCategory;
import lithium.service.sms.services.SMSService;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.DomainValidationUtil;
import lombok.extern.slf4j.Slf4j;
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
import lithium.service.sms.data.entities.SMSTemplate;
import lithium.service.sms.data.entities.SMSTemplateRevision;
import lithium.service.sms.data.entities.User;
import lithium.service.sms.data.repositories.SMSTemplateRepository;
import lithium.service.sms.data.repositories.SMSTemplateRevisionRepository;
import lithium.service.sms.services.UserService;

@Slf4j
@RestController
@RequestMapping("/smstemplate/{id}")
public class SMSTemplateController {
	@Autowired SMSService smsService;
	@Autowired UserService userService;
	@Autowired SMSTemplateRepository repository;
	@Autowired SMSTemplateRevisionRepository revisionRepository;
	@Autowired ChangeLogService changeLogService;
	
	@GetMapping
	public Response<SMSTemplate> get(@PathVariable("id") SMSTemplate t, LithiumTokenUtil tokenUtil) {
		try {
			DomainValidationUtil.validate(t.getDomain().getName(), "SMS_TEMPLATES_VIEW", tokenUtil);
			return Response.<SMSTemplate>builder().data(t).build();
		} catch (Exception e) {
			return Response.<SMSTemplate>builder().status(Response.Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}

	@PostMapping("/test/{recipientMobile:.+}")
	public Response<Boolean> testSMSTemplate(
		@PathVariable("id") SMSTemplate template,
		@PathVariable("recipientMobile") String recipientMobile,
		LithiumTokenUtil token
	) throws Exception {
		try {
			DomainValidationUtil.validate(template.getDomain().getName(), "SMS_TEMPLATES_VIEW", token);
			smsService.save(
				true,
				template.getDomain().getName(),
				template.getName(),
				template.getLang(),
				recipientMobile,
				1,
				token.guid(),
				new HashSet<>()
			);
			return Response.<Boolean>builder()
				.data(true)
				.status(Response.Status.OK)
				.build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Boolean>builder()
				.data(false)
				.status(Response.Status.INTERNAL_SERVER_ERROR)
				.message(e.getMessage())
				.build();
		}
	}
	
	@PostMapping
	public Response<SMSTemplate> save(@RequestBody SMSTemplate t, LithiumTokenUtil tokenUtil) throws Exception {
		try {
			SMSTemplate current = repository.findOne(t.getId());
			DomainValidationUtil.validate(current.getDomain().getName(), "SMS_TEMPLATES_VIEW", tokenUtil);
			SMSTemplateRevision currentRevision = revisionRepository.findOne(t.getCurrent().getId());
			List<ChangeLogFieldChange> clfc = changeLogService.copy(t.getEdit(), currentRevision, new String[]{"description", "text"});
			if (current.getEnabled() != t.getEnabled()) {
				ChangeLogFieldChange fieldEnabledChange =
						ChangeLogFieldChange.builder()
								.field("enabled")
								.fromValue(current.getEnabled().toString())
								.toValue(t.getEnabled().toString())
								.build();
				clfc.add(fieldEnabledChange);
			}
			t.getEdit().setSmsTemplate(t);
			revisionRepository.save(t.getEdit());
			t.setCurrent(t.getEdit());
			t.setEdit(null);
			t.setEditBy(null);
			t.setEditStartedOn(null);
            t.setUpdatedOn(new Date());
			t = repository.save(t);
			changeLogService.registerChangesWithDomain("smstemplate", "edit", t.getId(), tokenUtil.guid(), null, null, clfc, Category.SUPPORT, SubCategory.COMMUNICATIONS, 0, current.getDomain().getName());
			return Response.<SMSTemplate>builder().data(t).status(Response.Status.OK).build();
		} catch (Exception e) {
			return Response.<SMSTemplate>builder().status(Response.Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@PostMapping("/continueLater")
	public Response<SMSTemplate> continueLater(@RequestBody SMSTemplate t, LithiumTokenUtil tokenUtil) {
		try {
			SMSTemplate current = repository.findOne(t.getId());
			DomainValidationUtil.validate(current.getDomain().getName(), "SMS_TEMPLATES_VIEW", tokenUtil);
			t.getEdit().setSmsTemplate(t);
			SMSTemplateRevision edit = revisionRepository.save(t.getEdit());
			current.setEdit(edit);
			current.setEditStartedOn(new Date());
			current.setEditBy(userService.findOrCreate(tokenUtil.guid()));
			current = repository.save(current);
			return Response.<SMSTemplate>builder().data(current).build();
		} catch (Exception e) {
			return Response.<SMSTemplate>builder().status(Response.Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@PostMapping("/cancelEdit")
	public Response<SMSTemplate> cancelEdit(@RequestBody SMSTemplate t, LithiumTokenUtil tokenUtil) {
		try {
			SMSTemplate current = repository.findOne(t.getId());
			DomainValidationUtil.validate(current.getDomain().getName(), "SMS_TEMPLATES_VIEW", tokenUtil);
			current.setEdit(null);
			current.setEditBy(null);
			current.setEditStartedOn(null);
			current = repository.save(current);
			return Response.<SMSTemplate>builder().data(current).build();
		} catch (Exception e) {
			return Response.<SMSTemplate>builder().status(Response.Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@GetMapping("/edit")
	@Transactional
	public Response<SMSTemplate> edit(@PathVariable("id") SMSTemplate t, LithiumTokenUtil tokenUtil) {
		try {
			DomainValidationUtil.validate(t.getDomain().getName(), "SMS_TEMPLATES_VIEW", tokenUtil);
			if (t.getEdit() == null) {
				User user = userService.findOrCreate(tokenUtil.guid());
				SMSTemplateRevision revision = SMSTemplateRevision.builder()
						.smsTemplate(t)
						.description(t.getCurrent().getDescription())
						.text(t.getCurrent().getText())
						.build();
				revisionRepository.save(revision);
				t.setEdit(revision);
				t.setEditBy(user);
				t.setEditStartedOn(new Date());
				t = repository.save(t);
			}
			return Response.<SMSTemplate>builder().data(t).build();
		} catch (Exception e) {
			return Response.<SMSTemplate>builder().status(Response.Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@GetMapping(value = "/changelogs")
	public @ResponseBody Response<ChangeLogs> changeLogs(@PathVariable("id") SMSTemplate t, @RequestParam int p, LithiumTokenUtil tokenUtil) throws Exception {
		DomainValidationUtil.validate(t.getDomain().getName(), "SMS_TEMPLATES_VIEW", tokenUtil);
		return changeLogService.list("smstemplate", t.getId(), p);
	}
}
