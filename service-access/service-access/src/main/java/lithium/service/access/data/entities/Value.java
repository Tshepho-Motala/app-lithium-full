package lithium.service.access.data.entities;

import java.io.Serializable;

import javax.persistence.Column;
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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.joda.ser.DateTimeSerializer;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(
	name="\"value\"",
	indexes = {
		@Index(name="idx_list_value_id", columnList="id", unique=true),
		@Index(name="idx_list_id", columnList="list_id", unique=false)
	}
)
public class Value implements Serializable {
	private static final long serialVersionUID = 8624624912151228923L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Version
	private int version;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	@JsonBackReference("list_value")
	private List list;
	
	@Column(nullable=false)
	private String data;
	
	@Default
	@Column(nullable=true)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  @JsonSerialize(using = DateTimeSerializer.class)
	private DateTime dateAdded = DateTime.now();
}
