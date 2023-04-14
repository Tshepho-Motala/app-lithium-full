package lithium.service.stats.client.objects;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "name")
public class StatEntry { //<T> {
	private Long id;
	private Stat stat;
	private String ownerGuid;
	private String ipAddress;
	private String userAgent;
//	private Boolean success;
	@Builder.Default
	private Date entryDate = new Date();
	
	public static class StatEntryBuilder {
		public StatEntryBuilder name(String name) {
			if (stat == null) stat = Stat.builder().build();
			stat.setName(name);
			return this;
		}
		public StatEntryBuilder domain(String domainName) {
			if (stat == null) stat = Stat.builder().build();
			if (stat.getDomain() == null) stat.setDomain(Domain.builder().build());
			stat.getDomain().setName(domainName);
			return this;
		}
	}
	
	public String event() {
		return stat.event();
	}
}