package lithium.service.settlement.data.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString(exclude="settlement")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude="settlement")
@Table(indexes={
	@Index(name="idx_dates", columnList="dateStart, dateEnd", unique=false)
})
public class SettlementEntry implements Serializable {
	private static final long serialVersionUID = -6619751689766864590L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Version
	private int version;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	@JsonBackReference
	private Settlement settlement;
	
	@Column(nullable=false)
	private BigDecimal amount;
	
	@Column(nullable=false)
	@DateTimeFormat(iso=ISO.DATE_TIME)
	private Date dateStart;
	
	@Column(nullable=false)
	@DateTimeFormat(iso=ISO.DATE_TIME)
	private Date dateEnd;
	
	@Column(nullable=true)
	private String description;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="settlementEntry", cascade=CascadeType.PERSIST, orphanRemoval=true)
	@JsonManagedReference
	private List<SettlementEntryLabelValue> labelValues;
}
