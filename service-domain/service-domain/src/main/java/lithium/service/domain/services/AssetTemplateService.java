package lithium.service.domain.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.data.entities.AssetTemplate;
import lithium.service.domain.data.entities.Domain;
import lithium.service.domain.data.repositories.DomainRepository;
import lithium.service.domain.data.repositories.AssetTemplateRepository;
import lithium.service.domain.data.specifications.AssetTemplateSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AssetTemplateService {

  @Autowired
  DomainRepository domainRepo;
  @Autowired
  ChangeLogService changeLogService;
  @Autowired
  AssetTemplateRepository assetTemplateRepo;

  public AssetTemplate add(String domainName, AssetTemplate assetTemplate, String authorGuid) throws Exception {
    Domain domain = domainRepo.findByName(domainName);

    if(domain == null)
      throw new Exception("Domain Does not exists");

    assetTemplate.setDomain(domain);

    assetTemplateRepo.save(assetTemplate);

    ChangeLogFieldChange c = ChangeLogFieldChange.builder()
        .field("domain")
        .fromValue("")
        .toValue(assetTemplate.getDomain().getName())
        .build();

    List<ChangeLogFieldChange> clfc = changeLogService.copy(assetTemplate, new AssetTemplate(),
        new String[] {  "name", "description",  });
    clfc.add(c);

    changeLogService.registerChangesWithDomain("image", "create", assetTemplate.getId(), authorGuid, null, null, clfc,  Category.SUPPORT, SubCategory.TEMPLATES, 0, domainName);
    return assetTemplate;
  }


  public DataTableResponse<AssetTemplate> table(String domainName, DataTableRequest request) {
    Specification<AssetTemplate> spec = Specification.where(AssetTemplateSpecification.isDeleted(false))
            .and(Specification.where(AssetTemplateSpecification.domainName(domainName)));

    if ((request.getSearchValue() != null) && (request.getSearchValue().length() > 0)) {
      Specification<AssetTemplate> s = Specification.where(AssetTemplateSpecification.any(request.getSearchValue()));
      spec = (spec == null)? s: spec.and(s);
    }

    return new DataTableResponse<>(
        request,
        assetTemplateRepo.findAll(spec, request.getPageRequest())
    );
  }

  public AssetTemplate findById(long id) {
    return assetTemplateRepo.findOne(id);
  }
  public AssetTemplate findByNameAndLangAndDomainName(String name, String lang, String domain) {
    return assetTemplateRepo.findOneByDomainNameAndNameAndLangAndDeletedFalse(domain, name,lang);
  }

  public AssetTemplate delete(long id, String authorGuid) throws Exception {
    AssetTemplate assetTemplate = findById(id);

    if(assetTemplate == null || assetTemplate.getDeleted())
      throw new Exception("Image does not exist");


    assetTemplate.setDeleted(true);
    assetTemplate.setName(Long.toString(System.currentTimeMillis())); // Change the name to a timestamp so we dont have unique conflicts

    assetTemplateRepo.save(assetTemplate);

    List<ChangeLogFieldChange> clfc = changeLogService.copy(assetTemplate, new AssetTemplate(),
        new String[] {  "name", "description", "deleted", "domain",  });

    changeLogService.registerChangesWithDomain("image", "delete", assetTemplate.getId(), authorGuid, null, null, clfc, Category.SUPPORT, SubCategory.TEMPLATES, 0, assetTemplate.getDomain().getName());

    return assetTemplate;
  }

  public Response<ChangeLogs> changeLogs(String domainName, Long id, int p) throws Exception {
    Domain domain = domainRepo.findByName(domainName);
    if (domain == null) throw new Exception("Domain does not exist");

    return changeLogService.list("image", id, p);
  }
}


