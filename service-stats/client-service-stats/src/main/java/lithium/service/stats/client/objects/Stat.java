package lithium.service.stats.client.objects;

import java.io.Serializable;
import java.util.regex.Pattern;

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
public class Stat implements Serializable {
	private static final long serialVersionUID = -5195112153855639993L;
	private Long id;
	private String name;
	private Domain domain;
	private User owner;
	
	public String event() {
		return name.split(Pattern.quote("."))[name.split(Pattern.quote(".")).length-1];
	}
}