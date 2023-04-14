package lithium.service.affiliate.data.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
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
		@Index(name="idx_value", columnList="value", unique=true)
	})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StringValue implements Serializable {

	private static final long serialVersionUID = 1L;

	@Version
	int version;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long id;

	@Column(nullable=false)
	String value;
	
	@Column(nullable=false)
	long users;
}
