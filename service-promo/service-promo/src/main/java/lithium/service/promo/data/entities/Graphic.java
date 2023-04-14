package lithium.service.promo.data.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Data
@ToString(exclude="image")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude="image")
@Table
@JsonIgnoreProperties("image")
public class Graphic implements Serializable {
	@Serial
	private static final long serialVersionUID = 4774874169151154572L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	@Column(nullable=false)
	@Lob
	private byte[] image;

	@Column(nullable=false)
	private String name;

	@Column(nullable=true)
	private Long size;

	@Column(nullable=false)
	private String type;
}
