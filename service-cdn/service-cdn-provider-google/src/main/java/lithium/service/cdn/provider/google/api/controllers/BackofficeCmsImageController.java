package lithium.service.cdn.provider.google.api.controllers;


import lithium.service.Response;
import lithium.service.cdn.provider.google.service.asset.TemplateAssetService;
import lithium.service.cdn.provider.google.service.asset.model.BatchProcessingResult;
import lithium.service.cdn.provider.google.service.asset.model.StorageAsset;
import lithium.service.cdn.provider.google.service.storage.RequestInitializable;
import lithium.service.cdn.provider.google.service.storage.utils.CdnBucketType;
import lithium.service.cdn.provider.google.storage.objects.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("/backoffice/{domainName}/cms-image/{lang}")
@Slf4j
@RequiredArgsConstructor
public class BackofficeCmsImageController {
  private final RequestInitializable storageDetails;
  private final TemplateAssetService assetService;

  @PostMapping
  public Response<List<BatchProcessingResult>> upload(
      @PathVariable("domainName") String domainName,
      @PathVariable("lang") String lang,
      @RequestParam("file") List<MultipartFile> fileList) throws Exception {

    storageDetails.initialize(domainName);
    return Response.<List<BatchProcessingResult>>builder().data(assetService.upload(lang, fileList, CdnBucketType.CMS_IMAGE)).build();
  }

  @DeleteMapping
  public Response<BatchProcessingResult> delete(
      @PathVariable("domainName") String domainName,
      @PathVariable("lang") String lang,
      @RequestParam String fileName) throws Exception {

    storageDetails.initialize(domainName);
    return Response.<BatchProcessingResult>builder().data(assetService.delete(lang, fileName, CdnBucketType.CMS_IMAGE)).build();
  }

  @GetMapping
  public Response<List<StorageAsset>> list(
      @PathVariable("domainName") String domainName,
      @PathVariable("lang") String lang) throws Exception {

    storageDetails.initialize(domainName);
    return Response.<List<StorageAsset>>builder().data(assetService.list(lang, CdnBucketType.CMS_IMAGE)).build();
  }

  @ExceptionHandler({Exception.class})
  public Response<Template> exceptionHandler(Exception exception) {
    return Response.<Template>builder().message(exception.getMessage()).status(INTERNAL_SERVER_ERROR).build();
  }
}
