package lithium.service.notifications.data.entities;

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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(indexes = {
	@Index(name="idx_name", columnList="name", unique=true)
})
public class Channel implements Serializable {

	@Serial
	private static final long serialVersionUID = -2446153998239618023L;

	// These needs to move to lithium.service.notifications.client.enums.Channel
	// to enable auto-notification registration on channels from other microservices
	public static final String CHANNEL_SMS = "SMS";
	public static final String CHANNEL_EMAIL = "EMAIL";
	public static final String CHANNEL_POPUP = "POPUP";
	public static final String CHANNEL_PUSH = "PUSH";
	public static final String CHANNEL_PULL = "PULL";

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Version
	private int version;
	
	@Column(nullable=false)
	private String name;
}
