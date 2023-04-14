package lithium.service.document.client.objects;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

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
public class Document implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private int version;

	private long id;
	
	private String name;
	
	private String uuid;
	
	private String statusName;
	
	private String functionName;
	
	private String ownerGuid;
	
	private String authorServiceName;
	
	private boolean deleted;
	
	private boolean archived;
	
	@DateTimeFormat(iso=ISO.DATE_TIME)
	private Date lastFileUploadDate;

	private List<Integer> pages;
}
