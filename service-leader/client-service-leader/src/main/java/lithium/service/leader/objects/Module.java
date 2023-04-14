package lithium.service.leader.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Module implements Serializable {
	private static final long serialVersionUID = -3467654540857049470L;

	private long id;
	private int version;
	private String name;
}
