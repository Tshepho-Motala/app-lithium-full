package lithium.service.settlement.data.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@javax.persistence.Entity
@Data
@ToString(exclude="settlements")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude="settlements")
@Table(indexes={
	@Index(name="idx_name", columnList="name", unique=true),
	@Index(name="idx_open", columnList="open", unique=false),
	@Index(name="idx_created_date", columnList="createdDate", unique=false)
})
public class BatchSettlements {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(nullable=false)
	private String name;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Domain domain;
	
	@Column(nullable=false)
	@DateTimeFormat(iso=ISO.DATE_TIME)
	private Date createdDate;
	
	@Column(nullable=false)
	private Boolean open;
	
	@Column(nullable=false)
	private Boolean rerunning;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="batchSettlements")
	@JsonManagedReference
	private List<Settlement> settlements;
	
	@PrePersist
	public void prePersist() {
		if (this.open == null) this.open = true;
		if (this.rerunning == null) this.rerunning = false;
	}
}
