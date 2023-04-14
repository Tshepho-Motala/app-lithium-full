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
public enum Type {
	TYPE_HANDS(1, "hands"),
	TYPE_WIN(2, "win"),
	TYPE_WAGERED(3, "wagered");
	
	@Setter
	@Accessors(fluent = true)
	private Integer id;
	
	@Getter
	@Setter
	@Accessors(fluent = true)
	private String type;
	
	@JsonValue
	public Integer id() {
		return id;
	}
	
	@JsonCreator
	public static Type fromType(String type) {
		for (Type g : Type.values()) {
			if (g.type.equalsIgnoreCase(type)) {
				return g;
			}
		}
		return null;
	}
	
	@JsonCreator
	public static Type fromId(Integer id) {
		for (Type g : Type.values()) {
			if (g.id == id) {
				return g;
			}
		}
		return null;
	}
}