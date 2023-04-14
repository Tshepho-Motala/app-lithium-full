package lithium.service.cdn.provider.google.service.storage.Impl;

import static lithium.service.cdn.provider.google.service.storage.utils.BucketPrefixProcessor.INVALID_BUCKET_PREFIX;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import lithium.modules.ModuleInfo;
import lithium.service.cdn.provider.google.builders.StorageSupplier;
import lithium.service.cdn.provider.google.config.ProviderConfig;
import lithium.service.cdn.provider.google.config.ProviderConfigService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StorageDetailsImplTest {

  @InjectMocks
  private StorageDetailsImpl storageDetails;
  @Mock
  private ProviderConfigService providerConfigService;
  @Mock
  private ModuleInfo moduleInfo;
  @Mock
  private StorageSupplier storageSupplier;

  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  @Before
  public void initialize() throws Exception {
    init(getConfig());
  }

  @Test
  public void shouldInstantiateStorage() throws Exception {
    verify(storageSupplier).get(eq(getConfig()));
  }


  @Test
  public void shouldReturnStorageDetails() {
    ProviderConfig config = getConfig();
    assertThat(storageDetails.getBucket(), is(config.getBucket()));
    assertThat(storageDetails.getCacheControl(), is("no-cache, max-age=600"));
    assertThat(storageDetails.getURI(), is(config.getUri()));
  }

  @Test
  public void shouldProcessConfigNullValues() throws Exception {
    init(new ProviderConfig());
    assertThat(storageDetails.getCacheControl(), is("no-cache, max-age=900"));
    assertThat(storageDetails.getBucketPrefix(), is(""));
  }

  @Test
  public void shouldAddEndSlashToBucketPrefixIf() {
    String processedBucketPrefix = getConfig().getBucketPrefix() + "/";
    assertThat(storageDetails.getBucketPrefix(), is(processedBucketPrefix));
  }

  @Test
  public void shouldRemoveStartSlashFromBucketPrefix() throws Exception {
    String bucketPrefix = "/slashBoundedPrefix/";
    String processedPrefix = "slashBoundedPrefix/";
    init(getBucketPrefixConfig(bucketPrefix));
    assertThat(storageDetails.getBucketPrefix(), is(processedPrefix));
  }

  @Test
  public void shouldReturnEmptyBucketPrefixIfNull() throws Exception {
    String nullBucketPrefix = null;
    init(getBucketPrefixConfig(nullBucketPrefix));
    assertThat(storageDetails.getBucketPrefix(), is(""));
  }

  @Test
  public void shouldReturnEmptyBucketPrefixIfEmpty() throws Exception {
    String bucketPrefix = "";
    init(getBucketPrefixConfig(bucketPrefix));
    assertThat(storageDetails.getBucketPrefix(), is(""));
  }

  @Test
  public void shouldThrowExceptionIfBucketPrefixHasSlashesOnly() throws Exception {
    String bucketPrefix = "///";
    init(getBucketPrefixConfig(bucketPrefix));

    exceptionRule.expect(Exception.class);
    exceptionRule.expectMessage(INVALID_BUCKET_PREFIX);

    storageDetails.getBucketPrefix();
  }

  @Test
  public void shouldThrowExceptionIfBucketPrefixHasUnacceptableCharacters() throws Exception {
    String bucketPrefix = "#";
    init(getBucketPrefixConfig(bucketPrefix));

    exceptionRule.expect(Exception.class);
    exceptionRule.expectMessage(INVALID_BUCKET_PREFIX);

    storageDetails.getBucketPrefix();
  }

  private ProviderConfig getConfig() {
    ProviderConfig config = new ProviderConfig();
    config.setBucket("bucket.name");
    config.setBucketPrefix("Az9/a b-test");
    config.setUri("https://test.net");
    config.setCacheLength("600");
    return config;
  }

  private ProviderConfig getBucketPrefixConfig(String bucketPrefix) {
    ProviderConfig config = new ProviderConfig();
    config.setBucketPrefix(bucketPrefix);
    return config;
  }

  private void init(ProviderConfig config) throws Exception {
    String domain = "testDomain";
    String moduleName = "testModule";

    given(moduleInfo.getModuleName()).willReturn(moduleName);
    given(providerConfigService.getConfig(eq(moduleName), eq(domain))).willReturn(config);

    storageDetails.initialize(domain);
  }
}
