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
import javax.persistence.Table;

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
@ToString(exclude="inbox")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude="inbox")
@Table(indexes = {
	@Index(name="idx_inbox", columnList="inbox_id", unique=false),
	@Index(name="idx_inbox_key", columnList="inbox_id, key", unique=true)
})
public class InboxMessagePlaceholderReplacement implements Serializable {
	@Serial
	private static final long serialVersionUID = -7279109222400983468L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@JsonBackReference("inbox")
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Inbox inbox;
	
	@Column(name="`key`",nullable=false)
	private String key;
	
	@Column(name="`value`",nullable=true,length=1000000)
	private String value;
}
