package lithium.service.stats.data.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
	@Index(name="idx_pd_all", columnList="year,month,week,day,hour,domain_id", unique=true),
	@Index(name="idx_pd_dates", columnList="dateStart,dateEnd,domain_id", unique=true),
	@Index(name="idx_pd_datestart", columnList="dateStart", unique=false),
	@Index(name="idx_pd_dateend", columnList="dateEnd", unique=false),
	@Index(name="idx_pd_granularity", columnList="granularity", unique=false),
})
public class Period implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final int GRANULARITY_YEAR = 1;
	public static final int GRANULARITY_MONTH = 2;
	public static final int GRANULARITY_DAY = 3;
	public static final int GRANULARITY_WEEK = 4;
	public static final int GRANULARITY_TOTAL = 5;
	public static final int GRANULARITY_HOUR = 6;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Version
	int version;
	
	@Column(nullable = false)
	private Integer year;
	@Column(nullable = false)
	private Integer month;
	@Column(nullable = false)
	private Integer week;
	@Column(nullable = false)
	private Integer day;
	@Column(nullable = false)
	private Integer hour;
	
	@Column(nullable = false)
	@DateTimeFormat(iso=ISO.DATE_TIME)
	private Date dateStart;
	@DateTimeFormat(iso=ISO.DATE_TIME)
	@Column(nullable = false)
	private Date dateEnd;
	
	@Column(nullable = false)
	private int granularity;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Domain domain;
	
	public String granularity() {
		switch (granularity) {
		case GRANULARITY_YEAR:
			return "GRANULARITY_YEAR";
		case GRANULARITY_MONTH:
			return "GRANULARITY_MONTH";
		case GRANULARITY_DAY:
			return "GRANULARITY_DAY";
		case GRANULARITY_WEEK:
			return "GRANULARITY_WEEK";
		case GRANULARITY_TOTAL:
			return "GRANULARITY_TOTAL";
		case GRANULARITY_HOUR:
			return "GRANULARITY_HOUR";
		default:
			return null;
		}
	}

	public boolean isDay() {
		return (granularity == GRANULARITY_DAY);
	}
}
