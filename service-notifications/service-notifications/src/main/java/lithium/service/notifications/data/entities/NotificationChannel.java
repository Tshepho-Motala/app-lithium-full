package lithium.service.notifications.data.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
@ToString(exclude="notification")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude="notification")
@Table(indexes = {
	@Index(name="idx_notification_channel", columnList="notification_id, channel_id", unique=true)
})
public class NotificationChannel implements Serializable {
	@Serial
	private static final long serialVersionUID = 4312665891278847607L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Version
	private int version;
	
	@JsonBackReference("notification")
	@ManyToOne(fetch=FetchType.EAGER)
	private Notification notification;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Channel channel;
	
	@Column(nullable=false)
	private Boolean forced;
	
	@Column(nullable=false)
	private String templateName;
	
	@Column(nullable=false)
	private String templateLang;
	
	@PrePersist
	public void prePersist() {
		if (forced == null) forced = false;
	}
}
