package lithium.service.cdn.provider.google.service.template;

import com.google.cloud.storage.Blob;
import lithium.service.cdn.provider.google.storage.objects.Template;
import java.util.Optional;

public interface TemplateService {

  Blob createOrUpdate(Template template, String fileName, String language);

  void delete(String fileName);

  void delete(String fileName, String language);

  Optional<String> getLink(String fileName);

  Optional<String> getLink(String fileName, String language);

}
