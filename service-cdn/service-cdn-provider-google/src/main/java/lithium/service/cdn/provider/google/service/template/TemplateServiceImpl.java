package lithium.service.cdn.provider.google.service.template;

import static lithium.service.cdn.provider.google.service.utils.PredicateUtils.not;

import com.google.cloud.storage.Blob;
import java.util.Optional;
import lithium.service.cdn.provider.google.builders.TemplateBuilder;
import lithium.service.cdn.provider.google.service.storage.StorageDetails;
import lithium.service.cdn.provider.google.service.storage.StorageFileManager;
import lithium.service.cdn.provider.google.service.storage.utils.CdnBucketType;
import lithium.service.cdn.provider.google.storage.objects.Template;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TemplateServiceImpl implements TemplateService {

  private final StorageDetails storageDetails;
  private final StorageFileManager storageFileManager;

  private static final String FILE_SUFFIX = ".html";
  public static final String CONTENT_TYPE_TEXT_HTML_UTF8 = "text/html; charset=utf-8";

  @Override
  public Blob createOrUpdate(Template template, String fileName, String language) {
    String content = Optional.of(template)
        .map(Template::getHead)
        .filter(not(String::isEmpty))
        .map(head -> TemplateBuilder.build(head, template.getContent()))
        .orElse(template.getContent());

    return storageFileManager.upload(getStoringPath(fileName, language), CONTENT_TYPE_TEXT_HTML_UTF8, content, String::getBytes);
  }

  @Override
  public void delete(String fileName) {
    storageFileManager.delete(getStoringPath(fileName));
  }

  @Override
  public void delete(String fileName, String language) {
    storageFileManager.delete(getStoringPath(fileName, language));
  }

  @Override
  public Optional<String> getLink(String fileName) {
    return storageFileManager.get(getStoringPath(fileName))
        .map(Blob::getName)
        .map(name -> storageDetails.getURI().concat(name));
  }

  @Override
  public Optional<String> getLink(String fileName, String language) {
    return storageFileManager.get(getStoringPath(fileName, language))
        .map(Blob::getName)
        .map(name -> storageDetails.getURI().concat(name));
  }

  private String getStoringPath(String fileName) {
    return storageDetails.getBucketPrefix() + fileName + FILE_SUFFIX;
  }


  private String getStoringPath(String fileName, String language) {
    String bucketPrefix = storageDetails.getBucketPrefix(language, CdnBucketType.TEMPLATE);
    return bucketPrefix + fileName + FILE_SUFFIX;
  }
}
