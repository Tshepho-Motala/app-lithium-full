package lithium.service.cdn.provider.google.builders;

/**
 *
 */
public class PrefixBuilder {

  /**
   *
   * @param inPrefix
   * @return
   */
  public static String build(String inPrefix) {
    String outPrefix = inPrefix;

    //Check if prefix starts with a '/', if yes remove it.
    if(inPrefix.startsWith("/"))
    {
      outPrefix = inPrefix.substring(1);
    }

    //Check if prefix ends with a '/', if no add it.
    if(! outPrefix.endsWith("/"))
    {
      outPrefix = outPrefix + "/";
    }

    return outPrefix;
  }
}
