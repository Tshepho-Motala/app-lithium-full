package lithium.service.cdn.provider.google.builders;

import java.util.Date;

/**
 *
 */
public class CacheBuilder {

  /**
   *
   * @param inCacheDuration
   * @return
   */
  public static String build(String inCacheDuration) {
    Integer outCacheDuration = 900;

    if(inCacheDuration != null && ! inCacheDuration.isEmpty()) {
      try {
        outCacheDuration = Integer.parseInt(inCacheDuration);
      } catch(NumberFormatException exception ) {
        //We don't really need to log this as the cache is defaulted to 15 minutes
      }
    }

    return "no-cache, max-age=" + outCacheDuration;
  }
}
