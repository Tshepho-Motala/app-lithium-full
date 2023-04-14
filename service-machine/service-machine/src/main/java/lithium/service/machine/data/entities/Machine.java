package lithium.service.machine.data.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

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
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@javax.persistence.Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
@Table(indexes = { 
		@Index(columnList = "domain_id, guid", unique = true),
		@Index(columnList = "domain_id, status_id, lastPing") 
})
public class Machine implements Serializable {
	private static final long serialVersionUID = 1011190172750630560L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false)
	private String guid;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "status_id")
	private Status status;
	
	@Column(nullable = true)
	@Size(min = 2, max = 55, message = "No more than 50 and no less than 2 characters")
	private String name;
	
	@Column(nullable=true)
	private String description;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "domain_id")
	@JsonManagedReference
	private Domain domain;
	
	@Column(nullable = false)
	private Date createdDate;
	
	@Column(nullable = false)
	private Date updatedDate;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "location_id")
	@JsonManagedReference
	private Location location;
	
	@JsonManagedReference
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "machine", cascade = CascadeType.ALL)
	@Where(clause="deleted=false")
	@OrderBy
	private Set<Relationship> relationships;
	
	@Column(nullable = true) 
	private Date lastPing;
	
	@PreUpdate
	@PrePersist
	public void calculatedFields() {
		if (createdDate == null)
			createdDate = new Date();
		updatedDate = new Date();
	}
}