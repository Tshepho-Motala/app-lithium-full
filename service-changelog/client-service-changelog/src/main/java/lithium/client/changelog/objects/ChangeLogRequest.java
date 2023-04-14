package lithium.client.changelog.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeLogRequest {
	private String[] entities;
	private String[] types;
	private Long entityRecordId;
	private Integer page;
	private Integer pageSize = 10;
	private String sortDirection = "desc";
	private String sortField = "changeDate";
	private String searchValue;
	
	public String getSortField() {
		if (sortField != null && sortField.equals("authorGuid")) {
			return "authorUser.guid";
		}
		return sortField;
	}
	
	public static class ChangeLogRequestBuilder {
		private Integer pageSize = 10;
		private String sortDirection = "desc";
		private String sortField = "changeDate";
	}
}