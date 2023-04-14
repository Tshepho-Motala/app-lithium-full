package lithium.service.geo.data.entities;

import java.io.Serializable;

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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@Table(indexes = {
		@Index(name="idx_code", columnList="code", unique=true),
		@Index(name="idx_name", columnList="name", unique=false)
	})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"country"})
public class AdminLevel1 implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Version
	int version;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long id;
	
	String code;
	String name;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=true)
	Country country;
	
	Boolean enabled;
	Boolean manual;
	
	@PrePersist
	void defaults() {
		if (enabled == null) enabled = true;
		if (manual == null) manual = false;
	}
}
