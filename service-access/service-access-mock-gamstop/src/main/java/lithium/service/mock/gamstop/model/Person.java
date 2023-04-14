package lithium.service.mock.gamstop.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Person
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-09-22T08:59:29.116Z")
public class Person   {
  @JsonProperty("correlationId")
  private String correlationId = null;

  @JsonProperty("firstName")
  private String firstName = null;

  @JsonProperty("lastName")
  private String lastName = null;

  @JsonProperty("dateOfBirth")
  @JsonFormat(pattern="yyyy-MM-dd")
  @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
  private LocalDate dateOfBirth = null;

  @JsonProperty("email")
  private String email = null;

  @JsonProperty("postcode")
  private String postcode = null;

  @JsonProperty("mobile")
  private String mobile = null;

  public Person correlationId(String correlationId) {
    this.correlationId = correlationId;
    return this;
  }

  /**
   * Unique identifier must be unique within the context of this request
   * @return correlationId
  **/
  @Schema(description = "Unique identifier must be unique within the context of this request")

@Size(max=255) 
  public String getCorrelationId() {
    return correlationId;
  }

  public void setCorrelationId(String correlationId) {
    this.correlationId = correlationId;
  }

  public Person firstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  /**
   * First name of person
   * @return firstName
  **/
  @Schema(description = "First name of person", required = true )
  @NotNull

@Size(min=1,max=255) 
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public Person lastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  /**
   * Last name of person
   * @return lastName
  **/
  @Schema(required = true, description = "Last name of person")
  @NotNull

@Size(min=1,max=255) 
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Person dateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
    return this;
  }

  /**
   * Date of birth in ISO format (yyyy-mm-dd)
   * @return dateOfBirth
  **/
  @Schema(description = "Date of birth in ISO format (yyyy-mm-dd)")

  @Valid

  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public Person email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Email address
   * @return email
  **/
  @Schema(description = "Email address")

@Size(min=0) 
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Person postcode(String postcode) {
    this.postcode = postcode;
    return this;
  }

  /**
   * Postcode - spaces not significant
   * @return postcode
  **/
  @Schema(required = true, description = "Postcode - spaces not significant")
  @NotNull

@Size(min=5,max=10) 
  public String getPostcode() {
    return postcode;
  }

  public void setPostcode(String postcode) {
    this.postcode = postcode;
  }

  public Person mobile(String mobile) {
    this.mobile = mobile;
    return this;
  }

  /**
   * UK mobile telephone number which may include spaces, hyphens and optionally be prefixed with the international dialling code (+44, 0044, +353, 00353).
   * @return mobile
  **/
  @Schema(required = true, description = "UK mobile telephone number which may include spaces, hyphens and optionally be prefixed with the international dialling code (+44, 0044, +353, 00353).")
  @NotNull

@Size(min=0,max=14) 
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Person person = (Person) o;
    return
//            this.correlationId.equalsIgnoreCase(person.correlationId) &&
        this.firstName.equalsIgnoreCase(person.firstName) &&
        this.lastName.equalsIgnoreCase(person.lastName) &&
        this.dateOfBirth.compareTo(person.dateOfBirth) == 0 &&
        this.email.equalsIgnoreCase(person.email) &&
        this.postcode.equalsIgnoreCase(person.postcode) &&
        this.mobile.equalsIgnoreCase(person.mobile);
  }

  @Override
  public int hashCode() {
    return Objects.hash(correlationId, firstName, lastName, dateOfBirth, email, postcode, mobile);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Person {\n");

    sb.append("    correlationId: ").append(toIndentedString(correlationId)).append("\n");
    sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
    sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
    sb.append("    dateOfBirth: ").append(toIndentedString(dateOfBirth)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    postcode: ").append(toIndentedString(postcode)).append("\n");
    sb.append("    mobile: ").append(toIndentedString(mobile)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

