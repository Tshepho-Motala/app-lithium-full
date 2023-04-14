package lithium.service.entity.data.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
		@Index(name="idx_city", columnList="city", unique=false),
		@Index(name="idx_cityCode", columnList="cityCode", unique=false),
		@Index(name="idx_adminLevel1", columnList="adminLevel1", unique=false),
		@Index(name="idx_adminLevel1Code", columnList="adminLevel1Code", unique=false),
		@Index(name="idx_country", columnList="country", unique=false),
		@Index(name="idx_countryCode", columnList="countryCode", unique=false),
})
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class Address implements Serializable {
	private static final long serialVersionUID = -8586012009095938132L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
		
	@Column(nullable=false)
	private String addressLine1;

	@Column
	private String addressLine2;
	
	@Column
	private String addressLine3;
	
	@Column(nullable=false)
	private String city;
	
	@Column(length=10)
	private String cityCode;
	
	@Column
	private String adminLevel1;
	
	@Column(length=10)
	private String adminLevel1Code;
	
	@Column(nullable=false)
	private String country;
	
	@Column(length=10)
	private String countryCode;
	
	@Column
	private String postalCode;
	
	private Long entityId;
	
	public boolean isComplete() {
		if ((addressLine1==null) || (city==null) || (country==null)) {
			return false;
		}
		if ((addressLine1.isEmpty()) || (city.isEmpty()) || (country.isEmpty())) {
			return false;
		}
		return true;
	}
	
	public String getOneLinerFull() {
		return toOneLinerFull();
	}
	
	public static void appendWithComma(StringBuffer sb, String value) {
		if (value == null) return;
		if (value.isEmpty()) return;
		if (sb.length() > 0) sb.append(", ");
		sb.append(value);
	}
	
	public String toOneLinerStreet() {
		StringBuffer result = new StringBuffer(); 
		Address.appendWithComma(result, addressLine1);
		Address.appendWithComma(result, addressLine2);
		Address.appendWithComma(result, addressLine3);
		return result.toString();
	}
	
	public String toOneLinerFull() {
		StringBuffer result = new StringBuffer(); 
		Address.appendWithComma(result, addressLine1);
		Address.appendWithComma(result, addressLine2);
		Address.appendWithComma(result, addressLine3);
		Address.appendWithComma(result, city);
		Address.appendWithComma(result, adminLevel1);
		Address.appendWithComma(result, country);
		Address.appendWithComma(result, postalCode);
		return result.toString();
	}
}
