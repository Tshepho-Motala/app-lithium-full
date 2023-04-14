package lithium.service.gateway.client.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GatewayExchangeResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String target; //The mq room name the gateways will dispatch the event to
	
	private String event; //The event name that is being dispatched
	
	private String data; // json payload data
}
