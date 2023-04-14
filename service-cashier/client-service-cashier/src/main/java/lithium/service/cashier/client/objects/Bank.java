package lithium.service.cashier.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bank implements Serializable, Comparable<Bank> {
	private String name;
	private String code;

	@Override
	public int compareTo(Bank o) {
		return this.getName().toLowerCase().compareTo(o.getName().toLowerCase());
	}
}
