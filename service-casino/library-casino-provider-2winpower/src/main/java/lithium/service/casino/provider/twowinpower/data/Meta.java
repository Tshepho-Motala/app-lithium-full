package lithium.service.casino.provider.twowinpower.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Meta {
	private Integer totalCount;
	private Integer pageCount;
	private Integer currentPage;
	private Integer perPage;
}
