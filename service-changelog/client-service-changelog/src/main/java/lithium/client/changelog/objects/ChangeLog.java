package lithium.client.changelog.objects;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChangeLog {
	long id;
	String entity;
	String type;
	long entityRecordId;
	String authorGuid;
	String authorFullName;
	Date changeDate;
	String comments;
	String additionalInfo;
	List<ChangeLogFieldChange> changes;
	String categoryName;
	String subCategoryName;
	boolean deleted = false;
	int priority = 0;
	String updatedBy;
	Date dateUpdated;
	String domainName;

	public String getAuthor() {
		if (authorFullName != null && !authorFullName.isEmpty()){
			return authorFullName;
		} else return authorGuid;
	}
}

