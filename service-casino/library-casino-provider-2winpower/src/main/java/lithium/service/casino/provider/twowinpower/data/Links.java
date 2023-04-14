package lithium.service.casino.provider.twowinpower.data;

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
public class Links {
	private Href self;
	private Href next;
	private Href prev;
	private Href last;
	private Href first;
}
