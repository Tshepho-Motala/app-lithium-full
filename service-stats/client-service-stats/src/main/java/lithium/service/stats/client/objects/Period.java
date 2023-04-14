package lithium.service.stats.client.objects;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Period implements Serializable {
	private static final long serialVersionUID = 4922206517231327386L;
	
	private long id;
	private Integer year;
	private Integer month;
	private Integer week;
	private Integer day;
	private Integer hour;
	
	@DateTimeFormat(iso=ISO.DATE_TIME)
	private Date dateStart;
	@DateTimeFormat(iso=ISO.DATE_TIME)
	private Date dateEnd;
	
	private int granularity;
	
	private Domain domain;
	
	@ToString
	@JsonFormat(shape = JsonFormat.Shape.OBJECT)
	@AllArgsConstructor(access=AccessLevel.PRIVATE)
	@Deprecated
	public enum Granularity {
		GRANULARITY_YEAR(1, "GRANULARITY_YEAR"),
		GRANULARITY_MONTH(2, "GRANULARITY_MONTH"),
		GRANULARITY_DAY(3, "GRANULARITY_DAY"),
		GRANULARITY_WEEK(4, "GRANULARITY_WEEK"),
		GRANULARITY_TOTAL(5, "GRANULARITY_TOTAL"),
		GRANULARITY_HOUR(6, "GRANULARITY_HOUR");
		
		@Setter
		@Accessors(fluent = true)
		private Integer granularity;
		@Getter
		@Setter
		@Accessors(fluent = true)
		private String type;
		
		@JsonValue
		public Integer granularity() {
			return granularity;
		}
		
		@JsonCreator
		public static Granularity fromGranularity(int granularity) {
			for (Granularity g: Granularity.values()) {
				if (g.granularity == granularity) {
					return g;
				}
			}
			return GRANULARITY_TOTAL;
		}
		
		@JsonCreator
		public static Granularity fromType(String type) {
			for (Granularity g: Granularity.values()) {
				if (g.type == type) {
					return g;
				}
			}
			return GRANULARITY_TOTAL;
		}
	}
}
