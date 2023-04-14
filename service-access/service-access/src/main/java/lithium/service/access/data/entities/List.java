package lithium.service.access.data.entities;

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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude="values")
@EqualsAndHashCode(of={"id", "domain", "name"})
@Table(indexes = {
	@Index(name="idx_list_id", columnList="id", unique=true),
	@Index(name="idx_domain_name", columnList="domain_id, name", unique=true)
})
@Builder(toBuilder = true)
public class List {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Domain domain;
	
	@Column(nullable=false)
	private String name;
	
	@Column(nullable=true)
	private String description;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(nullable=false)
	private ListType listType;
	
	@Column(nullable=false)
	private boolean enabled;
	
	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.MERGE)
	@JoinTable(
		name = "list_value",
		joinColumns = {	@JoinColumn(name = "list_id", nullable = false, updatable = false) }, 
		inverseJoinColumns = { @JoinColumn(name = "list_value_id", nullable = false, updatable = false) },
		indexes = {
			@Index(name="idx_list_id", columnList="list_id", unique=false),
			@Index(name="idx_list_value_id", columnList="list_value_id", unique=false)
		}
	)
	@JsonManagedReference("list_value")
	private java.util.Set<Value> values;
	
	@Version
	private int version;
}
