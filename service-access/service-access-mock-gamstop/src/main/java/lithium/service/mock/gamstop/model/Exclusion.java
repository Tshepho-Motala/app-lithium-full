package lithium.service.mock.gamstop.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Exclusion
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-09-22T08:59:29.116Z")




public class Exclusion   {
  @JsonProperty("correlationId")
  private String correlationId = null;

  @JsonProperty("msRequestId")
  private String msRequestId = null;

  /**
   * Exclusion type
   */
  public enum ExclusionEnum {
    Y("Y"),
    
    N("N"),
    
    P("P");

    private String value;

    ExclusionEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static ExclusionEnum fromValue(String text) {
      for (ExclusionEnum b : ExclusionEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("exclusion")
  private ExclusionEnum exclusion = null;

  public Exclusion correlationId(String correlationId) {
    this.correlationId = correlationId;
    return this;
  }

  /**
   * The optional unique identifier field for each person if present in the request
   * @return correlationId
  **/
  @Schema(description = "The optional unique identifier field for each person if present in the request")


  public String getCorrelationId() {
    return correlationId;
  }

  public void setCorrelationId(String correlationId) {
    this.correlationId = correlationId;
  }

  public Exclusion msRequestId(String msRequestId) {
    this.msRequestId = msRequestId;
    return this;
  }

  /**
   * Unique reference that identifies this record in the GAMSTOP logs. Should be quoted with the response header X-Unique-Id by an operator when contacting GAMSTOP with either a technical or an application query
   * @return msRequestId
  **/
  @Schema(required = true, description = "Unique reference that identifies this record in the GAMSTOP logs. Should be quoted with the response header X-Unique-Id by an operator when contacting GAMSTOP with either a technical or an application query")
  @NotNull


  public String getMsRequestId() {
    return msRequestId;
  }

  public void setMsRequestId(String msRequestId) {
    this.msRequestId = msRequestId;
  }

  public Exclusion exclusion(ExclusionEnum exclusion) {
    this.exclusion = exclusion;
    return this;
  }

  /**
   * Exclusion type
   * @return exclusion
  **/
  @Schema(required = true, description = "Exclusion type")
  @NotNull


  public ExclusionEnum getExclusion() {
    return exclusion;
  }

  public void setExclusion(ExclusionEnum exclusion) {
    this.exclusion = exclusion;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Exclusion exclusion = (Exclusion) o;
    return Objects.equals(this.correlationId, exclusion.correlationId) &&
        Objects.equals(this.msRequestId, exclusion.msRequestId) &&
        Objects.equals(this.exclusion, exclusion.exclusion);
  }

  @Override
  public int hashCode() {
    return Objects.hash(correlationId, msRequestId, exclusion);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Exclusion {\n");

    sb.append("    correlationId: ").append(toIndentedString(correlationId)).append("\n");
    sb.append("    msRequestId: ").append(toIndentedString(msRequestId)).append("\n");
    sb.append("    exclusion: ").append(toIndentedString(exclusion)).append("\n");
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

