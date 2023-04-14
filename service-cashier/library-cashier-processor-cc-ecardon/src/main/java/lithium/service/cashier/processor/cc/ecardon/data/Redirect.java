package lithium.service.cashier.processor.cc.ecardon.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@JsonIgnoreProperties(ignoreUnknown = true)
public class Redirect {
	private String url; // https://test.ppipe.net/connectors/demo/simulator.link?sessionID=1640A18BA19CD1B965AA605BE9F358A9.sbg-vm-con02&ndcid=8a82941852cad0530152cfa454bb0a42_a0519273bba249229546b770d5e95cc7
	private String method; //"POST",
	private List<Parameters> parameters;
}