package lithium.service.access.provider.iovation.data;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlackboxMetaData {
	/**
	 * Age of the blackbox, in seconds.
	 */
	private Integer age;
	/**
	 * Date / time the blackbox was created.
	 */
	@DateTimeFormat(iso=ISO.DATE_TIME)
	private Date timestamp;
}
