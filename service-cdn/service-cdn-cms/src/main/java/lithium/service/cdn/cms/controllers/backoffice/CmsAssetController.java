package lithium.service.cdn.cms.controllers.backoffice;

import lithium.client.changelog.objects.ChangeLogs;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.cdn.cms.data.entities.CmsAsset;
import lithium.service.cdn.cms.data.objects.CmsAssetRequest;
import lithium.service.cdn.cms.data.objects.CmsAssetResponse;
import lithium.service.cdn.cms.services.CmsAssetService;
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
import java.util.List;

@RestController
@RequestMapping(value = "/backoffice/{domainName}/cms-assets")
public class CmsAssetController {

  @Autowired
  CmsAssetService casinoImageService;

  @PostMapping
  public Response<CmsAsset> create(@PathVariable("domainName") String domain, @RequestBody CmsAsset cmsAsset, LithiumTokenUtil util)
          throws Exception {
    return Response.<CmsAsset>builder().status(Status.OK).data(
        casinoImageService.add(domain, cmsAsset, util.guid())
    ).build();
  }

  @PostMapping("/table")
  public CmsAssetResponse table(@PathVariable("domainName") String domainName, @RequestBody CmsAssetRequest request) {
    return casinoImageService.table(domainName, request);
  }

  @GetMapping("/find-by-name-and-domain-and-type")
  public Response<CmsAsset> findByNameAndDomainAndType(@PathVariable("domainName") String domainName, @RequestParam ("name") String name, @RequestParam("type") String type)
  {
    return Response.<CmsAsset>builder().status(Status.OK).data(
          casinoImageService.findByNameAndDomainAndType(name, domainName, type)
        ).build();
  }

  @GetMapping("/{id}")
  public Response<CmsAsset> findById(@PathVariable("id") long id) {
    return Response.<CmsAsset>builder()
        .status(Status.OK)
        .data(casinoImageService.findById(id))
        .build();
  }

  @DeleteMapping("/{id}")
  public Response<CmsAsset> delete(@PathVariable("id") long id, LithiumTokenUtil util) throws Exception {
    return Response.<CmsAsset>builder()
        .status(Status.OK)
        .data(casinoImageService.delete(id, util.guid()))
        .build();
  }

  @GetMapping("/find-all-by-domain-name-and-type/{type}")
  public Response<List<CmsAsset>> findByDomainNameAndType(@PathVariable("domainName") String domainName,
                                                          @PathVariable("type") String type)
  {
    return Response.<List<CmsAsset>>builder().status(Status.OK).data(
            casinoImageService.findByDomainNameAndType(domainName, type)
    ).build();
  }

  @GetMapping("/{id}/changelogs")
  public Response<ChangeLogs> changeLogs(@PathVariable("domainName") String domainName, @PathVariable Long id, @RequestParam int p) throws Exception {
    return casinoImageService.changeLogs(domainName, id, p);
  }
}
