package lithium.service.casino.client.objects.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Response implements Serializable {
	private static final long serialVersionUID = 1L;
	private String result;
	private String code;
	private String description;
	private Integer errorCode;
}