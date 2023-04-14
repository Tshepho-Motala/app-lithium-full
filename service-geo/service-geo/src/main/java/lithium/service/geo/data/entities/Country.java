
package lithium.service.geo.data.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;

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
		@Index(name="idx_iso3", columnList="iso3", unique=true),
		@Index(name="idx_fips", columnList="fips", unique=true),
		@Index(name="idx_name", columnList="name", unique=true)
	})
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Country implements Serializable {

	private static final long serialVersionUID = 1L;

	@Version
	int version;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long id;
	
	String code;
	String iso3;
	Integer isoNr;
	String fips;
	String name;
	String capital;
	Long sqkm;
	Long population;
	String continent;
	String topLevelDomain;
	String currencyCode;
	String currencyName;
	String phone;
	String postalCodeFormat;
	String postalCodeRegex;
	String languages;
	String neighbours;
	String equivalentFips;
	
	Boolean enabled;
	Boolean manual;
	
	@PrePersist
	void defaults() {
		if (enabled == null) enabled = true;
		if (manual == null) manual = false;
	}
	
}
