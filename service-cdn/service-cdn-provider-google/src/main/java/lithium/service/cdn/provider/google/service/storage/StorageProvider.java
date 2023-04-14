package lithium.service.cdn.provider.google.service.storage;

import com.google.cloud.storage.Storage;

public interface StorageProvider {

  Storage getStorage();

  String getBucket();

  String getCacheControl();

}
