package lithium.service.cdn.cms.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.exceptions.Status400BadRequestException;
import lithium.service.Response;
import lithium.service.cdn.client.exceptions.Status406DomainNotFoundException;
import lithium.service.cdn.client.exceptions.Status415InvalidFileUploadException;
import lithium.service.cdn.cms.data.entities.CmsAsset;
import lithium.service.cdn.cms.data.entities.Domain;
import lithium.service.cdn.cms.data.enums.FileTypes;
import lithium.service.cdn.cms.data.enums.ImageExtensions;
import lithium.service.cdn.cms.data.objects.CmsAssetRequest;
import lithium.service.cdn.cms.data.objects.CmsAssetResponse;
import lithium.service.cdn.cms.data.repositories.CmsAssetRepository;
import lithium.service.cdn.cms.data.repositories.DomainRepository;
import lithium.service.cdn.cms.data.specifications.CmsAssetSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CmsAssetService {

  private final int MAX_PAGE_SIZE = 100;

  @Autowired
  DomainRepository domainRepo;
  @Autowired
  ChangeLogService changeLogService;
  @Autowired
  CmsAssetRepository cmsAssetRepo;

  public CmsAsset add(String domainName, CmsAsset cmsAsset, String authorGuid) throws Exception {
    Domain domain = domainRepo.findByName(domainName);

    if(domain == null)
      throw new Status406DomainNotFoundException("Domain does not exist");

    validateFile(cmsAsset);

    cmsAsset.setDomain(domain);

    cmsAssetRepo.save(cmsAsset);

    List<ChangeLogFieldChange> clfc = changeLogService.copy(cmsAsset, new CmsAsset(), new String[] {  "name", "url","type","size", "uploadedDate", "domain"  });

    changeLogService.registerChangesWithDomain("cmsAsset", "create", cmsAsset.getId(), authorGuid, null, null, clfc,  Category.SUPPORT, SubCategory.TEMPLATES, 0, domainName);

    return cmsAsset;
  }

  private void validateFile(CmsAsset cmsAsset) throws Status415InvalidFileUploadException {

    if (cmsAsset == null || cmsAsset.getType() == null || cmsAsset.getType().trim().isEmpty() || cmsAsset.getUrl() == null ||
            cmsAsset.getUrl().trim().isEmpty()) {
      throw new Status415InvalidFileUploadException("Invalid file upload request.");
    }

    if (cmsAsset.getType().equalsIgnoreCase(FileTypes.BANNER.name()) || cmsAsset.getType().equalsIgnoreCase(FileTypes.TILE.name())) {
        if(!cmsAsset.getUrl().contains(".")) {
          throw new Status415InvalidFileUploadException("Invalid file upload request.");
        }
        String extension = cmsAsset.getUrl().substring(cmsAsset.getUrl().lastIndexOf(".") + 1);
        if (!checkIfImageExtensionIsValid(extension)) {
            throw new Status415InvalidFileUploadException("Invalid file upload request.");
        }
    }
  }

  private boolean checkIfImageExtensionIsValid(String extension) {
    for (ImageExtensions imageExtension : ImageExtensions.values()) {
        if (extension.equalsIgnoreCase(imageExtension.name())) {
          return true;
        }
    }
    return false;
  }

  public CmsAssetResponse table(String domainName, CmsAssetRequest request) {
    if (request.getSize() > MAX_PAGE_SIZE) {
      throw new Status400BadRequestException("Page size requested exceeds the maximum allowed page size");
    }

    Specification<CmsAsset> spec = Specification.where(CmsAssetSpecification.isDeleted(false))
        .and(Specification.where(CmsAssetSpecification.domainName(domainName)));

    if(request.getData().containsKey("type")) {
      spec = spec.and(CmsAssetSpecification.type(request.getData().get("type")));
    }

    PageRequest pageRequest = PageRequest.of(request.getPage() - 1, request.getSize());

    if(request.getSortBy() != null) {
      Sort sort = Sort.by(request.getSortOrder().equalsIgnoreCase("desc") ?Direction.DESC : Direction.ASC,request.getSortBy());
      pageRequest = PageRequest.of(request.getPage() - 1, request.getSize(), sort);
    }

    Page<CmsAsset> cmsAssets =  cmsAssetRepo.findAll(spec, pageRequest);

    return CmsAssetResponse.builder()
        .data(cmsAssets.getContent())
        .currentPage(request.getPage())
        .totalItems(cmsAssets.getTotalElements())
        .totalPages(cmsAssets.getTotalPages())
        .build();
  }

  public CmsAsset findById(long id) {
    return cmsAssetRepo.findById(id).orElse(null);
  }

  public CmsAsset delete(long id, String authorGuid) throws Exception {
    CmsAsset cmsAsset = findById(id);

    if(cmsAsset == null || cmsAsset.getDeleted())
      throw new Exception("CMS Asset does not exist");


    cmsAsset.setDeleted(true);
    cmsAsset.setName(Long.toString(System.currentTimeMillis()));

    cmsAssetRepo.save(cmsAsset);

    List<ChangeLogFieldChange> clfc = changeLogService.copy(cmsAsset, new CmsAsset(),
        new String[] {  "name", "url","type","size", "uploadedDate", "domain"  });

    changeLogService.registerChangesWithDomain("cmsAsset", "delete", cmsAsset.getId(), authorGuid, null, null, clfc, Category.SUPPORT, SubCategory.TEMPLATES, 0, cmsAsset.getDomain().getName());

    return cmsAsset;
  }

  public CmsAsset findByNameAndDomainAndType(String name, String domain, String type) {
    return cmsAssetRepo.findFirstByNameAndDomainNameAndType(name, domain, type);
  }

  public List<CmsAsset> findByDomainNameAndType(String domainName, String type) {
    List<CmsAsset> cmsAssetList = cmsAssetRepo.findByDomainNameAndTypeAndDeletedFalse(domainName, type);
    return cmsAssetList;
  }

  public Response<ChangeLogs> changeLogs(String domainName, Long id, int p) throws Exception {
    Domain domain = domainRepo.findByName(domainName);
    if (domain == null) throw new Exception("Domain does not exist");

    return changeLogService.list("cmsAsset", id, p);
  }
}
