package lithium.service.raf.data.entities;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import lithium.service.raf.converter.EnumConverter.RAFConversionTypeConverter;
import lithium.service.raf.data.enums.AutoConvertPlayer;
import lithium.service.raf.enums.RAFConversionType;
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
	@Index(name="idx_domain", columnList="domain_id", unique=true)
})
public class Configuration {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Version
	private int version;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Domain domain;
	
	@Column(nullable=true)
	private String referrerBonusCode;
	
	@Column(nullable=true)
	private String refereeBonusCode;
	
	@Column(nullable=true)
	private String referralNotification;
	
	@Column(nullable=false)
	@Convert(converter=RAFConversionTypeConverter.class)
	private RAFConversionType conversionType;
	
	@Column(nullable=true)
	private Integer conversionXpLevel;
	
	@Column(nullable=true)
	@Enumerated(EnumType.STRING)
	private AutoConvertPlayer autoConvertPlayer;
}
