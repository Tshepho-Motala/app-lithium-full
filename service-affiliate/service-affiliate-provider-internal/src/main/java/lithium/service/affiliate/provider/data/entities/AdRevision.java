package lithium.service.affiliate.provider.data.entities;

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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
@Table(indexes={
		@Index(name="idx_name", columnList="name", unique=false),
		@Index(name="idx_ad_id", columnList="adId", unique=false)
})
public class AdRevision {
	
	public static final int AD_TYPE_LINK = 0;
	public static final int AD_TYPE_IMAGE = 1;
	public static final int AD_TYPE_IFRAME = 2;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	int version;
	
	@ManyToOne
	@JoinColumn
	private long adId;
	
	@Column(nullable=false)
	private String name;
	
	@Column(nullable=false)
	private Boolean archived;
	
	@Column(nullable=false)
	private Boolean deleted;
	
	@Column(nullable=false)
	private Integer type;
	
	//Should all these dates not just live in changelog?
	@Column(nullable=false)
	private Date createdDate;
	
	@Column(nullable=false)
	private Date effectiveDate;
	
	@Column(nullable=false)
	private Date archiveDate;

	@Column(nullable=false)
	private Date deletedDate;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private Brand brand;
	
	@Column(nullable=false)
	private String targetUrl;
	
	@Column(nullable=true)
	private String entryPoint;
	
	@ManyToOne
	@JoinColumn
	private AdResource currentResource;
	
	@PrePersist
	void defaults() {
		if (archived == null) archived = false;
		if (deleted == null) deleted = false;
		if (createdDate == null) createdDate = new Date();
	}

}
