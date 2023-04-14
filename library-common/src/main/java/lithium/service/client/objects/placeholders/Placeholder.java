package lithium.service.client.objects.placeholders;

import lombok.Getter;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

public class Placeholder {
	@Getter
	private String key;
	@Getter
	private String value;

	Placeholder(){}
	
	//Don't make it public to avoid call from other packages
	Placeholder(String key, String value) {
		this.key = key;
		this.value = Optional.ofNullable(value).orElse(key);
	}

	//Don't make it public to avoid call from other packages
	Placeholder(String key, Optional<String> optional) {
		this.key = key;
		this.value = optional.orElse(key);
	}

	//Don't make it public to avoid call from other packages
	Placeholder(String key, Long value) {
		this.key = key;
		this.value = asString(value);
	}

	//Don't make it public to avoid call from other packages
	Placeholder(String key, Integer value) {
		this.key = key;
		this.value = asString(value);
	}

	//Don't make it public to avoid call from other packages
	Placeholder(String key, Date date) {
		this.key = key;
		this.value = Optional.ofNullable(date).map(Date::toString).orElse(key);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Placeholder that = (Placeholder) o;
		return key.equals(that.key);
	}

	@Override
	public int hashCode() {
		return Objects.hash(key);
	}

	@Override
	public String toString() {
		return "{" + key + ":" + value + "}";
	}

	private static String asString(Object value) {
		return Optional.ofNullable(value).map(String::valueOf).orElse(null);
	}
}