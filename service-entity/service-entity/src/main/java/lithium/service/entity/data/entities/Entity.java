package lithium.service.entity.data.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@javax.persistence.Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
@Table(indexes={@Index(name="idx_uuid", columnList="uuid", unique=true)})
public class Entity implements Serializable {
	private static final long serialVersionUID = 1011190172750630560L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false)
	private String uuid;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "status_id")
	private Status status;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "entity_type_id")
	private EntityType entityType;
	
	@Column(nullable=false)
	@Size(min=2, max=55, message="No more than 50 and no less than 2 characters")
	private String name;
	
	@Pattern(regexp="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$",
			message="Invalid email address")
	@Column(name="EMAIL")
	private String email;
	
	@Column
	@Pattern(regexp="^((\\+[0-9]{1,3}){0,1}[0-9\\ ]{4,14}(?:x.+)?){0,1}$", message="Invalid telephone number. Please specify the number in international format (+47 xx xxx xxx) or local format (xx xxx xxx). Spaces are allowed. An extension using x is also allowed.")
	private String telephoneNumber;
	
	@Column
	@Pattern(regexp="^((\\+[0-9]{1,3}){0,1}[0-9\\ ]{4,14}(?:x.+)?){0,1}$", message="Invalid cellphone number. Please specify the number in international format (+47 xx xxx xxx) or local format (xx xxx xxx). Spaces are allowed.")
	private String cellphoneNumber;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "domain_id")
	@JsonManagedReference
	private Domain domain;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade=CascadeType.PERSIST)
	@JoinColumn(name = "physical_address_id")
	private Address physicalAddress;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade=CascadeType.PERSIST)
	@JoinColumn(name = "billing_address_id")
	private Address billingAddress;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private BankDetails bankDetails;
	
	@Column(nullable=false)
	private Date createdDate;
	
	@Column(nullable=false)
	private Date updatedDate;
	
	@PreUpdate
	@PrePersist
	public void calculatedFields() {
		if (createdDate == null)
			createdDate = new Date();
		updatedDate = new Date();
	}
}