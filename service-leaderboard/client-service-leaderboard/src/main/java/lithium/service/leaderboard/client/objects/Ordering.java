package lithium.service.leaderboard.client.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Ordering {
	ORDERING_MORE_IS_BETTER(1, "more"),
	ORDERING_LESS_IS_BETTER(2, "less");
	
	@Setter
	@Accessors(fluent = true)
	private Integer id;
	
	@Getter
	@Setter
	@Accessors(fluent = true)
	private String ordering;
	
	@JsonValue
	public Integer id() {
		return id;
	}
	
	@JsonCreator
	public static Ordering fromId(Integer id) {
		for (Ordering g : Ordering.values()) {
			if (g.id == id) {
				return g;
			}
		}
		return null;
	}
	
	@JsonCreator
	public static Ordering fromOrdering(String ordering) {
		for (Ordering g : Ordering.values()) {
			if (g.ordering.equalsIgnoreCase(ordering)) {
				return g;
			}
		}
		return null;
	}
}