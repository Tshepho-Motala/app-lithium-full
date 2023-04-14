package lithium.service.leaderboard.data.entities;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lithium.service.leaderboard.client.objects.Type;
import lithium.service.leaderboard.data.converter.EnumConverter.TypeConverter;
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
	@Index(name="idx_name", columnList="leaderboard_id, type", unique=true),
})
public class LeaderboardConversion implements Serializable {
	private static final long serialVersionUID = -8746591773604921501L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	private int version;
	
	@JoinColumn(nullable=false)
	@ManyToOne(fetch = FetchType.EAGER)
	@JsonIgnoreProperties("leaderboardConversions")
	private Leaderboard leaderboard;
	
	@Column(nullable=false)
//	@Enumerated(EnumType.ORDINAL)
	@Convert(converter=TypeConverter.class)
	private Type type;
	
	private BigDecimal conversion;
}