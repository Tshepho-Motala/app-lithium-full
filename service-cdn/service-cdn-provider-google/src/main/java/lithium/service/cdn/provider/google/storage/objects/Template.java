package lithium.service.cdn.provider.google.storage.objects;

import lombok.Data;

import java.io.Serializable;

/**
 *
 */
@Data
public class Template implements Serializable {

  private String content;

  private String head;

  /**
   *
   */
  public Template() {
  }
}
