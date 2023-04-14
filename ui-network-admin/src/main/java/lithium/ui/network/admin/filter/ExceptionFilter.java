package lithium.ui.network.admin.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lithium.exceptions.Status460LoginRestrictedException;
import lithium.exceptions.Status469InvalidInputException;
import lithium.exceptions.Status492ExcessiveFailedLoginBlockException;
import lithium.service.translate.client.objects.LoginError;
import lithium.exceptions.Status453EmailNotUniqueException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 *
 */
@Slf4j
@Component
public class ExceptionFilter extends ZuulFilter {

  @Autowired MessageSource messageSource;

  /**
   *
   * @return
   */
  @Override
  public String filterType() {
    return "post";
  }

  /**
   *
   * @return
   */
  @Override
  public int filterOrder() {
    //Needs to run before SendErrorFilter which has filterOrder == 0
    return -1;
  }

  /**
   *
   * @return
   */
  @Override
  public boolean shouldFilter() {
    //Only forward to errorPath if it hasn't been forwarded to already
    return RequestContext.getCurrentContext().containsKey("error.status_code");
  }

  /**
   *
   * @return
   */
  @Override
  public Object run() {
    try {
      RequestContext ctx = RequestContext.getCurrentContext();
      Object exceptionObject = ctx.get("error.exception");

      if (exceptionObject != null && exceptionObject instanceof IllegalArgumentException) {
        IllegalArgumentException illegalArgumentException = (IllegalArgumentException)exceptionObject;
        String message = illegalArgumentException.getMessage();

        if(message.contains("[") && message.contains("]")) {
          ObjectMapper mapper = new ObjectMapper();
          Integer statusCode = Integer.parseInt(message.substring(message.indexOf("[") +1, message.indexOf("]")));

          switch(statusCode) {
            case 453: {
              // Remove error code to prevent further error handling in follow up filters
              ctx.remove("error.status_code");

              //Populate context with new response values
              Status453EmailNotUniqueException emailException = new Status453EmailNotUniqueException(
                      "The email you are trying to use already exists within the domain, and the domain does not allow duplicate emails.");
              ctx.setResponseBody(mapper.writeValueAsString(emailException));
              ctx.getResponse().setContentType("application/json");
              ctx.setResponseStatusCode(statusCode);
              break;
            }
            case 492: {
              // Remove error code to prevent further error handling in follow up filters
              ctx.remove("error.status_code");

              //Populate context with new response values
              Status492ExcessiveFailedLoginBlockException blockedException = new Status492ExcessiveFailedLoginBlockException(
                  "Your account has been blocked due to excessive failed logins. Please reset your password to log in.");
              ctx.setResponseBody(mapper.writeValueAsString(blockedException));
              ctx.getResponse().setContentType("application/json");
              ctx.setResponseStatusCode(statusCode);
              break;
            }
            case 469: {
              // Remove error code to prevent further error handling in follow up filters
              ctx.remove("error.status_code");

              Status469InvalidInputException blocked469Exception = new Status469InvalidInputException(
                  "Problem adding or modifying ecosystem");
              ctx.setResponseBody(mapper.writeValueAsString(blocked469Exception));
              ctx.getResponse().setContentType("application/json");
              ctx.setResponseStatusCode(statusCode);
              break;
            }
            case 460: {
              ctx.remove("error.status_code");

              Status460LoginRestrictedException loginRestrictedException = new Status460LoginRestrictedException(LoginError.BACKOFFICE_ORIGIN_LOGIN_BLOCK.getResponseMessageLocal(messageSource, "default"));
              ctx.setResponseBody(mapper.writeValueAsString(loginRestrictedException));
              ctx.getResponse().setContentType("application/json");
              ctx.setResponseStatusCode(statusCode);
              break;
            }
          }
        }
      }
    }
    catch (Exception exception) {
      ReflectionUtils.rethrowRuntimeException(exception);
    }

    return null;
  }

}
