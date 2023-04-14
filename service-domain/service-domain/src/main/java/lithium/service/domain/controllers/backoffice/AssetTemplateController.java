package lithium.service.domain.controllers.backoffice;

import lithium.client.changelog.objects.ChangeLogs;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.data.entities.AssetTemplate;
import lithium.service.domain.services.AssetTemplateService;
import lithium.tokens.LithiumTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/backoffice/{domainName}/asset/templates")
public class AssetTemplateController {

  @Autowired
  AssetTemplateService assetTemplateService;

  @PostMapping
  public Response<AssetTemplate> create(@PathVariable("domainName") String domain, @RequestBody AssetTemplate assetTemplate, LithiumTokenUtil util)
      throws Exception {
    return Response.<AssetTemplate>builder().status(Status.OK).data(
        assetTemplateService.add(domain, assetTemplate, util.guid())
    ).build();
  }

  @GetMapping("/table")
  public DataTableResponse<AssetTemplate> table(@PathVariable("domainName") String domainName, DataTableRequest request) {
    return assetTemplateService.table(domainName, request);
  }

  @GetMapping("/find-by-name-and-lang-and-domainname")
  public Response<AssetTemplate> findByNameAndLangAndDomainName(@PathVariable("domainName") String domainName, @RequestParam("name") String name,
      @RequestParam("lang") String lang) {
    return Response.<AssetTemplate>builder()
        .status(Status.OK)
        .data(assetTemplateService.findByNameAndLangAndDomainName(name, lang, domainName))
        .build();
  }

  @GetMapping("/{id}")
  public Response<AssetTemplate> findById(@PathVariable("id") long id) {
    return Response.<AssetTemplate>builder()
        .status(Status.OK)
        .data(assetTemplateService.findById(id))
        .build();
  }

  @DeleteMapping("/{id}")
  public Response<AssetTemplate> delete(@PathVariable("id") long id, LithiumTokenUtil util) throws Exception {
    return Response.<AssetTemplate>builder()
        .status(Status.OK)
        .data(assetTemplateService.delete(id, util.guid()))
        .build();
  }

  @GetMapping("/{id}/changelogs")
  public Response<ChangeLogs> changeLogs(@PathVariable("domainName") String domainName, @PathVariable Long id, @RequestParam int p) throws Exception {
    return assetTemplateService.changeLogs(domainName, id, p);
  }
}
