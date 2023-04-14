package lithium.service.cdn.provider.google.builders;

/**
 *
 */
public class TemplateBuilder {

  /**
   *
   * @param head
   * @param body
   * @return
   */
  public static String build(String head, String body) {
    StringBuilder content = new StringBuilder();

    content.append("<html><head>")      //Add opening html and head.
        .append(head)                   //Add head
        .append("</head><body>")        //Add closing head and opening body
        .append(body)                   //Add body
        .append("</body></html>");      //Add closing body and closing html

    return content.toString();
  }
}
