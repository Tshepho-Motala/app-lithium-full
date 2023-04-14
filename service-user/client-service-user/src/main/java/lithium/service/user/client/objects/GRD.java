package lithium.service.user.client.objects;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString(of={"domain", "role"})
public class GRD implements Serializable {
	private static final long serialVersionUID = -8230785310847782674L;
	private Long id;
	private Role role;
	private Domain domain;
	private Boolean selfApplied; // Is this role applicable to the current domain ?
	private Boolean descending; // Is this role applicable to the children for this domain ?
}