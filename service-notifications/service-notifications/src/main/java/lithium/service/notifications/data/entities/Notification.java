package lithium.service.notifications.data.entities;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(indexes = {
		@Index(name="idx_domain", columnList="domain_id, name", unique=true),
		@Index(name="idx_created", columnList="createdDate", unique=false)
})
public class Notification implements Serializable {
	@Serial
	private static final long serialVersionUID = 1914717619584233376L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Domain domain;

	@Column(nullable=false)
	private Date createdDate;

	@Column(nullable=false)
	private String name;

	@Column(nullable=false)
	private String displayName;

	@Column(nullable=true)
	private String description;

	@Column(nullable=false, length=1000000)
	private String message;

	@OneToMany(fetch=FetchType.EAGER, mappedBy="notification", cascade=CascadeType.ALL)
	@Fetch(FetchMode.SELECT)
	@JsonManagedReference("notification")
	private List<NotificationChannel> channels;

	@Column(nullable=false)
	private Boolean systemNotification;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "notification_type_id")
	private NotificationType notificationType;

	@PrePersist
	public void prePersist() {
		if (createdDate == null) createdDate = new Date();

		if(systemNotification == null) {
			systemNotification = false;
		}
	}
}
