package lithium.service.translate.client.objects;

import org.joda.time.DateTime;

import lombok.Data;
@Data
public class LatestChangeSet {
	private Long id;
	int version;
	private String name;
	private int changeNumber;
	private DateTime applyDate;	
	private Language language;
}
