package lithium.service.cdn.provider.google.api.controllers;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.NOT_FOUND;
import static lithium.service.Response.Status.OK;

import lithium.service.Response;
import lithium.service.cdn.provider.google.service.storage.RequestInitializable;
import lithium.service.cdn.provider.google.service.template.TemplateService;
import lithium.service.cdn.provider.google.storage.objects.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
@RequestMapping("/backoffice/{domainName}/template")
@Slf4j
@RequiredArgsConstructor
public class BackofficeController {

  private final RequestInitializable storageDetails;
  private final TemplateService templateService;

  /**
   * @param domainName
   * @param name
   * @param template
   * @return
   */
  @PostMapping(value = "/{name}/{language}")
  public Response<Template> createOrUpdate(@PathVariable("domainName") String domainName, @PathVariable("name") String name, @PathVariable("language") String language,
      @RequestBody Template template) throws Exception {
    storageDetails.initialize(domainName);
    templateService.createOrUpdate(template, name, language);
    return Response.<Template>builder().message("Template of " + name + " for " + domainName + " published").status(OK).build();
  }

  /**
   * @param domainName
   * @param name
   * @return
   */
  @DeleteMapping(value = "/{name}/{language}")
  public Response<Template> delete(@PathVariable("domainName") String domainName, @PathVariable("name") String name, @PathVariable("language") String language)
      throws Exception {
    storageDetails.initialize(domainName);
    templateService.delete(name, language);
    return Response.<Template>builder().message("Template of " + name + " for " + domainName + " was un-published").status(OK).build();
  }

  @GetMapping(value = "/{name}/link/{language}")
  public Response<String> getLink(@PathVariable("domainName") String domainName, @PathVariable("name") String name, @PathVariable("language") String language)
      throws Exception {
    storageDetails.initialize(domainName);
    return templateService.getLink(name, language)
        .map(link -> Response.<String>builder().data(link).message("Template of " + name + " for " + domainName + " was found").status(OK).build())
        .orElse(Response.<String>builder().message("Template of " + name + " for " + domainName + " wasn't found").status(NOT_FOUND).build());
  }

  @ExceptionHandler({Exception.class})
  public Response<Template> exceptionHandler(Exception exception) {
    return Response.<Template>builder().message(exception.getMessage()).status(INTERNAL_SERVER_ERROR).build();
  }
}
