package lithium.service.pushmsg.client.objects;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Domain implements Serializable {
	private static final long serialVersionUID = -6874169154744091697L;
	
	private String name;
}