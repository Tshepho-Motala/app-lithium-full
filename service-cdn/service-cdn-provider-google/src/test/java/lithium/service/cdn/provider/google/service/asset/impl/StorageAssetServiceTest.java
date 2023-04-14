package lithium.service.cdn.provider.google.service.asset.impl;

import lithium.service.cdn.provider.google.service.asset.Impl.StorageAssetService;
import lithium.service.cdn.provider.google.service.asset.Impl.TemplateAssetServiceImpl;
import lithium.service.cdn.provider.google.service.storage.StorageDetails;
import lithium.service.cdn.provider.google.service.storage.StorageFileManager;
import lithium.service.cdn.provider.google.service.storage.exception.AssetValidationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StorageAssetServiceTest {

  private static final String FILENAME = "file";
  private static final String BUCKET_PREFIX = "bucketPrefix/";
  private static final String PATH = BUCKET_PREFIX + FILENAME + ".jpeg";

  @Mock
  private StorageFileManager storageFileManager;

  @InjectMocks
  StorageAssetService storageAssetService;

  @Test
  public void shouldDeleteAsset() {
    storageAssetService.delete(FILENAME + ".jpeg" , BUCKET_PREFIX);
    verify(storageFileManager).delete(PATH);
  }
}
