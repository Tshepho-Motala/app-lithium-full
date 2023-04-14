package lithium.service.access.controllers;

import static lithium.service.Response.Status.CONFLICT;
import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.NOT_FOUND;
import static lithium.service.Response.Status.OK;

import java.security.Principal;
import java.util.ArrayList;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.access.data.entities.List;
import lithium.service.access.data.entities.Value;
import lithium.service.access.data.repositories.ListRepository;
import lithium.service.access.data.repositories.ValueRepository;
import lithium.service.access.data.specifications.ValueSpecification;
import lithium.service.access.services.ListService;
import lithium.service.access.services.ListValueService;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.DomainValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/list/{id}")
public class ListController {
	@Autowired ListRepository listRepository;
	@Autowired ValueRepository valueRepository;
	@Autowired ListValueService listValueService;
	@Autowired ChangeLogService changeLogService;
  @Autowired ListService listService;
	@Autowired TokenStore tokenStore;

	@GetMapping
	public Response<List> get(@PathVariable("id") Long id, LithiumTokenUtil tokenUtil) {
		try {
      List list = listRepository.findOne(id);
      if (list == null) {
        return Response.<List>builder().status(NOT_FOUND).build();
      }
      DomainValidationUtil.validate(list.getDomain().getName(), tokenUtil, "ACCESSCONTROL_VIEW", "ACCESSCONTROL_EDIT", "ACCESSCONTROL_ADD",
        "ACCESSRULES_VIEW", "ACCESSRULES_EDIT", "ACCESSRULES_ADD");
      return Response.<List>builder().data(list).status(OK).build();
    } catch (Exception e) {
      return Response.<List>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
    }
	}

	@GetMapping(value="/values/table")
	public DataTableResponse<Value> table(
		@PathVariable("id") List list,
		DataTableRequest request,
    LithiumTokenUtil tokenUtil
	) throws Status500InternalServerErrorException {
    DomainValidationUtil.validate(list.getDomain().getName(), tokenUtil, "ACCESSCONTROL_VIEW", "ACCESSCONTROL_EDIT", "ACCESSCONTROL_ADD",
        "ACCESSRULES_VIEW", "ACCESSRULES_EDIT", "ACCESSRULES_ADD");
		Page<Value> lists = valueRepository.findAll(ValueSpecification.findByListId(list.getId(), request.getSearchValue()), request.getPageRequest());
		return new DataTableResponse<>(request, lists);
	}

	@PostMapping("/addvalue")
	public Response<List> addListValue(@PathVariable("id") List list, @RequestBody String data, LithiumTokenUtil tokenUtil) throws Exception {
		try {
		  DomainValidationUtil.validate(list.getDomain().getName(), tokenUtil, "ACCESSCONTROL_ADD", "ACCESSCONTROL_EDIT");
      return listService.addListValue(list, data, tokenUtil);
    } catch (Exception e) {
      return Response.<List>builder().status(INTERNAL_SERVER_ERROR).build();
    }
	}

	@PostMapping("/removevalue")
	public Response<List> removeListValue(@PathVariable("id") List list, @RequestBody Long valueId, Principal principal) throws Exception {
		try {
		  LithiumTokenUtil tokenUtil = LithiumTokenUtil.builder(tokenStore, principal).build();
      DomainValidationUtil.validate(list.getDomain().getName(), tokenUtil, "ACCESSCONTROL_ADD", "ACCESSCONTROL_EDIT");
      return listService.removeListValue(list, valueId, tokenUtil);
    } catch (Exception e) {
      return Response.<List>builder().status(INTERNAL_SERVER_ERROR).build();
    }
	}

	@PostMapping("/toggleEnable")
	public Response<List> enable(@PathVariable("id") List list, LithiumTokenUtil tokenUtil) throws Exception {
		try {
      DomainValidationUtil.validate(list.getDomain().getName(), tokenUtil, "ACCESSCONTROL_ADD", "ACCESSCONTROL_EDIT");
      boolean enable = !list.isEnabled();
      list.setEnabled(enable);
      list = listRepository.save(list);
      java.util.List<ChangeLogFieldChange> clfc = new ArrayList<ChangeLogFieldChange>();
      ChangeLogFieldChange c = ChangeLogFieldChange.builder()
          .field("enabled")
          .fromValue(String.valueOf(!enable))
          .toValue(String.valueOf(enable))
          .build();
      clfc.add(c);
      changeLogService.registerChangesWithDomain("list", "edit", list.getId(), tokenUtil.guid(), null, null, clfc, Category.ACCESS,
          SubCategory.ACCESS_RULE, 0, list.getDomain().getName());
      return Response.<List>builder().data(list).status(OK).build();
    } catch (Exception e) {
      return Response.<List>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
    }
	}

	@GetMapping(value = "/changelogs")
	private @ResponseBody Response<ChangeLogs> changeLogs(@PathVariable("id") List list, @RequestParam int p, LithiumTokenUtil tokenUtil) throws Exception {
    DomainValidationUtil.validate(list.getDomain().getName(), tokenUtil, "ACCESSCONTROL_VIEW", "ACCESSCONTROL_EDIT", "ACCESSCONTROL_ADD",
        "ACCESSRULES_VIEW", "ACCESSRULES_EDIT", "ACCESSRULES_ADD");
	  return changeLogService.listLimited(ChangeLogRequest.builder()
				.entityRecordId(list.getId())
				.entities(new String[] { "list", "list.value" })
				.page(p)
				.build()
			);
	}
}
