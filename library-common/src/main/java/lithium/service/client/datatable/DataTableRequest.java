package lithium.service.client.datatable;

import java.io.Serializable;

import org.springframework.data.domain.PageRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class DataTableRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private PageRequest pageRequest;
	private String echo;
	private String searchValue;
}