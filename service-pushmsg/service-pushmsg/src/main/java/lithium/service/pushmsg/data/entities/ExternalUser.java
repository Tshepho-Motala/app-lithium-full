package lithium.service.pushmsg.data.entities;

import java.io.Serializable;
import java.util.Date;

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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lithium.service.pushmsg.client.objects.ExternalUser.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
@ToString(exclude="user")
@EqualsAndHashCode(exclude="user")
@Table(indexes = {
	@Index(name="idx_externaluser_uuid", columnList="uuid, user_id", unique=true)
})
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class ExternalUser implements Serializable {
	private static final long serialVersionUID = -7476687568705267941L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Version
	int version;
	
	@Column(nullable=false)
	private String uuid;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	@JsonBackReference
	private User user;
	
	private Long sessionCount; // 5,
	private Language language; //": "en",
	private String deviceOs; //": "72",
	private Integer deviceType; //": 5,
	private String deviceModel; //": "MacIntel",
	private Date lastActive; //": 1551074028,
	private Date createdAt; //": 1550764356,
	private String ip; //: "196.22.242.138",
	
	public String deviceTypeStr() {
		log.info("Type : "+DeviceType.fromId(deviceType).type());
		return DeviceType.fromId(deviceType).type();
	}
}