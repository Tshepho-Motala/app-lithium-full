package lithium.service.translate.client.objects;

import org.springframework.core.io.Resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChangeSet {

	String lang;
	String name;
	String changeReference;
	Resource resource;
	String checksum;
	Date lastUpdated;
}
