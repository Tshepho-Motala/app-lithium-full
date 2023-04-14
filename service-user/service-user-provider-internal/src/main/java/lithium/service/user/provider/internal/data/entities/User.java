package lithium.service.user.provider.internal.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(nullable = false)
	private String domain;
	
	@Column(nullable = false, unique = true)
	@Size(min = 2, max = 35, message = "No more than 30 and no less than 2 characters")
	@Pattern(regexp = "^[_A-Za-z0-9\\._ØÆÅøæå]+$", message = "Only numbers, letters, underscore and dots allowed")
	private String username;
	
	@Column(nullable = false)
	private String password;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinTable(
		name = "user_roles",
		joinColumns = { @JoinColumn(name = "user_id", nullable = false, updatable = false) },
		inverseJoinColumns = { @JoinColumn(name = "role_id", nullable = false, updatable = false) },
		indexes = { @Index(name = "idx_urr_id", columnList = "user_id", unique = false) }
	)
	private Set<Role> roles;
	
	@Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message = "Invalid email address")
	@Column(name = "EMAIL")
	private String email;
	
	@Column(nullable = false)
	@Size(min = 2, max = 255, message = "No more than 255 and no less than 2 characters")
	private String firstName;
	
	@Column(nullable = false)
	@Size(min = 2, max = 255, message = "No more than 255 and no less than 2 characters")
	private String lastName;
	
	@Column(nullable = false)
	private boolean enabled;
	
	@Column(nullable = false)
	private boolean deleted;
	
	@Column
	private String residentialAddress;
	
	@Column
	private String postalAddress;
	
	@Column
	@Pattern(regexp = "^((\\+[0-9]{1,3}){0,1}[0-9\\ ]{4,14}(?:x.+)?){0,1}$", message = "Invalid telephone number. Please specify the number in international format (+47 xx xxx xxx) or local format (xx xxx xxx). Spaces are allowed. An extension using x is also allowed.")
	private String telephoneNumber;
	
	@Column
	@Pattern(regexp = "^((\\+[0-9]{1,3}){0,1}[0-9\\ ]{4,14}(?:x.+)?){0,1}$", message = "Invalid cellphone number. Please specify the number in international format (+47 xx xxx xxx) or local format (xx xxx xxx). Spaces are allowed.")
	private String cellphoneNumber;
	
	@Column(length = 1000)
	@Size(max = 1000, message = "The comment may not exceed 1000 characters")
	private String comments;
	
	@Column(nullable = false)
	private Date createdDate = new Date();
	
	@Column(nullable = false)
	private Date updatedDate = new Date();
	
}