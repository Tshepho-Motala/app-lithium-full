package lithium.service.domain.controllers;

import lithium.client.changelog.objects.ChangeLogs;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.domain.data.entities.Template;
import lithium.service.domain.services.TemplateService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/{domainName}/template/{id}")
public class TemplateController {
	@Autowired TemplateService service;
	
	@GetMapping
	public Response<Template> get(@PathVariable("domainName") String domainName, @PathVariable("id") Template t) {
	  if(t.getDeleted()) {
      return Response.<Template>builder().status(Status.NOT_FOUND).build();
    }
		return Response.<Template>builder().status(Status.OK).data(t).build();
	}

  @DeleteMapping
  public Response delete (@PathVariable("domainName") String domainName, @PathVariable("id") Template t, LithiumTokenUtil tokenUtil) {
    try {
      service.delete(domainName, t, tokenUtil);
      return Response.builder().build();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return Response.<Template>builder().data(null).status(Status.INTERNAL_SERVER_ERROR).build();
    }
  }


  @GetMapping("/edit")
	public Response<Template> edit(@PathVariable("domainName") String domainName, @PathVariable("id") Template t, LithiumTokenUtil tokenUtil) {
		Template template = null;
		try {
      template = service.edit(domainName, t, tokenUtil);
      return Response.<Template>builder().data(template).status(Status.OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Template>builder().data(template).status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping
	public Response<Template> save(@PathVariable("domainName") String domainName, @RequestBody Template t, LithiumTokenUtil tokenUtil) {
		Template template = null;
		try {
      template = service.save(domainName, t, tokenUtil);
			return Response.<Template>builder().data(template).status(Status.OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Template>builder().data(template).status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping("/continueLater")
	public Response<Template> continueLater(@PathVariable("domainName") String domainName, @RequestBody Template t, LithiumTokenUtil tokenUtil) {
		Template template = null;
		try {
			template = service.continueLater(domainName, t, tokenUtil.guid());
			return Response.<Template>builder().data(template).status(Status.OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Template>builder().data(template).status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping("/cancelEdit")
	public Response<Template> cancelEdit(@PathVariable("domainName") String domainName, @RequestBody Template t, LithiumTokenUtil tokenUtil) {
		Template template = null;
		try {
			template = service.cancelEdit(domainName, t);
			return Response.<Template>builder().data(template).status(Status.OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Template>builder().data(template).status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GetMapping(value="/changelogs")
	public @ResponseBody Response<ChangeLogs> changeLogs(@PathVariable("domainName") String domainName, @PathVariable Long id, @RequestParam int p) throws Exception {
		return service.changeLogs(domainName, id, p);
	}
}
