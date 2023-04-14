package lithium.service.cdn.provider.google.service.template;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.google.cloud.storage.Blob;
import lithium.service.cdn.provider.google.service.storage.StorageDetails;
import lithium.service.cdn.provider.google.service.storage.StorageFileManager;
import lithium.service.cdn.provider.google.service.storage.utils.CdnBucketType;
import lithium.service.cdn.provider.google.storage.objects.Template;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class TemplateServiceImplTest {

  private static final String HTML_CONTENT_TYPE = "text/html; charset=utf-8";
  private static final String FILENAME = "file";
  private static final String BUCKET_PREFIX = "bucketPrefix/";
  private static final String PATH = BUCKET_PREFIX + FILENAME + ".html";
  private final static String LANGUAGE = "en";

  @InjectMocks
  private TemplateServiceImpl templateService;
  @Mock
  private StorageDetails storageDetails;
  @Mock
  private StorageFileManager storageFileManager;

  @Before
  public void setup() {
    given(storageDetails.getBucketPrefix()).willReturn(BUCKET_PREFIX);
    given(storageDetails.getBucketPrefix(LANGUAGE, CdnBucketType.TEMPLATE)).willReturn(BUCKET_PREFIX);
  }

  @Test
  public void shouldUploadTemplateWithoutHead() {
    String content = "<p>content</p>";
    Template template = getTemplate(content, null);

    templateService.createOrUpdate(template, FILENAME, LANGUAGE);
    verify(storageFileManager).upload(eq(PATH), eq(HTML_CONTENT_TYPE), eq(content), any());
  }

  @Test
  public void shouldAddHeadToContent() {
    String content = "<p>content</p>";
    String head = "HEAD";
    Template template = getTemplate(content, head);
    String expectedContent = String.format("<html><head>%s</head><body>%s</body></html>", head, content);

    templateService.createOrUpdate(template, FILENAME, LANGUAGE);
    verify(storageFileManager).upload(eq(PATH), eq(HTML_CONTENT_TYPE), eq(expectedContent), any());
  }

  @Test
  public void testDelete() {
    templateService.delete(FILENAME);
    verify(storageFileManager).delete(eq(PATH));
  }

  @Test
  public void shouldReturnFileNameIfFound() {
    String cdnUri = "https://cdn.net";
    Blob blob = Mockito.mock(Blob.class);

    given(blob.getName()).willReturn(PATH);
    given(storageFileManager.get(eq(PATH))).willReturn(Optional.of(blob));
    given(storageDetails.getURI()).willReturn(cdnUri);

    assertThat(templateService.getLink(FILENAME), is(Optional.of(cdnUri+PATH)));
  }

  @Test
  public void shouldReturnEmptyOptionalIfNotFound() {
    given(storageFileManager.get(eq(PATH))).willReturn(Optional.empty());
    assertThat(templateService.getLink(FILENAME), is(Optional.empty()));
  }

  private Template getTemplate(String content, String head) {
    Template template = new Template();
    template.setContent(content);
    template.setHead(head);
    return template;
  }

}
