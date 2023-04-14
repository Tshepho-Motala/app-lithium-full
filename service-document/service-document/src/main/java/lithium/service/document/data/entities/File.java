package lithium.service.document.data.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Data
@ToString(exclude="data")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude="data")
@Table(indexes = {
		@Index(name="idx_gr_size_md5", columnList="size, md5Hash", unique=true) //Lets hope we never have a false hash match
	})
public class File implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Version
	private int version;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Lob
	private byte[] data;
	
	private long size;
	
	private String md5Hash;
	
	private String mimeType;
	
	private String name;
}
