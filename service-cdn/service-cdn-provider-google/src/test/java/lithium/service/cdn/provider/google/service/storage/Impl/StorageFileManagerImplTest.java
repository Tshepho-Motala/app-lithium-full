package lithium.service.cdn.provider.google.service.storage.Impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import java.util.Optional;
import lithium.service.cdn.provider.google.service.storage.StorageProvider;
import lithium.service.cdn.provider.google.service.storage.exception.GoogleStoreException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StorageFileManagerImplTest {

  private static final String BUCKET_NAME = "bucket-name";
  private static final String CACHE_CONTROL = "no-cache, max-age=900";
  private static final String STORING_PATH = "bucket-prefix/file.html";
  private static final String CONTENT_TYPE = "text/html; charset=utf-8";

  @InjectMocks
  private StorageFileManagerImpl storageFileManager;
  @Mock
  StorageProvider storageProvider;
  @Mock
  Storage storage;
  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  @Before
  public void setup() {
    given(storageProvider.getStorage()).willReturn(storage);
    given(storageProvider.getBucket()).willReturn(BUCKET_NAME);
    given(storageProvider.getCacheControl()).willReturn(CACHE_CONTROL);
  }

  @Test
  public void testDelete() {
    storageFileManager.delete(STORING_PATH);
    verify(storage).delete(eq(BlobId.of(BUCKET_NAME, STORING_PATH)));
  }

  @Test
  public void shouldReturnIfFound() {
    Blob blob = Mockito.mock(Blob.class);
    given(storage.get(eq(BUCKET_NAME), eq(STORING_PATH))).willReturn(blob);
    assertThat(storageFileManager.get(STORING_PATH), is(Optional.of(blob)));
  }

  @Test
  public void shouldReturnEmptyIfNotFound() {
    given(storage.get(eq(BUCKET_NAME), eq(STORING_PATH))).willReturn(null);
    assertThat(storageFileManager.get(STORING_PATH), is(Optional.empty()));
  }

  @Test
  public void uploadShouldThrowSpecificException() {
    exceptionRule.expect(GoogleStoreException.class);
    exceptionRule.expectMessage("Failed upload file to storage");
    given(storage.create(any(BlobInfo.class), any(byte[].class))).willThrow(new StorageException(504, "testErrorMessage"));
    storageFileManager.upload(STORING_PATH, CONTENT_TYPE, "contentSource", String::getBytes);
  }

  @Test
  public void uploadFile() {
    storageFileManager.upload(STORING_PATH, CONTENT_TYPE, "contentSource", String::getBytes);
    verify(storage).create(any(BlobInfo.class), any(byte[].class));
  }
}
