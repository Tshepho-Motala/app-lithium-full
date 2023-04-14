package lithium.service.report.games.data.entities;

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
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@Table(indexes = {
		@Index(name="idx_name", columnList="name", unique=false),
		@Index(name="idx_description", columnList="description", unique=false)
	})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude={"report"})
@JsonIgnoreProperties(value={"report"})
public class ReportRevision implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Version
	int version;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long id;
	
	@Column(nullable=false)
	String name;
	String description;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=true)
	Report report;
	
	@Column(nullable=false)
	@DateTimeFormat(iso=ISO.DATE_TIME)
	Date updateDate;
	
	@Column(nullable=false)
	String updateBy;
	
	@Column(nullable=true)
	String notifyEmail;
	
	@Column(nullable=false)
	Integer granularity;
	
	@Column(nullable=false)
	Integer granularityOffset;
	
	@Column(nullable=true)
	Boolean allFiltersApplicable;
	
	@Column(nullable=true)
	String cron;
	
	@Column(name="compare_x_periods",nullable=true)
	Integer compareXperiods;
	
	@PrePersist
	void defaults() {
		if (updateDate == null) updateDate = new Date();
	}
}