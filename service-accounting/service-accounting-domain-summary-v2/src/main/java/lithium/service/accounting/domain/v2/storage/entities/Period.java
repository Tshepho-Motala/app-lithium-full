package lithium.service.accounting.domain.v2.storage.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

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
import java.io.Serializable;
import java.util.Date;

@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(
	indexes = {
		@Index(name = "idx_pd_all", columnList = "year, month, week, day, domain_id", unique = true),
		@Index(name = "idx_pd_dates", columnList = "dateStart, dateEnd, domain_id", unique = true),
		@Index(name = "idx_pd_datestart", columnList = "dateStart", unique = false),
		@Index(name = "idx_pd_dateend", columnList = "dateEnd", unique = false),
		@Index(name = "idx_pd_open", columnList = "open", unique = false),
		@Index(name = "idx_pd_granularity", columnList = "granularity", unique = false)
	}
)
public class Period implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final int GRANULARITY_YEAR = 1;
	public static final int GRANULARITY_MONTH = 2;
	public static final int GRANULARITY_DAY = 3;
	public static final int GRANULARITY_WEEK = 4;
	public static final int GRANULARITY_TOTAL = 5;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	private int version;
	
	@Column(nullable = false)
	private Integer year;
	@Column(nullable = false)
	private Integer month;
	@Column(nullable = false)
	private Integer week;
	@Column(nullable = false)
	private Integer day;
	
	@Column(nullable = false)
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date dateStart;
	@DateTimeFormat(iso = ISO.DATE_TIME)
	@Column(nullable = false)
	private Date dateEnd;
	
	@Column(nullable = false)
	private boolean open;
	
	@Column(nullable = false)
	private int granularity;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	private Domain domain;
}
