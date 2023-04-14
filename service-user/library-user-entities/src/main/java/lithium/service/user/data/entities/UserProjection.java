package lithium.service.user.data.entities;

import java.util.Date;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "userProjection", types = {User.class})
public interface UserProjection {

  Long getId();

  Domain getDomain();

  String getUsername();

  String getGuid();

  //Should not be included
  //	String password;

  Date getPasswordUpdated();

  String getPasswordUpdatedBy();

//	List<Group> getGroups();

//	List<Label> getLabels();

//	String getEmail();

  String getFirstName();

  String getLastName();

  boolean getDeleted();

//	Address getResidentialAddress();

//	Address getPostalAddress();

  String getTelephoneNumber();

  String getCellphoneNumber();

  String getCountryCode();

  String getComments();

  Date getCreatedDate();

  Date getUpdatedDate();

  Status getStatus();

  boolean getEmailValidated();

//	String getSocialSecurityNumber();

  String getBonusCode();

  Integer getDobYear();

  Integer getDobMonth();

  Integer getDobDay();

  boolean getWelcomeEmailSent();
}
