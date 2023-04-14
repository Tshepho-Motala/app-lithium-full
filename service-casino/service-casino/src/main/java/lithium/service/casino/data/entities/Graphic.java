package lithium.service.casino.data.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude={"image"})
@Table(indexes = {
	@Index(name="idx_gr_size_md5", columnList="size, md5Hash", unique=true) //Lets hope we never have a false hash match
})
public class Graphic implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Version
	private int version;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Lob
	private byte[] image;
	
	private long size;
	
	private String md5Hash;
	
	private boolean deleted;
	
	private String fileType;
}