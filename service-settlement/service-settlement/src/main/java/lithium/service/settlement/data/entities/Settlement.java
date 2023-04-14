package lithium.service.settlement.data.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
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

@javax.persistence.Entity
@Data
@ToString(exclude="batchSettlements")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude="batchSettlements")
@Table(indexes={
	@Index(name="idx_all", columnList="entity_id, user_id, dateStart, dateEnd", unique=true),
	@Index(name="idx_dates", columnList="dateStart, dateEnd", unique=false),
	@Index(name="idx_entity", columnList="entity_id", unique=false),
	@Index(name="idx_domain", columnList="domain_id", unique=false),
	@Index(name="idx_user", columnList="user_id", unique=false)
})
public class Settlement implements Serializable {
	private static final long serialVersionUID = 3494257833160928980L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Version
	private int version;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=true)
	@JsonBackReference
	private BatchSettlements batchSettlements;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Domain domain;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=true)
	private Entity entity;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=true)
	private User user;
	
	@Column(nullable=false)
	@DateTimeFormat(iso=ISO.DATE_TIME)
	private Date createdDate;
	
	@Column(nullable=false)
	private String createdBy;
	
	@Column(nullable=false)
	@DateTimeFormat(iso=ISO.DATE_TIME)
	private Date dateStart;
	
	@Column(nullable=true)
	@DateTimeFormat(iso=ISO.DATE_TIME)
	private Date dateEnd;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="settlement", cascade=CascadeType.PERSIST, orphanRemoval=true)
	@JsonManagedReference
	private List<SettlementEntry> settlementEntries;
	
	@Column(nullable=false)
	private Boolean open;
	
	@Column(nullable=true)
	private BigDecimal total;
	
	@OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST, orphanRemoval=true)
	@JoinColumn(name = "pdf_id")
	private SettlementPDF pdf;
	
	@OneToOne(fetch = FetchType.EAGER, cascade=CascadeType.PERSIST, orphanRemoval=true)
	@JoinColumn(name = "physical_address_id")
	private Address physicalAddress;
	
	@OneToOne(fetch = FetchType.EAGER, cascade=CascadeType.PERSIST, orphanRemoval=true)
	@JoinColumn(name = "billing_address_id")
	private Address billingAddress;
	
	@OneToOne(fetch = FetchType.EAGER, cascade=CascadeType.PERSIST, orphanRemoval=true)
	@JoinColumn(name = "bank_details_id")
	private BankDetails bankDetails;
	
	@Transient
	private lithium.service.entity.client.objects.Entity externalEntity;
	
	@Transient
	private lithium.service.user.client.objects.User externalUser;
	
	@PrePersist
	public void prePersist() {
		if (createdDate == null) createdDate = new Date();
	}
}
