package lithium.service.cashier.client.objects.autowithdrawal;

import com.fasterxml.jackson.annotation.JsonInclude;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleFieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AutoWithdrawalRuleField {
	@Data
	@Builder
	@AllArgsConstructor
	static public class Option {
		private String id;
		private String name;

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Option option = (Option) o;
			return Objects.equals(name, option.name);
		}

		@Override
		public int hashCode() {
			return Objects.hash(name);
		}
	}
	private Integer id;
	private String key;
	private String field;
	private String displayName;
	private AutoWithdrawalRuleFieldType type;
	private List<Option> options;
	private String description;
}
