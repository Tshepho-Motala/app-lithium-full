package lithium.service.domain.client.objects;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DomainRevision implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	int version;
	private Domain domain;
	private Date creationDate;
	private List<DomainRevisionLabelValue> labelValueList;
}
