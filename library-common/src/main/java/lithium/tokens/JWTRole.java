package lithium.tokens;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Used in the construction of the JWT object.
 * 
 * @author riaans
 */

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class JWTRole {
	@JsonProperty("n")
	private String name;
//	@JsonProperty("c")
//	private Long categoryId;
	@JsonProperty("s")
	private Boolean selfApplied; // Is this role applicable to the current domain ?
	@JsonProperty("d")
	private Boolean descending; // Is this role applicable to the children for this domain ?
}